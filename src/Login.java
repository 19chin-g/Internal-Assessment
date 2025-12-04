import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;


public class Login {
    private Database loginFile;
    private int userID;


    public Login(String filename) {
        try {
            loginFile = new Database(filename); // Prompt user if file missing
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Login system unavailable: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            loginFile = null; // Disable login if file cant be accessed
        }
    }




    // Checks if user exists in file
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


    // Check if username is already taken
    public boolean isUserTaken(String username,String password) {
        return loginFile.searchRecord(username + " " + password) != -1;

    }


    // Register a new user
    public void register(String username, String password) {
        User user = new User(username, password);
        loginFile.addRecord(user.toString());  // Append user to the file
        loginFile.sortRecords();

    }

    public int getUserID() {
        return userID;
    }
}
