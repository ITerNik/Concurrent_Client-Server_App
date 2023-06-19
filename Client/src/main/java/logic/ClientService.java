package logic;

import arguments.ArgumentReader;
import arguments.FileArguments;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.Constants;
import constants.Messages;
import elements.User;
import exceptions.BadConnectionException;
import exceptions.BadParametersException;
import sendings.Query;
import sendings.Response;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Класс представляет сервис клиента для взаимодействия с сервером
 */
public class ClientService implements Service {
    private final CliDevice cio;
    private User user;
    private InputStream inputStream;
    private OutputStream outputStream;
    private HashMap<String, ArgumentReader> commandInfo;

    ObjectMapper mapper = new ObjectMapper();

    {
        mapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    }

    public ClientService(CliDevice cio) throws BadConnectionException {
        this.cio = cio;
        initConnection();
        authorize();
    }

    /**
     * Отправляет запрос на сервер и получает ответ, который выводится в консоль.
     * Необходимые аргументы для команды берутся из списка {@link this#commandInfo}
     * и читаются с помощью устройства ввода/вывода перед созданием запроса
     *
     * @throws BadConnectionException если не удается установить соединение с сервером
     */
    public void sendQuery() throws BadConnectionException {
        while (true) {
            cio.write(Messages.getMessage("input.command"));
            String commandName = cio.read();
            if (commandName.equals("exit")) break;
            if (!commandInfo.containsKey(commandName)) {
                System.out.println(Messages.getMessage("warning.format.no_such_command", commandName));
                continue;
            }
            ArgumentReader arguments = commandInfo.get(commandName);
            try {
                arguments.read(cio);

                Query query = new Query(commandName, arguments, user);
                outputStream.write(mapper.writeValueAsBytes(query));
                outputStream.flush();

                Response response = mapper.readValue(inputStream, Response.class);
                System.out.println(response.getReport());
            } catch (BadParametersException e) {
                System.out.println(e.getMessage());
            } catch (SocketException e) {
                initConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void authorize() throws BadConnectionException {
        AuthorizationDevice auth = new AuthorizationDevice();
        boolean authorized = auth.getUserStatus();
        while (true) {
            if (authorized) user = auth.authorizeUser();
            else user = auth.registerUser();
            try {
                outputStream.write(mapper.writeValueAsBytes(user));
                outputStream.flush();

                Response response = mapper.readValue(inputStream, Response.class);
                if (response.getReport().equals(Constants.OK_STATUS)) break;
                System.out.println(response.getReport());
            } catch (SocketException e) {
                initConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Запускает клиентский сервис
     */
    public void run() {
        try {
            sendQuery();
        } catch (BadConnectionException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchElementException ignore) {
        } finally {
            closeConnection();
        }
    }

    /**
     * Закрывает соединение с сервером после выхода из программы
     * или при неудачной попытке соединения
     */
    public void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            System.out.println(Messages.getMessage("message.connection_closed"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Инициализирует соединение с сервером. Повторяет процесс 3 раза
     * с перерывом в 5 секунд в случае неудачи до момента повторного
     * или окончательного разрыва соединения.
     * При установке соединения сервер передает на сторону клиента список
     * аргументов {@link this#commandInfo} с функциями чтения и валидации
     * необходимых для функционирования команд данных
     *
     * @throws BadConnectionException если сервер не отвечает
     */
    public void initConnection() throws BadConnectionException {
        int retry = 1;
        for (; retry <= Constants.MAX_RETRY; retry++) {
            try {
                Socket socket = new Socket(Constants.CLIENT_HOST, Constants.PORT);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                if (commandInfo == null) {
                    commandInfo = mapper.readValue(inputStream, new TypeReference<>() {});
                    commandInfo.put("execute_script", new ArgumentReader(new FileArguments(commandInfo)));
                }
                break;
            } catch (SocketException e) {
                System.out.println(Messages.getMessage("message.connection_waiting"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Constants.RETRY_DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (retry > Constants.MAX_RETRY) {
            throw new BadConnectionException(Messages.getMessage("message.server_unavailable"));
        }
        cio.write(Messages.getMessage("message.welcome"));
    }

    /**
     * Точка начала работы клиентского приложения
     */
    public static void main(String[] args) {

        try (CliDevice cio = new CliDevice()) {
            Service client = new ClientService(cio);
            client.run();
        } catch (BadConnectionException e) {
            System.out.println(e.getMessage());
        }
    }
}