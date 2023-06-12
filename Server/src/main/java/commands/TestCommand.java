package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import logic.Manager;


public class TestCommand extends AbstractCommand {
    public TestCommand() {

    }
    public TestCommand(Manager manager) {
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
        return "test";
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.test");
    }
}
