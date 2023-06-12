package commands;

import arguments.ArgumentReader;
import arguments.LocationArguments;
import com.fasterxml.jackson.core.JsonProcessingException;
import constants.Messages;
import elements.Location;
import logic.Manager;

public class GreaterLocationCommand extends AbstractCommand {
    private int count;
    public GreaterLocationCommand() {
    }

    public GreaterLocationCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new LocationArguments());
    }

    @Override
    public void execute() {
        try {
            count = manager.countGreaterThanLocation(mapper.readValue(reader.getArgument(), Location.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.greater_location");
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.count", count);
    }

    @Override
    public String getName() {
        return "count_greater_than_location";
    }
}
