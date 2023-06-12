package commands;

import arguments.ArgumentReader;
import arguments.FileArguments;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import logic.CommandBuilder;
import logic.Manager;
import constants.Messages;
import sendings.Query;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;


public class ExecuteScriptCommand extends AbstractCommand {

    private final StringBuilder report = new StringBuilder();

    public ExecuteScriptCommand(Manager manager, HashMap<String, ArgumentReader> commandInfo) {
        super(manager);
        reader = new ArgumentReader(new FileArguments(commandInfo));
    }

    @Override
    public void execute() {
        CommandBuilder builder = new CommandBuilder(manager);
        try {
            ArrayList<String> queries = mapper.readValue(reader.getArgument(), new TypeReference<ArrayList<String>>() {});
            for (String query : queries) {
                Command current = builder.build(mapper.readValue(query, Query.class));
                current.execute();
                builder.logCommand(current);
                report.append(String.format(Messages.getMessage("message.format.execute_result") + "\n", current.getName()))
                        .append(current.getReport());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getReport() {
        return report.toString();
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.execute_script");
    }
}