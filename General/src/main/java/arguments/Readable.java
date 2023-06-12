package arguments;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import logic.IODevice;

/**
 * Интерфейс представляет считыватель аргументов для клиентской стороны приложения.
 * Аргументы будут считываться в соответствии с конкретной реализацией, необходимой для команды
 * */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StringPersonArguments.class, name = "StringPersonArguments"),
        @JsonSubTypes.Type(value = IntegerPersonArguments.class, name = "IntegerPersonArguments"),
        @JsonSubTypes.Type(value = KeyArguments.class, name = "KeyArguments"),
        @JsonSubTypes.Type(value = PersonArguments.class, name = "PersonArguments"),
        @JsonSubTypes.Type(value = NoReadableArguments.class, name = "NoReadableArguments"),
        @JsonSubTypes.Type(value = WeightArguments.class, name = "WeightArguments"),
        @JsonSubTypes.Type(value = LocationArguments.class, name = "LocationArguments"),
        @JsonSubTypes.Type(value = FileArguments.class, name = "FileArguments")
})
public interface Readable {
    /**
     * Локальный класс для вложенной сериализации экземпляров и коллекций
     */
    ObjectMapper mapper = new ObjectMapper();
    String read(IODevice from) throws JsonProcessingException;
}
