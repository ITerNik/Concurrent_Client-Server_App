package commands;

import arguments.ArgumentReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.Messages;
import logic.Manager;

/**
 * Абстрактный класс реализующий интерфейс Command и определяющий базовое поведение команд.
 * Здесь определены методы чтения и установки аргументов.
 * Все команды наследуются от этого класса и переопределяют исполнение в соответствии с требованиями.
 */
public abstract class AbstractCommand implements Command {
    protected ArgumentReader reader;
    protected ObjectMapper mapper = new ObjectMapper();
    protected Manager manager;

    public AbstractCommand() {
    }
    public AbstractCommand(Manager manager) {
        this.manager = manager;
    }

    @Override
    public Command setArguments(ArgumentReader reader) {
        this.reader = reader;
        return this;
    }

    @Override
    public ArgumentReader getReader() {
        return reader;
    }

    @Override
    public String getInfo() {
        return Messages.getMessage("command.no_information");
    }

    @Override
    public String getReport() {
        return Messages.getMessage("message_success");
    }

    @Override
    public String argumentsInfo() {
        return ""; //TODO: Add info method
    }
}
