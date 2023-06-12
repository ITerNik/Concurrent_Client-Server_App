package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import exceptions.BadParametersException;
import logic.Manager;

import java.io.IOException;

public class SaveCommand extends AbstractCommand {
    public SaveCommand() {
    }

    public SaveCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new NoReadableArguments());
    }

    @Override
    public void execute() {
        try {
            manager.save();
        } catch (IOException e) {
            throw new BadParametersException(Messages.getMessage("warning.write_error"));
        }
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.saved");
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.save");
    }
}
