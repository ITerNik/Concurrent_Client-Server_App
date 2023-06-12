package commands;

import arguments.ArgumentReader;
import arguments.KeyArguments;
import constants.Messages;
import logic.Manager;

public class RemoveKeyCommand extends AbstractCommand {
    public RemoveKeyCommand() {
    }
    public RemoveKeyCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new KeyArguments());
    }


    @Override
    public void execute() {
        manager.remove(reader.getArgument());
    } //TODO: Add existing key exception

    @Override
    public String getName() {
        return "remove_key";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.success_delete", reader.getArgument());
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.remove_key");
    }
}
