package arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import elements.Person;
import logic.IODevice;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * Класс представляет считыватель ключа-значения преимущественно для заполнения коллекции
 */
public class StringPersonArguments implements Readable {
    public StringPersonArguments(){
    }
    @Override
    public String read(IODevice io) throws JsonProcessingException {
        String input = io.read();
        io.readLine();
        Entry<String, Person> entry = new SimpleEntry<>(input, io.readElement(Person.class));
        return mapper.writerFor(new TypeReference<Entry<String, Person>>() {}).writeValueAsString(entry);
    }
}
