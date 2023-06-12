package commands;

import arguments.ArgumentReader;
import arguments.PersonArguments;
import com.fasterxml.jackson.core.JsonProcessingException;
import constants.Messages;
import elements.Person;
import logic.Manager;

import java.util.ArrayList;

public class RemoveLowerCommand extends AbstractCommand {
    private ArrayList<String> removed;
    public RemoveLowerCommand() {
    }
    public RemoveLowerCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new PersonArguments());
    }

    @Override
    public void execute() {
        try {
            removed = manager.removeLower(mapper.readValue(reader.getArgument(), Person.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "remove_lower";
    }

    @Override
    public String getReport() {
        if (removed.isEmpty()) {
            return Messages.getMessage("message.nothing_deleted");
        } else {
            return Messages.getMessage("message.format.deleted", removed);
        }
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.remove_lower");
    }
}
