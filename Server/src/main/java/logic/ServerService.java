package logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import commands.Command;
import commands.SaveCommand;
import constants.Constants;
import constants.Messages;
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

    public ServerService(String fileName) throws StartingProblemException, NonUniqueIdException {
        Manager manager = new CollectionManager(fileName);
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

    private void getQuery(SelectionKey key) throws IOException, ClassNotFoundException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Constants.BUFFER_SIZE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        while ((bytesRead = client.read(buffer)) > 0) {
            buffer.flip();
            byte[] bytes = new byte[bytesRead];
            buffer.get(bytes, 0, bytesRead);
            out.write(bytes);
        }
        if (bytesRead == -1) {
            return;
        }
        byte[] queryBytes = out.toByteArray();

        Query query = mapper.readValue(queryBytes, Query.class);
        Response response = handleQuery(query);

        key.attach(response);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private Response handleQuery(Query query) {
        Command command = builder.build(query);
        command.execute();
        builder.logCommand(command);
        return new Response(command.getReport());
    }

    /**
     * После обработки запроса сервер отсылает обратно прикрепленный
     * ранее отчет об исполнении
     */
    private void sendResponse(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        Response response = (Response) key.attachment();
        ByteBuffer responseBytes = ByteBuffer.wrap(mapper.writeValueAsBytes(response));
        client.write(responseBytes);

        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * После инициализации соединения сервер отправляет клиенту список
     * необходимых аргументов для каждой команды, который клиент будет
     * использовать для считывания данных и их валидации на своей стороне
     *
     * @param key отобранный ключ подключения
     */
    private void sendInfo(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        byte[] mapBytes = mapper.writeValueAsBytes(builder.getArguments());

        ByteBuffer buffer = ByteBuffer.wrap(mapBytes);
        client.write(buffer);

        System.out.println(Messages.getMessage("message.format.new_connection", client.getRemoteAddress()));
        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * Метод инициализирует основные потоки, регистрирует ключи
     * и подготавливает сервер к отправке приветственных данных
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
                    if (key.isReadable()) getQuery(key);
                    if (key.isWritable()) {
                        if (key.attachment() instanceof Response) sendResponse(key);
                        else sendInfo(key);
                    }
                }
            }
        } catch (CloseConnectionSignal ignored) {
        } catch (IOException | ClassNotFoundException e) {
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
            Service service = new ServerService(Constants.HOST_FILE_PATH);
            service.run();
        } catch (StartingProblemException | NonUniqueIdException e) {
            System.out.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(Messages.getMessage("warning.file_argument"));
        }
    }
}