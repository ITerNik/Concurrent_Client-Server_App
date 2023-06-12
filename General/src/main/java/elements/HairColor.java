package elements;

import constants.Messages;

import java.io.Serializable;

public enum HairColor implements Serializable {
    ORANGE,
    WHITE,
    BROWN;

    private final String locale = Messages.getMessage("color." + name().toLowerCase()).toUpperCase();

    public static HairColor getByValue(String value) {
        for (HairColor color : HairColor.values()) {
            if (color.locale.contains(value.toUpperCase()) ||
                    color.name().contains(value.toUpperCase())) return color;
        }
        try {
            return HairColor.values()[Integer.parseInt(value) - 1];
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return locale;
    }
}
