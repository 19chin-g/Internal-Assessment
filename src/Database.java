import java.io.*;
import java.util.List;

public class Database {
    private final String filename;

    public Database(String filename) {
        this.filename = filename;
    }

    public String[] readRecord(int recordNum) { // Method to read one line in record
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            int i = 0;
            while (i <= recordNum && (line = br.readLine()) != null) {
                if(i == recordNum) {
                    return line.split(" "); // returns with each record item in a list
                }
                i++;
            }
            br.close();

        } catch (IOException e) {
            System.out.println("error reading");
        }
        return null;
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

    public void removeRecord(int recordNum) {
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter writer = new BufferedWriter(fw);


            String line = "";
            int i = 0;
            while (i <= recordNum && (line = br.readLine()) != null) {
                if(i == recordNum) {
                    String target = br.readLine();
                }
                i++;
            }
            br.close();

        } catch (IOException e) {
            System.out.println("error reading");
        }
    }
}