package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import logic.Manager;

import java.util.HashMap;

public class HelpCommand extends AbstractCommand {
    private HashMap<String, Command> commandList = new HashMap<>();
    private final StringBuilder report = new StringBuilder();
    public HelpCommand() {
    }

    public HelpCommand(HashMap<String, Command> commandList, Manager manager) {
        super(manager);
        this.commandList = commandList;
    }
    {
        reader = new ArgumentReader(new NoReadableArguments());
    }

    @Override
    public void execute() {
        for (Command command : commandList.values()) {
            report.append(String.format("%s%s: %s\n", command.getName(), command.argumentsInfo(), command.getInfo()));
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getReport() {
        return report.toString();
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.help");
    }
}
