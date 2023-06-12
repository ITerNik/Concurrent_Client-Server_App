package commands;

import arguments.ArgumentReader;
import arguments.NoReadableArguments;
import constants.Messages;
import logic.Manager;

import java.util.Queue;

public class HistoryCommand extends AbstractCommand {
    private Queue<Command> commandHistory = null;
    private StringBuilder report = new StringBuilder();
    public HistoryCommand() {
    }

    public HistoryCommand(Queue<Command> commandHistory, Manager manager) {
        super(manager);
        this.commandHistory = commandHistory;
    }
    {
        reader = new ArgumentReader(new NoReadableArguments());
    }

    @Override
    public void execute() {
        if (commandHistory.isEmpty()) {
            report = new StringBuilder(Messages.getMessage("message.no_completed"));
        } else {
            for (Command command : commandHistory) {
                report.append("\n").append(command.getName());
            }
            report = new StringBuilder(Messages.getMessage("message.completed")).append(report.toString());
        }
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getReport() {
        return report.toString();
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.history");
    }
}
