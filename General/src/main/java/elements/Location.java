package elements;

import annotations.Builder;
import constants.Messages;
import exceptions.BadParametersException;

import java.io.Serializable;

public class Location implements Comparable<Location>, Serializable {
    private long x;
    private Double y; //Поле не может быть null
    private float z;

    public Location() {
    }

    @Builder(field = "parameter.loc_x", order = 1)
    public void setX(String x) {
        try {
            this.x = x.isBlank() ? 0 : Long.parseLong(x);
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_integer", Messages.getMessage("parameter.loc_x")));
        }
    }

    @Builder(field = "parameter.loc_y", order = 2)
    public void setY(String y) {
        try {
            if (y.isBlank())
                throw new BadParametersException(Messages.getMessage("warning.format.not_defined", Messages.getMessage("parameter.loc_y")));
            this.y = Double.parseDouble(y);
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real", Messages.getMessage("parameter.loc_y")));
        }
    }

    @Builder(field = "parameter.loc_z", order = 3)
    public void setZ(String z) {
        try {
            this.z = z.isBlank() ? 0 : Float.parseFloat(z);
        } catch (NumberFormatException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.not_real", Messages.getMessage("parameter.loc_z")));
        }
    }

    public long getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", x, y, z);
    }

    @Override
    public int compareTo(Location o) {
        double distanceThis = Math.sqrt(x * x + y * y + z * z);
        double distanceO = Math.sqrt(o.x * o.x + o.y * o.y + o.z * o.z);
        return Double.compare(distanceThis, distanceO);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != Location.class) return false;
        Location location = (Location) o;
        return compareTo(location) == 0;
    }
}
