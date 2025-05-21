import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String filename;

    public Database(String filename) {
        this.filename = filename;
    }


    // read all records from the file and return them as list of strings
    public ArrayList<String> readAllRecords() {
        ArrayList<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
        return records;
    }

    // Add a new record (task) to the file
    public void addRecord(String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    // remove a specific record  from the file
    public void removeRecord(int recordNum) {
        List<String> records = readAllRecords();
        if (recordNum < 0 || recordNum >= records.size()) {
            System.out.println(recordNum);
            System.out.println("Invalid record number!");
            return;
        }
        records.remove(recordNum);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error updating file");
        }
    }
}
