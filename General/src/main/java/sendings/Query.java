package sendings;

import arguments.ArgumentReader;

public class Query {
    public Query(String commandName, ArgumentReader arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }
    public Query() {
    }

    private String commandName;
    private ArgumentReader arguments;

    public ArgumentReader getArguments() {
        return arguments;
    }
    public String getCommandName() {
        return commandName;
    }
}
