import java.util.Scanner;

public class Login {
    Database database;
    int userID;

    public Login(String filename) {
        database = new Database(filename);
    }

    // Method checking if user is in database when login
    public boolean findUser(String username, String password) {
        int recordNum = 0;
        String[] record;

        while ((record = database.readRecord(recordNum)) != null) {
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

        while ((record = database.readRecord(recordNum)) != null) {
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

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (findUser(username, password)) {
            System.out.println("Login successful!");
            System.out.println(userID);
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    // Method to register a new user
    public void register() {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";

        while (username.isBlank() || isUserTaken(username)) {
            System.out.print("Enter a username: ");
            username = scanner.nextLine().trim(); // makes sure just spaces aren't allowed
            if (username.isBlank()) {
                System.out.println("Username cannot be empty! Please enter a valid username.");
            } else if (isUserTaken(username)) {
                System.out.println("Username has been taken! Please enter a valid username.");
            }
        }

        while (password.isBlank()) {
            System.out.print("Enter a password: ");
            password = scanner.nextLine().trim(); // note - trim removes leading and trailing white space
            if (password.isBlank()) {
                System.out.println("Password cannot be empty! Please enter a valid password.");
            }
        }

        User newUser = new User(username, password);
        database.addUser(newUser);
        System.out.println("User registered successfully.");
    }

}