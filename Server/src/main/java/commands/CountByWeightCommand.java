package commands;

import arguments.ArgumentReader;
import arguments.WeightArguments;
import constants.Messages;

import logic.Manager;

public class CountByWeightCommand extends AbstractCommand {
    private int count;

    public CountByWeightCommand() {
    }

    public CountByWeightCommand(Manager manager) {
        super(manager);
    }
    {
        reader = new ArgumentReader(new WeightArguments());
    }

    @Override
    public void execute() {
        count = manager.countByWeight(Double.parseDouble(reader.getArgument()));
    }

    @Override
    public String getName() {
        return "count_by_weight";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.count", count);
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.count_by_weight");
    }
}
