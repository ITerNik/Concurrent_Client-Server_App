package commands;

import arguments.ArgumentReader;
import arguments.KeyArguments;
import constants.Messages;
import logic.Manager;

import java.util.ArrayList;

public class RemoveGreaterCommand extends AbstractCommand {
    private ArrayList<String> removed;
    public RemoveGreaterCommand() {
    }
    public RemoveGreaterCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new KeyArguments());
    }

    @Override
    public void execute() {
        removed = manager.removeGreater(reader.getArgument());
    }

    @Override
    public String getName() {
        return "remove_greater_key";
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
        return Messages.getMessage("command.remove_greater");
    }
}
