package logic;

import elements.Location;
import elements.Person;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public interface Manager extends Serializable {
    boolean containsKey(String s);

    void put(String parameter, Person element);

    void update(String key, Person element);

    void remove(String parameter);

    ArrayList<String> removeGreater(String parameter);

    ArrayList<String> removeLower(Person element);

    void clear();
    void save() throws IOException;

    int countByWeight(double weight);

    ArrayList<Person> filterByLocation(Location readElement);

    int countGreaterThanLocation(Location readElement);

    String findById(int id);

    String getInfo();


}
