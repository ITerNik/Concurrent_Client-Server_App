package commands;

import arguments.ArgumentReader;
import arguments.LocationArguments;
import com.fasterxml.jackson.core.JsonProcessingException;
import constants.Messages;
import elements.Location;
import elements.Person;
import logic.Manager;

import java.util.ArrayList;

public class FilterByLocationCommand extends AbstractCommand {

    private ArrayList<Person> selected;
    public FilterByLocationCommand() {
    }
    public FilterByLocationCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new LocationArguments());
    }

    @Override
    public void execute() {
        try {
            selected = manager.filterByLocation(mapper.readValue(reader.getArgument(), Location.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.filter_by_location");
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.found", selected);
    }

    @Override
    public String getName() {
        return "filter_by_location";
    }
}
