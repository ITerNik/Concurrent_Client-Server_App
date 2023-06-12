package elements;

import annotations.Builder;
import constants.Messages;
import exceptions.BadParametersException;

import java.io.Serializable;
import java.time.LocalDate;

public class Person implements Comparable<Person>, Serializable {
    private int id;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null

    private LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private double height; //Значение поля должно быть больше 0
    private double weight; //Значение поля должно быть больше 0
    private EyeColor eyeColor; //Поле может быть null
    private HairColor hairColor; //Поле может быть null
    private Location location; //Поле может быть null


    public Person() {
    }


    public Location getLocation() {
        return location;
    }

    public Integer getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public double getHeight() {
        return height;
    }

    public EyeColor getEyeColor() {
        return eyeColor;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_positive", Messages.getMessage("parameter.id")));
        }
        this.id = id;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    @Builder(field = "parameter.name", order = 2)
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new BadParametersException(Messages.getMessage("warning.format.not_defined", Messages.getMessage("parameter.name")));
        this.name = name;
    }

    @Builder(order = 3)
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null)
            throw new BadParametersException(Messages.getMessage("warning.format.not_defined", Messages.getMessage("parameter.coordinates")));
        this.coordinates = coordinates;
    }

    @Builder(field = "parameter.weight", order = 6)
    public void setWeight(String weight) {
        try {
            this.weight = weight.isBlank() ? 0 : Double.parseDouble(weight);

        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real", Messages.getMessage("parameter.weight")));
        }
        if (this.weight < 0)
            throw new BadParametersException(Messages.getMessage("warning.format.not_positive", Messages.getMessage("parameter.weight")));
    }

    @Builder(field = "parameter.height", order = 5)
    public void setHeight(String height) {
        try {
            this.height = height.isBlank() ? 0 : Double.parseDouble(height);

        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real", Messages.getMessage("parameter.height")));
        }
        if (this.height < 0)
            throw new BadParametersException(Messages.getMessage("warning.format.not_positive", Messages.getMessage("parameter.height")));
    }

    @Builder(field = "parameter.eye_color", variants = {"color.green", "color.red", "color.black", "color.blue", "color.yellow"}, order = 7)
    public void setEyeColor(String value) {
        if (value == null || value.isBlank()) {
            this.eyeColor = null;
        } else {
            EyeColor converted = EyeColor.getByValue(value);
            if (converted == null) throw new BadParametersException(Messages.getMessage("warning.no_color"));
            this.eyeColor = converted;
        }
    }

    @Builder(field = "parameter.hair_color", variants = {"color.orange", "color.white", "color.brown"}, order = 8)
    public void setHairColor(String value) {
        if (value == null || value.isBlank()) {
            this.hairColor = null;
        } else {
            HairColor converted = HairColor.getByValue(value);
            if (converted == null) throw new BadParametersException(Messages.getMessage("warning.no_color"));
            this.hairColor = converted;
        }
    }

    @Builder(order = 9)
    public void setLocation(Location location) {
        this.location = location;
    }



    @Override
    public int compareTo(Person o) {
        int res = Double.compare(height, o.height);
        if (res == 0) {
            res = Double.compare(weight, o.weight);
        }
        if (res == 0) {
            res = name.compareToIgnoreCase(o.name);
        }
        return res;
    }

    @Override
    public String toString() {
        return String.format(Messages.getMessage("parameter.id") + ": %s\n" +
                        Messages.getMessage("parameter.name") + ": %s\n" +
                        Messages.getMessage("parameter.coordinates") + ": %s\n" +
                        Messages.getMessage("parameter.creation_date") + ": %s\n" +
                        Messages.getMessage("parameter.height") + ": %s\n" +
                        Messages.getMessage("parameter.weight") + ": %s\n" +
                        Messages.getMessage("parameter.eye_color") + ": %s\n" +
                        Messages.getMessage("parameter.hair_color") + ": %s\n" +
                        Messages.getMessage("parameter.location") + ": %s",
                id, name, coordinates, creationDate, height, weight, eyeColor, hairColor, location);
    }
}

