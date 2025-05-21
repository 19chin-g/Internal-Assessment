import java.util.ArrayList;
import java.util.Scanner;

public class Login {
    Database loginFile;
    int userID;

    String YELLOW = "\u001B[33m";
    String RESET = "\u001B[0m";
    String BOLD = "\033[1m";
    String DARK_RED = "\033[0;31m";

    public Login(String filename) {
        loginFile = new Database(filename);
    }


    public boolean findUser(String username, String password) {
        ArrayList<String> records = loginFile.readAllRecords();
        userID = 0;

        for (String record : records) {
            String[] userInfo = record.split(" ");
            if (userInfo[0].equals(username) && userInfo[1].equals(password)) {
                return true;
            }
            userID++;
        }
        return false;
    }


    // check if username is already taken
    public boolean isUserTaken(String username) {
        ArrayList<String> records = loginFile.readAllRecords();

        for (String record : records) {
            String[] userInfo = record.split(" ");
            if (userInfo[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    // login process
    public boolean login() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(BOLD + YELLOW + "========== LOGIN ==========" + RESET);

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (findUser(username, password)) {
            System.out.println("Login successful!");
            System.out.println("User ID: " + userID);
            return true;
        } else {
            System.out.println(DARK_RED + "Invalid username or password." + RESET);
            return false;
        }
    }





    // register a new user
    public void register() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println(BOLD + YELLOW + "======== REGISTER ========" + RESET);

        while (username.isBlank() || isUserTaken(username) || username.contains(" ")) {
            System.out.print("Enter a username: ");
            username = scanner.nextLine().trim();

            if (username.isBlank()) {
                System.out.println(DARK_RED + "Username cannot be empty!" + RESET);
            } else if (isUserTaken(username)) {
                System.out.println(DARK_RED + "Username has been taken!" + RESET);
            } else if (username.contains(" ")) {
                System.out.println(DARK_RED + "Username cannot contain spaces!" + RESET);
            }
        }

        while (password.isBlank() || password.contains(" ")) {
            System.out.print("Enter a password: ");
            password = scanner.nextLine().trim();

            if (password.isBlank()) {
                System.out.println(DARK_RED + "Password cannot be empty!" + RESET);
            } else if (password.contains(" ")) {
                System.out.println(DARK_RED + "Password cannot contain spaces!" + RESET);
            }
        }

        User user = new User(username, password);
        loginFile.addRecord(user.toString());  // Append user to the file
        System.out.println("User registered successfully.");
    }

    public int getUserID() {
        return userID;
    }
}
