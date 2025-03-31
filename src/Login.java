import java.util.Scanner;

public class Login {
    Database loginFile;
    int userID;

    //COLOURS
    String YELLOW = "\u001B[33m";
    String RESET = "\u001B[0m";
    String BOLD = "\033[1m";  // Bold
    String DARK_RED = "\033[0;31m";



    public Login(String filename) {
        loginFile = new Database(filename);
    }

    // Method checking if user is in loginFile when login
    public boolean findUser(String username, String password) {
        int recordNum = 0;
        String[] record;

        while ((record = loginFile.readRecord(recordNum)) != null) {
            if (record.length > 0 && record[0].equals(username) && record[1].equals(password)) {
                userID = recordNum;
                return true;
            }
            recordNum++;
        }
        return false;
    }

    // Method to check for same username
    public boolean isUserTaken(String username) {
        int recordNum = 0;
        String[] record;

        while ((record = loginFile.readRecord(recordNum)) != null) {
            if (record.length > 0 && record[0].equals(username)) {
                return true;
            }
            recordNum++;
        }
        return false;
    }

    // Method to handle login process
    public Boolean login() {
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
            System.out.print(DARK_RED +"Invalid username or password. " + RESET);
            return false;
        }
    }

    // Method to register a new user
    public void register() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        System.out.println(BOLD + YELLOW + "======== REGISTER ========" + RESET);

        while (username.isBlank() || isUserTaken(username)) {
            System.out.print("Enter a username: ");
            username = scanner.nextLine().trim(); // makes sure just spaces aren't allowed
            if (username.isBlank()) {
                System.out.println(DARK_RED + "Username cannot be empty! Please enter a valid username." + RESET);
            } else if (isUserTaken(username)) {
                System.out.println(DARK_RED + "Username has been taken! Please enter a valid username." + RESET);
            }
        }

        while (password.isBlank()) {
            System.out.print("Enter a password: ");
            password = scanner.nextLine().trim(); // note - trim removes leading and trailing white space
            if (password.isBlank()) {
                System.out.println(DARK_RED + "Password cannot be empty! Please enter a valid password." + RESET);
            }
        }

        User newUser = new User(username, password);
        loginFile.addUser(newUser);
        System.out.println("User registered successfully.");
    }

    public int getUserID() {
        return userID;
    }

}