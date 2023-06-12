package constants;
/**
 * Класс Constants содержит константы, используемые в приложении
 */
public abstract class Constants {
    //Разделитель для строк
    public static final String SPLITTER = "\\s+";
    // Временные задержки
    public static final long TIMEOUT = 1000;
    public static final int MAX_RETRY = 3;
    public static final int RETRY_DELAY = 5000;
    // Размер буфера для чтения по-умолчанию
    public static final int BUFFER_SIZE = 1024;
    // Путь к файлу с коллекцией
    public static final String FILE_PATH = ".\\repository.json";

    public static final String HOST_FILE_PATH = ".\\Server\\src\\main\\resources\\repository.json";
    // Данные для налаживания соединения
    public static final int PORT = 9999;
    public static final String CLIENT_HOST = "127.0.0.1";
    public static final String SERVER_HOST = "localhost";

}
