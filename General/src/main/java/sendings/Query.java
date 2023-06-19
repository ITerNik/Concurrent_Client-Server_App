package sendings;

import arguments.ArgumentReader;
import elements.User;

public class Query {
    public Query(String commandName, ArgumentReader arguments, User user) {
        this.commandName = commandName;
        this.arguments = arguments;
        this.user = user;
    }
    public Query() {
    }
    private User user;

    private String commandName;
    private ArgumentReader arguments;

    public ArgumentReader getArguments() {
        return arguments;
    }
    public String getCommandName() {
        return commandName;
    }

    public User getUser() {
        return user;
    }
}
