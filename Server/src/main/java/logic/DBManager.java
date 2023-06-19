package logic;

import constants.Constants;
import constants.Messages;
import elements.*;
import sendings.Response;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DBManager implements Manager {
    private String login;
    private String password;
    private final CollectionManager manager;
    private Connection db;
    private final Map<String, Person> collection = Collections.synchronizedMap(new Hashtable<>());

    public DBManager(String login, String password) {
        this.login = login;
        this.password = password;
        this.manager = new CollectionManager(collection);
        connect(login, password);
    }

    public void connect(String login, String password) {
        try {
            db = DriverManager.getConnection("jdbc:postgresql://localhost:5444/studs", login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadCollection() throws SQLException {
        Statement loaded = db.createStatement();
        ResultSet columns = loaded.executeQuery("SELECT * FROM person"); //TODO: Optimize selecting
        while (columns.next()) {
            collection.put(columns.getString("key"), new Person(
                    columns.getInt("id"), columns.getString("name"),
                    new Coordinates(), columns.getDate("creationDate").toLocalDate(),
                    columns.getDouble("height"), columns.getDouble("weight"),
                    EyeColor.getByValue(columns.getString("eyeColor")),
                    HairColor.getByValue(columns.getString("hairColor")), null
            ));
        }
        loaded.close();
        columns.close();
    }

    @Override
    public boolean containsKey(String s) {
        return collection.containsKey(s);
    }

    @Override
    public void put(String parameter, Person element) {
        try {
            PreparedStatement statement = db.prepareStatement("INSERT INTO person (key, name, coordinates, height, weight, eyeColor, hairColor, location) VALUES ('" + parameter + "', ?, ?::point, ?, ?, ?, ?, ?::point)");
            fillStatement(statement, element);
            statement.execute();
            statement.close();
            ResultSet id = db.createStatement().executeQuery("SELECT CURRVAL('person_id_seq')");
            if (id.next()) element.setId(id.getInt(1));
            collection.put(parameter, element);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void fillStatement(PreparedStatement statement, Person element) throws SQLException {
        statement.setString(1, element.getName());
        statement.setString(2, element.getCoordinates().toString());
        statement.setDouble(3, element.getHeight());
        statement.setDouble(4, element.getWeight());
        statement.setString(5, element.getEyeColor().name());
        statement.setString(6, element.getHairColor().name());
        statement.setString(7, "(0, 0)");
    }

    @Override
    public void update(String key, Person element) {
        try {
            PreparedStatement statement = db.prepareStatement("UPDATE person SET name = ?, coordinates = ?::point, height = ?, weight = ?, eyeColor = ?, hairColor = ?, location = ?::point WHERE key = '" + key + "'");
            fillStatement(statement, element);
            statement.execute();
            statement.close();
            ResultSet id = db.createStatement().executeQuery("SELECT id FROM person WHERE key = '" + key + "'");
            if (id.next()) element.setId(id.getInt(1));
            collection.replace(key, element);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(String parameter) {
        try {
            db.createStatement().execute("DELETE FROM person WHERE key = '" + parameter + "'");
            collection.remove(parameter);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ArrayList<String> removeGreater(String parameter) {
        ArrayList<String> removed = new ArrayList<>();
        try {
            ResultSet res = db.createStatement().executeQuery("DELETE FROM person WHERE key > '" + parameter + "' RETURNING key");
            while (res.next()) {
                collection.remove(res.getString(1));
                removed.add(res.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return removed;
    }

    @Override
    public ArrayList<String> removeLower(Person element) {
        ArrayList<String> removed = collection.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(element) < 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        try {
            PreparedStatement statement = db.prepareStatement("DELETE FROM person WHERE key = ?");
            for (String key : removed) {
                statement.setString(1, key);
                statement.addBatch();
            }
            statement.executeBatch();
            removed.forEach(collection.keySet()::remove);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return removed;
    }

    @Override
    public void clear() {
        manager.clear();
    }

    @Override
    public void save() throws IOException { //TODO: restructure interface
    }

    @Override
    public int countByWeight(double weight) {
        return manager.countByWeight(weight);
    }

    @Override
    public ArrayList<Person> filterByLocation(Location location) {
        return manager.filterByLocation(location);
    }

    @Override
    public int countGreaterThanLocation(Location location) {
        return manager.countGreaterThanLocation(location);
    }

    @Override
    public String findById(int id) {
        return manager.findById(id);
    }

    @Override
    public String getInfo() {
        try {
            ResultSet res = db.createStatement().executeQuery("SELECT MIN(creationdate) FROM person");
            LocalDate date = res.next() ? res.getDate(1).toLocalDate() : null;
            return String.format(Messages.getMessage("collection.type") + ": %s\n" +
                            Messages.getMessage("collection.elements") + ": %s\n" +
                            Messages.getMessage("collection.date") + ": %s\n" +
                            Messages.getMessage("collection.size") + ": %s",
                    collection.getClass().getSimpleName(), Messages.getMessage("parameter.person"), date, collection.size());
        } catch (SQLException e) {
            e.printStackTrace();
            return "No info";
        }
    }
    public String show() {
        return manager.show();
    }

    @Override
    public Response checkUser(User user) {
        try {
            if (user.getStatus()) {
               return checkAuthorized(user.getLogin(), user.getPassword()) ?
                       new Response(Constants.OK_STATUS) : new Response("Неверный логин или пароль");
            } else {
                return addUnauthorized(user.getLogin(), user.getPassword()) ?
                        new Response(Constants.OK_STATUS) : new Response("Такой пользователь уже существует");

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response("Ошибка авторизации");
        }
    }
    private boolean checkAuthorized(String login, String password) throws SQLException {
        PreparedStatement statement = db.prepareStatement("SELECT EXISTS(SELECT 1 FROM users WHERE login = ? AND password = ?)");
        statement.setString(1, login);
        statement.setString(2, password);
        ResultSet res = statement.executeQuery();
        return res.next() && res.getBoolean(1);
    }
    private boolean addUnauthorized(String login, String password) throws SQLException {
        ResultSet res = db.createStatement().executeQuery("SELECT EXISTS (SELECT 1 FROM users WHERE login = '" + login + "')");
        if (res.next() && !res.getBoolean(1)) {
            PreparedStatement statement = db.prepareStatement("INSERT INTO users (login, password, admin_roots) VALUES (?, ?, ?)");
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setBoolean(3, false);
            statement.execute();
            return true;
        } else {
            return false;
        }
    }
}
