package elements;

public class User {
    public User() {
    }

    public User(String login, String password, boolean status) {
        this.login = login;
        this.password = password;
        this.status = status;
    }

    private String login = null;
    private String password = null;
    private boolean status = false;

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public boolean getStatus() {
        return status;
    }
}
