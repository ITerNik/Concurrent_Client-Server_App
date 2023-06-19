package arguments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import constants.Messages;
import elements.User;
import exceptions.BadParametersException;
import logic.FileDevice;
import logic.IODevice;
import sendings.Query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Класс представляет считыватель аргументов из файла
 * преимущественно для команды ExecuteScript
 */
public class FileArguments implements Readable {
    /**
     * Поле для считывания аргументов остальных команд.
     * Не сериализуется, т.к. вызывает рекурсию
     */
    @JsonIgnore
    private HashMap<String, ArgumentReader> commandInfo = new HashMap<>();
    /**
     * Поле для мониторинга повторяющихся файлов в цепочке execute_script.
     * При десериализации создается копия экземпляра данного класса, поэтому использование
     * модификатора static безопасно
     */
    private static HashSet<Path> fileLog = new HashSet<>();

    public FileArguments() {
    }

    public FileArguments(HashMap<String, ArgumentReader> commandInfo) {
        this.commandInfo = commandInfo;
    }

    /**
     * Считывает название файла и проверяет его наличие в системе.
     * Рекурсивно считывает команды с их аргументами внутри файла
     * и сериализует их в список JSON строк.
     * Требует десериализации на сервере
     *
     * @param from устройство ввода-вывода для считывания команды и аргументов
     * @return сериализованная JSON строка - список команд для исполнения
     * @throws JsonProcessingException если возникла ошибка при сериализации
     */
    @Override
    public String read(IODevice from) throws JsonProcessingException {
        Path file = Paths.get(from.read());
        ArrayList<String> commands = new ArrayList<>();
        if (Files.notExists(file)) throw new BadParametersException(
                Messages.getMessage("warning.format.file_not_found", file.getFileName()));
        if (!fileLog.add(file.toAbsolutePath())) throw new BadParametersException(Messages.getMessage("warning.cyclic_file"));
        try {
            FileDevice input = new FileDevice(file);
            while (input.hasNextLine()) {
                String commandName = input.read();
                ArgumentReader current = commandInfo.get(commandName);
                current.read(input);
                Query query = new Query(commandName, current, new User());
                commands.add(mapper.writerFor(Query.class).writeValueAsString(query));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapper.writerFor(new TypeReference<ArrayList<String>>() {
        }).writeValueAsString(commands);
    }

    /**
     * Устанавливает информацию о необходимых аргументах команды.
     * Необходима для избежания рекурсии при попытке сериализовать список аргументов
     *
     * @param commandInfo информация об аргументах, переданная со стороны сервера
     */
    public void setCommandInfo(HashMap<String, ArgumentReader> commandInfo) {
        this.commandInfo = commandInfo;
    }

    public HashMap<String, ArgumentReader> getCommandInfo() {
        return commandInfo;
    }

    public static HashSet<Path> getFileLog() {
        return fileLog;
    }
}