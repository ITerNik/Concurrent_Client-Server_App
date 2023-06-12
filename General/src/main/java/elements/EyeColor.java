package elements;

import constants.Messages;

import java.io.Serializable;

public enum EyeColor implements Serializable {
    GREEN,
    RED,
    BLACK,
    BLUE,
    YELLOW;

    private final String locale = Messages.getMessage("color." + name().toLowerCase()).toUpperCase();

    public static EyeColor getByValue(String value) {
        for (EyeColor color : EyeColor.values()) {
            if (color.locale.contains(value.toUpperCase()) ||
                    color.name().contains(value.toUpperCase())) return color;
        }
        try {
            return EyeColor.values()[Integer.parseInt(value) - 1];
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return null;
        }
    }


    @Override
    public String toString() {
        return locale;
    }
}
