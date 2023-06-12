package commands;

import java.util.Map.Entry;

import arguments.ArgumentReader;
import arguments.StringPersonArguments;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import constants.Messages;
import elements.Person;
import logic.Manager;


public class InsertCommand extends AbstractCommand {
    @JsonIgnore
    private String key;
    @JsonIgnore
    private Person person;
    public InsertCommand() {
    }
    public InsertCommand(Manager manager) {
        super(manager);
    }

    {
        reader = new ArgumentReader(new StringPersonArguments());
    }

    @Override
    public void execute() {
        try {
            Entry<String, Person> entry = mapper.readValue(reader.getArgument(), new TypeReference<Entry<String, Person>>() {});
            key = entry.getKey();
            person = entry.getValue();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        manager.put(key, person);
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message.format.added", key);
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.insert");
    }
}
