package arguments;

import constants.Messages;
import exceptions.BadParametersException;
import logic.IODevice;

/**
 * Класс представляет считыватель веса с валидацией вещественного числа
 */
public class WeightArguments implements Readable {
    public WeightArguments() {
    }

    @Override
    public String read(IODevice io) {
        double input = 0;
        try {
            input = Double.parseDouble(io.read());
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real",
                    Messages.getMessage("parameter.weight")));
            //TODO: BadParam -> Exception
        }
        return String.valueOf(input);
    }
}
