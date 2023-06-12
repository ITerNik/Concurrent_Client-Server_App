package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import logic.Manager;

public class InfoCommand extends AbstractCommand {
    public InfoCommand() {
    }

    public InfoCommand(Manager manager) {
        super(manager);
    }
    {
       reader = new ArgumentReader(new NoReadableArguments());
    }

    @Override
    public void execute() {
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getReport() {
        return manager.getInfo();
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.info");
    }
}
