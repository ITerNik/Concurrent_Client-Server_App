package arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import constants.Messages;
import elements.Person;
import exceptions.BadParametersException;
import logic.IODevice;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * Класс представляет считыватель аргументов ID и Person
 * преимущественно для команды UpdateID
 */
public class IntegerPersonArguments implements Readable {
    /**
     * Метод сначала проверяет вводимое значение на соответствие целому числу ID,
     * Затем построчно считывает экземпляр человека
     * @param from устройство ввода-вывода для считывания команды и аргументов
     * @return сериализованная в JSON строку пара ID => человек
     * @throws JsonProcessingException если возникла ошибка сериализации
     */
    @Override
    public String read(IODevice from) throws JsonProcessingException {
        int input;
        try {
            input = Integer.parseInt(from.read());
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_integer",
                    Messages.getMessage("parameter.id")));
        }
        from.readLine();
        Entry<Integer, Person> entry = new SimpleEntry<>(input, from.readElement(Person.class));
        return mapper.writerFor(new TypeReference<Entry<Integer, Person>>() {}).writeValueAsString(entry);
    }
}
