import java.util.ArrayList;


public class Login {
    private Database loginFile;
    private int userID;


    public Login(String filename) {
        loginFile = new Database(filename);
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
        ArrayList<String> records = loginFile.readAllRecords();

        if (loginFile.searchRecord(username + " " + password) != -1) {
            return true;
        }
        else {
            return false;
        }

    }


    // Register a new user
    public void register(String username, String password) {
        User user = new User(username, password);
        loginFile.addRecord(user.toString());  // Append user to the file
        ArrayList<String> userList = loginFile.readAllRecords();
        loginFile.sortRecords(); // you define this in Database class

    }

    public int getUserID() {
        return userID;
    }
}
