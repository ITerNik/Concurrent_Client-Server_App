package constants;

import java.util.ResourceBundle;

public abstract class Messages {
    static ResourceBundle rb = ResourceBundle.getBundle("messages");

    static public String getMessage(String property) {
        return rb.getString(property);
    }

    static public String getMessage(String property, Object... args) {
        return String.format(getMessage(property), args);
    }
}
