package arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import logic.IODevice;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Класс представляет считыватель аргументов команд
 */
public class ArgumentReader {
    private String argument;

    private Readable reader;

    public ArgumentReader() {
    }

    public ArgumentReader(Readable reader) {
        this.reader = reader;
    }

    /**
     * Считывает аргументы с использованием устройства ввода-вывода.
     * Переопределяется в зависимости от установленной стратегии, которая
     * зависит от рконкретной реализации метода {@link Readable#read}
     *
     * @param io устройство ввода-вывода для чтения из консоли/файла
     */
    public void read(IODevice io) {
        try {
            argument = reader.read(io);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    /**
     * Возвращает считанный аргумент, сериализованный в JSON строку при помощи {@link ObjectMapper}.
     * Требует десериализации на сервере
     *
     * @return считанный аргумент в формате JSON
     */
    public String getArgument() {
        return argument;
    }
    public Readable getReader() {
        return reader;
    }
}
