package logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import commands.Command;
import commands.SaveCommand;
import constants.Constants;
import constants.Messages;
import elements.User;
import exceptions.CloseConnectionSignal;
import exceptions.NoSuchCommandException;
import exceptions.NonUniqueIdException;
import exceptions.StartingProblemException;
import sendings.Query;
import sendings.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс представляет собой серверную службу, отвечающую за обработку запросов клиентов.
 * Работает в неблокирующем режиме
 */
public class ServerService implements Service {
    private ServerSocketChannel ssChannel;
    private Selector selector;
    private final CommandBuilder builder;

    private final Scanner input = new Scanner(System.in);

    private final ObjectMapper mapper = new ObjectMapper();
    ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public ServerService() throws StartingProblemException, NonUniqueIdException {
        Manager manager = new DBManager(Constants.ADMIN_LOGIN, Constants.ADMIN_PASSWORD);
        this.builder = new CommandBuilder(manager);
        initConnection();
    }

    /**
     * Метод инициализации соединения сервера открывает каналы и
     * устанавливает порты для прослушивания
     */
    public void initConnection() {
        System.out.println(Messages.getMessage("message.initializing"));
        try {
            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(false);
            ssChannel.socket().bind(new InetSocketAddress(Constants.SERVER_HOST, Constants.PORT));
            selector = Selector.open();
            ssChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getQuery(SelectionKey key) {
        key.interestOpsAnd(~(SelectionKey.OP_READ));
        new QueryHandleTread(key).start();
    }

    private class QueryHandleTread extends Thread {
        private final SelectionKey key;

        public QueryHandleTread(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            Response response;
            byte[] queryBytes = readBytes(key);
            if (queryBytes == null) return;
            try {
                Query query = mapper.readValue(queryBytes, Query.class);
                response = handleQuery(query);
            } catch (IOException e) {
                response = new Response(Messages.getMessage("warning.deserialization_error"));
            }

            key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);
        }

        private Response handleQuery(Query query) {
            Command command;
            synchronized (builder) {
                command = builder.build(query);
            }
            command.execute();
            builder.logCommand(command);
            return new Response(command.getReport());
        }
    }

    private byte[] readBytes(SelectionKey key) {
        ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        SocketChannel client = (SocketChannel) key.channel();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        try {
            while ((bytesRead = client.read(buffer)) > 0) {
                buffer.flip();
                byte[] bytes = new byte[bytesRead];
                buffer.get(bytes, 0, bytesRead);
                out.write(bytes);
            }
        } catch (IOException e) {
            return null;
        }
        if (bytesRead == -1) {
            return null;
        }
        return out.toByteArray();
    }

    /**
     * После обработки запроса сервер отсылает обратно прикрепленный
     * ранее отчет об исполнении
     */
    private void sendResponse(SelectionKey key) {
        key.interestOpsAnd(~(SelectionKey.OP_WRITE));
        new ResponseSendThread(key).start();
    }

    private class ResponseSendThread extends Thread {
        private final SelectionKey key;

        public ResponseSendThread(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            SocketChannel client = (SocketChannel) key.channel();
            Response response = (Response) key.attachment();
            try {
                ByteBuffer responseBytes = ByteBuffer.wrap(mapper.writeValueAsBytes(response));
                client.write(responseBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            key.interestOps(SelectionKey.OP_READ);
            if (!response.getReport().equals(Constants.OK_STATUS)) key.attach(null);
        }
    }

    public void handleNewUser(SelectionKey key) {
        key.interestOpsAnd(~(SelectionKey.OP_READ));
        threadPool.execute(() -> {
            byte[] userBytes = readBytes(key);
            if (userBytes == null) return;
            Response response;
            try {
                User user = mapper.readValue(userBytes, User.class);
                response = builder.checkUser(user);
            } catch (IOException e) {
                response = new Response("Ошибка обработки запроса");
                e.printStackTrace();
            }
            key.interestOps(SelectionKey.OP_WRITE);
            key.attach(response);
        });
    }

    /**
     * После инициализации соединения сервер отправляет клиенту список
     * необходимых аргументов для каждой команды, который клиент будет
     * использовать для считывания данных и их валидации на своей стороне
     *
     * @param key отобранный ключ подключения
     */
    private void sendInfo(SelectionKey key) throws IOException {
        key.interestOpsAnd(~(SelectionKey.OP_WRITE));
        new InfoSendThread(key).start();
    }

    private class InfoSendThread extends Thread {
        private final SelectionKey key;

        public InfoSendThread(SelectionKey key) {
            this.key = key;
        }

        @Override
        public void run() {
            SocketChannel client = (SocketChannel) key.channel();
            try {
                byte[] mapBytes = mapper.writeValueAsBytes(builder.getArguments());

                ByteBuffer buffer = ByteBuffer.wrap(mapBytes);
                client.write(buffer);


                System.out.println(Messages.getMessage("message.format.new_connection", client.getRemoteAddress()));
                key.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                key.cancel();
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод инициализирует основные потоки, регистрирует ключи
     * и подготавливает сервер к отправке приветственных данных
     *
     * @param key отобранный ключ подключения
     */
    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_WRITE);
    }

    /**
     * Метод закрывает соединение с сохранением коллекции
     * в случае непредвиденной ошибки или по запросу из командной строки
     */
    private void closeConnection() {
        System.out.println(Messages.getMessage("message.server_stopped"));
        if (selector != null) {
            try {
                selector.close();
                ssChannel.socket().close();
                ssChannel.close();
                builder.get("save").execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод запускает работу сервера. Сначала отбирает активные ключи,
     * либо продолжает работу по истечению определенного времени.
     * Затем принимает подключения, принимает и исполняет запросы и
     * посылает ответы в неблокирующем режиме при помощи {@link this#acceptConnection},
     * {@link this#getQuery}, {@link this#sendResponse} и {@link this#sendInfo} соответственно
     */
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                checkInput();
                selector.select(Constants.TIMEOUT);
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                for (var iter = selectedKeys.iterator(); iter.hasNext(); ) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) acceptConnection(key);
                    if (key.isReadable())
                        if (key.attachment() == null) handleNewUser(key);
                        else getQuery(key);
                    if (key.isWritable()) {
                        if (key.attachment() instanceof Response) sendResponse(key);
                        else sendInfo(key);
                    }
                }
            }
        } catch (CloseConnectionSignal ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * Читает аргументы командной строки в промежутке между
     * обработкой запросов пользователей
     */
    private void checkInput() throws IOException {
        while (System.in.available() > 0) {
            try {
                Command command = builder.build(input.next());
                command.execute();
                System.out.println(command.getReport());
            } catch (NoSuchCommandException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Входная точка для запуска серверной службы
     */
    public static void main(String[] args) {
        try {
            Service service = new ServerService();
            service.run();
        } catch (StartingProblemException | NonUniqueIdException e) {
            System.out.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(Messages.getMessage("warning.file_argument"));
        }
    }
}