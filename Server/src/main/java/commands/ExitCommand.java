package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import exceptions.CloseConnectionSignal;
import logic.Manager;

public class ExitCommand extends AbstractCommand {
    public ExitCommand(Manager manager) {
        super(manager);
    }

    {
        reader = new ArgumentReader(new NoReadableArguments());
    }

    @Override
    public void execute() {
        new SaveCommand(manager).execute();
        throw new CloseConnectionSignal("Соединение закрыто");
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.goodbye");
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.exit");
    }
}
