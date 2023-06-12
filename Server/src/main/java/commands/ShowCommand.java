package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import logic.Manager;

public class ShowCommand extends AbstractCommand {

    public ShowCommand() {
    }

    {
        reader = new ArgumentReader(new NoReadableArguments());
    }

    public ShowCommand(Manager manager) {
        super(manager);
    }

    @Override
    public void execute() {
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getReport() {
        return manager.toString();
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.show");
    }
}
