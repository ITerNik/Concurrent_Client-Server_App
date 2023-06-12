package arguments;

import constants.Messages;
import exceptions.BadParametersException;
import logic.IODevice;

/**
 * Класс представляет валидатор ввода для команд,
 * не требующих аргумента
 */
public class NoReadableArguments implements Readable {
    public NoReadableArguments() {
    }

    @Override
    public String read(IODevice io) {
        if (io.hasNext() && !io.readLine().isBlank())
            throw new BadParametersException(
                    Messages.getMessage("warning.needless_argument"));
        return "";
    }
}
