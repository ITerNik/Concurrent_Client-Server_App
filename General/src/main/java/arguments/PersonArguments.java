package arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import elements.Person;
import logic.IODevice;

/**
 * Представляет класс для считывания экземпляра класса {@link Person}
 * с валидацией значений полей
 */

public class PersonArguments implements Readable {
    public PersonArguments() {
    }
    @Override
    public String read(IODevice io) throws JsonProcessingException {
         return mapper.writerFor(Person.class).writeValueAsString(io.readElement(Person.class));
    }
}
