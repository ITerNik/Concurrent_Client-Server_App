package commands;

import arguments.ArgumentReader;

import java.io.Serializable;

public interface Command extends Serializable {
    /**
     * Исполнение команды. Переопределяется в классах наследниках
     */
    void execute();
    ArgumentReader getReader();
    Command setArguments(ArgumentReader arguments);

    /**
     * Возвращает название для добавления в список команд и вывода в help
     * @see HelpCommand
     * @return название команды в консоли
     */
    String getName();
    /**
     * Возвращает описание функционала команды для вывода в help.
     * Необходимо переопределить в наследниках
     * @return описание команды
     * @see HelpCommand
     */
    String getInfo();

    /**
     * Возвращает результат выполнения команды в строковом представлении.
     * Необходимо переопределить в классах наследниках
     * @return результат выполнения команды
     */
    String getReport();

    /**
     * Возвращает названия аргументов и количество необходимых экземпляров {element} коллекции для вывода в help
     * @see HelpCommand
     * @return названия аргументов и количество экземпляров в строчном представлении
     */
    String argumentsInfo();
}
