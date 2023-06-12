package commands;


import arguments.ArgumentReader;
import arguments.IntegerPersonArguments;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import constants.Messages;
import elements.Person;
import logic.Manager;

import java.util.ArrayList;

import java.util.Map.Entry;

public class UpdateIdCommand extends AbstractCommand {
    @JsonIgnore
    private int id;
    public UpdateIdCommand() {
    }

    public UpdateIdCommand(Manager manager) {
        super(manager);
    }

    {
        reader = new ArgumentReader(new IntegerPersonArguments());
    }

    @Override
    public void execute() {
        try {
            Entry<Integer, Person> entry = mapper.readValue(reader.getArgument(), new TypeReference<Entry<Integer, Person>>() {});
            id = entry.getKey();
            manager.update(manager.findById(id), entry.getValue());
        } catch (JsonProcessingException e) {
            e.printStackTrace(); //TODO catch BadParameterException
        }
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.updated", id);
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.update");
    }
}
