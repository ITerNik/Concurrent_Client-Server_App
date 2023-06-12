package logic;

import arguments.ArgumentReader;
import commands.*;
import constants.Messages;
import exceptions.NoSuchCommandException;
import sendings.Query;

import java.util.*;

public class CommandBuilder {
    private final HashMap<String, Command> clientCommandList = new HashMap<>(),
    serverCommandList = new HashMap<>();

    private final HashMap<String, ArgumentReader> commandInfo = new HashMap<>();
    private ArrayList<String> fileLog;
    private final Queue<Command> commandLog = new ArrayDeque<>() {
        @Override
        public boolean add(Command command) {
            if (size() >= 8) remove();
            return super.add(command);
        }
    };
    private final Manager manager;

    public CommandBuilder(Manager manager) {
        this.manager = manager;
        initialize();
    }

    public CommandBuilder(Manager manager, ArrayList<String> fileHistory) {
        this.manager = manager;
        this.fileLog = fileHistory;
        initialize();
    }

    public void addCommand(Command... commands) {
        for (Command command : commands) {
            String commandName = command.getName();
            clientCommandList.put(commandName, command);
            commandInfo.put(commandName, command.getReader());
        }
    }

    private void initialize() {
        addCommand(new ExitCommand(manager), new ClearCommand(), new TestCommand(),
                new InfoCommand(manager), new ShowCommand(manager), new InsertCommand(manager),
                new RemoveKeyCommand(manager), new UpdateIdCommand(),
                new RemoveLowerCommand(manager), new HistoryCommand(commandLog, manager), new RemoveGreaterCommand(manager),
                new HelpCommand(clientCommandList, manager), new CountByWeightCommand(manager), new GreaterLocationCommand(manager),
                new FilterByLocationCommand(manager), new ExecuteScriptCommand(manager, commandInfo));
        serverCommandList.put("save", new SaveCommand(manager));
        serverCommandList.put("exit", new ExitCommand(manager));
    }

    public Command build(Query query) {
        return clientCommandList.get(query.getCommandName()).setArguments(query.getArguments());
    }
    public Command build(String name) {
        Command command = serverCommandList.get(name);
        if (command == null) throw new NoSuchCommandException(
                Messages.getMessage("warning.format.no_such_command", name));
        return command;
    }
    public Command get(String name) {
        return clientCommandList.getOrDefault(name, new InfoCommand(manager));
    }

    public boolean logFile(String fileName) {
        if (!fileLog.contains(fileName)) {
            fileLog.add(fileName);
            return true;
        }
        return false;
    }
    public void logCommand(Command command) {
        commandLog.add(command);
    }

    public HashMap<String, ArgumentReader> getArguments() {
        return commandInfo;
    }

}