package logic;

import constants.Messages;
import elements.Location;
import elements.Person;
import exceptions.BadParametersException;
import exceptions.NonUniqueIdException;
import exceptions.StartingProblemException;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionManager implements Manager {
    private final Hashtable<String, Person> collection;
    private final JsonHandler handler;
    private final DependentSet<Integer> uniqueSet = new DependentSet<>();
    private final LocalDateTime date;

    AtomicInteger idCounter = new AtomicInteger(0);

    {
        date = LocalDateTime.now();
    }

    public CollectionManager(String filename) throws NonUniqueIdException, StartingProblemException {
        this.handler = new JsonHandler(filename);
        this.collection = handler.readCollection();
        for (String key : collection.keySet()) {
            if (!uniqueSet.add(collection.get(key).getId()))
                throw new NonUniqueIdException(Messages.getMessage("warning.non_unique_id"));
        }
    }

    public static class DependentSet<T extends Comparable<T>> implements Serializable {
        private final TreeSet<T> unique = new TreeSet<>();

        public boolean add(T value) {
            return unique.add(value);
        }

        public boolean remove(T value) {
            return unique.remove(value);
        }

        public boolean replace(T oldValue, T newValue) {
            return unique.remove(oldValue) && unique.add(newValue);
        }

        public void clear() {
            unique.clear();
        }
    }

    private ArrayList<String> getKeyIf(Predicate<? super String> filter) {
        return collection.keySet()
                .stream()
                .filter(filter)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void put(String key, Person person) {
        while (!uniqueSet.add(idCounter.get())) {
            idCounter.incrementAndGet();
        }
        person.setId(idCounter.get());
        person.setCreationDate(LocalDate.now());
        collection.put(key, person);
    }


    public void remove(String key) {
        uniqueSet.remove(collection.get(key).getId());
        collection.remove(key);
    }


    public void update(String key, Person person) {
        uniqueSet.replace(collection.get(key).getId(), person.getId());
        collection.replace(key, person);
    }

    public boolean containsKey(String arg) {
        return collection.containsKey(arg);
    }

    public void clear() {
        collection.clear();
        uniqueSet.clear();
    }

    public void save() throws IOException {
        handler.clear();
        handler.writeData(collection);
    }

    public int countByWeight(double weight) {
        return (int) collection.values().stream()
                .filter(person -> Double.compare(person.getWeight(), weight) == 0)
                .count();
    }

    public String findById(int id) {
        try {
            return collection.entrySet().stream()
                    .filter(entry -> entry.getValue().getId() == id)
                    .findFirst().get().getKey();
        } catch (NoSuchElementException e) {
            throw new BadParametersException(Messages.getMessage("warning.format.no_such_element", Messages.getMessage("parameter.id")));
        }
    }

    public int countGreaterThanLocation(Location location) {
        return (int) collection.values().stream()
                .filter(person -> {
                    Location curr = person.getLocation();
                    return curr != null && curr.compareTo(location) > 0;
                })
                .count();
    }

    public ArrayList<Person> filterByLocation(Location location) {
        ArrayList<Person> selected = collection.values().stream()
                .filter(person -> {
                    Location curr = person.getLocation();
                    return curr != null && curr.equals(location);
                })
                .collect(Collectors.toCollection(ArrayList::new));
        if (selected.isEmpty())
            throw new IllegalArgumentException(Messages.getMessage("warning.format.no_such_element", Messages.getMessage("parameter.location")));
        else {
            return selected;
        }
    }


    public ArrayList<String> removeGreater(String key) {
        ArrayList<String> removed = collection.keySet().stream()
                .filter(curr -> curr.compareTo(key) > 0)
                .collect(Collectors.toCollection(ArrayList::new));
        for (String currKey : removed) {
            uniqueSet.remove(collection.get(currKey).getId());
            collection.remove(currKey);
        }
        return removed;
    }

    public ArrayList<String> removeLower(Person element) {
        ArrayList<String> removed = collection.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(element) < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        for (String currKey : removed) {
            uniqueSet.remove(collection.get(currKey).getId());
            collection.remove(currKey);
        }
        return removed;
    }

    public String getInfo() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm:ss");
        return String.format(Messages.getMessage("collection.type") + ": %s\n" +
                        Messages.getMessage("collection.elements") + ": %s\n" +
                        Messages.getMessage("collection.date") + ": %s\n" +
                        Messages.getMessage("collection.size") + ": %s",
                collection.getClass().getSimpleName(), Messages.getMessage("parameter.person"), date.format(format), collection.size());
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) {
            return Messages.getMessage("message.empty");
        }
        StringBuilder res = new StringBuilder();
        for (String key : collection.keySet()) {
            res.append(String.format("%s \"%s\":\n%s\n\n", Messages.getMessage("parameter.person"), key, collection.get(key)));
        }
        return res.toString();
    }
}
