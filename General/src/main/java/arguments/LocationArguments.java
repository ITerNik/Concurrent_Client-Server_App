package arguments;

import com.fasterxml.jackson.core.JsonProcessingException;
import elements.Location;
import logic.IODevice;

/**
 * Класс представляет считыватель экземпляра класса {@link Location}
 */
public class LocationArguments implements Readable {
    public LocationArguments(){
    }

    /**
     * @param io устройство ввода-вывода для считывания команды и аргументов
     * @return сериализованный в JSON строку экземпляр {@link Location}
     * @throws JsonProcessingException при ошибке сериализации
     */
    @Override
    public String read(IODevice io) throws JsonProcessingException {
         return mapper.writerFor(Location.class).writeValueAsString(io.readElement(Location.class));
    }
}
