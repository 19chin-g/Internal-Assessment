import java.io.*;

public class Database {
    private String filename;

    // Constructor to initialize the filename
    public Database(String filename) {
        this.filename = filename;
    }
    public boolean checkUsername(String username) { // Method to check for same username
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            line = r.readLine();
            while (line != null) {
                String[] record = line.split(" ");
                if (record[0].equals(username))   {
                    return true;
                }
                line = r.readLine();

            }
            r.close();

        } catch (IOException e) {
            System.out.println("error reading");
        }
        return false;
    }

    // Method to check if a user with the given username and password exists
    public boolean findUser(String username, String password) {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader r = new BufferedReader(fr);
            String line = "";
            line = r.readLine();
            while (line != null) {
                String[] record = line.split(" "); // Split the line into username and password
                if (record[0].equals(username) && record[1].equals(password)) {
                    return true;
                }
                line = r.readLine();

            }
            r.close();

        } catch (IOException e) {
            System.out.println("error reading");
        }
        return false;
    }

    // Method to write a new user to the file
    public void addUser(User user) {
        try {
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(user.toString());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("error writing");
        }
    }
}