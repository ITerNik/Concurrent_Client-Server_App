package elements;

import annotations.Builder;
import constants.Messages;
import exceptions.BadParametersException;

import java.io.Serializable;

public class Coordinates implements Comparable<Coordinates>, Serializable {
    private Double x; //Поле не может быть null
    private Integer y; //Значение поля должно быть больше -663, Поле не может быть null


    public Coordinates() {
    }

    @Builder(field = "parameter.coordinate_x", order = 1)
    public void setX(String x) {
        try {
            if (x.isBlank())
                throw new BadParametersException(Messages.getMessage("warning.format.not_defined", Messages.getMessage("parameter.coordinate_x")));
            this.x = Double.parseDouble(x);
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real", Messages.getMessage("parameter.coordinate_x")));
        }
    }

    @Builder(field = "parameter.coordinate_y", order = 2)
    public void setY(String y) {
        try {
            if (y.isBlank())
                throw new BadParametersException(Messages.getMessage("warning.format.not_defined", Messages.getMessage("parameter.coordinate_y")));
            this.y = Integer.parseInt(y);
            if (this.y < -633)
                throw new BadParametersException(Messages.getMessage("warning.format.not_big_enough", Messages.getMessage("parameter.coordinate_y"), -633));
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_integer", Messages.getMessage("parameter.coordinate_y")));
        }
    }

    public Double getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", x, y);
    }


    @Override
    public int compareTo(Coordinates o) {
        double distanceThis = Math.sqrt(x * x + y * y);
        double distanceO = Math.sqrt(o.x * o.x + o.y * o.y);
        return Double.compare(distanceThis, distanceO);
    }
}
