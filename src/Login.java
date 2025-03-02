import java.util.Scanner;

public class Login {
    Database database;

    public Login(String filename) {
        database = new Database(filename);
    }

    // Method to handle login process
    public Boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (database.findUser(username, password)) {
            System.out.println("Login successful!");
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

        while (username.isBlank() || database.checkUsername(username)) {
            System.out.print("Enter a username: ");
            username = scanner.nextLine().trim(); // makes sure just spaces aren't allowed
            if (username.isBlank()) {
                System.out.println("Username cannot be empty! Please enter a valid username.");
            } else if (database.checkUsername(username)) {
                System.out.println("Username has been taken! Please enter a valid username.");
            }
        }

        while (password.isBlank()) {
            System.out.print("Enter a password: ");
            password = scanner.nextLine().trim();// note - trim removes leading and trailing white space
            if (password.isBlank()) {
                System.out.println("Password cannot be empty! Please enter a valid password.");
            }
        }

        User newUser = new User(username, password);
        database.addUser(newUser);

        System.out.println("User registered successfully.");
    }

}