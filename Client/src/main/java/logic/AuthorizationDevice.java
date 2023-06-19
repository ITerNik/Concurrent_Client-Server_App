package logic;

import constants.Constants;
import elements.User;

import java.io.Console;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AuthorizationDevice {
    private final Console input = System.console();
    private final Scanner scanner = new Scanner(System.in);

    public AuthorizationDevice() {
    }

    public boolean getUserStatus() {
        while (true) {
            System.out.println("Вы уже зарегистрированы? (Да/Нет)");
            String authorized = scanner.nextLine();
            if (authorized.equalsIgnoreCase("Да")) {
                return true;
            } else if (authorized.equalsIgnoreCase("Нет")) {
                return false;
            }
            System.out.println("Некорректный ответ");
        }
    }

    public User registerUser() {
        Pattern regex = Pattern.compile(Constants.PASSWORD_CONSTRAINT);
        System.out.println("Придумайте логин:");
        String login = scanner.nextLine(), password = "";
        while (!regex.matcher(password).matches()) {
            System.out.println("Пароль должен состоять не менее чем из 8 символов латиницы и включать как минимум одну заглавную и строчную букву, цифру и специальный символ");
            if (input != null) {
                password = new String(input.readPassword("Придумайте пароль:\n"));
            } else {
                System.out.println("Придумайте пароль:");
                password = scanner.nextLine();
            }
        }
        return new User(login, encodePassword(password), false);
    }

    public User authorizeUser() {
        System.out.println("Введите логин:");
        String login = scanner.nextLine(), password;
        if (input != null) {
            password = new String(input.readPassword("Введите пароль:\n"));
        } else {
            System.out.println("Введите пароль:");
            password = scanner.nextLine();
        }
        return new User(login, encodePassword(password), true);
    }

    private String encodePassword(String password) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hashBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            hash = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        }
        return hash;
    }
}
