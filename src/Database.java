import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String filename;

    public Database(String filename) {
        this.filename = filename;
    }

    public String[] readRecord(int recordNum) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                if (i == recordNum) {
                    reader.close();
                    return line.split(" ");
                }
                i++;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("error");
        }
        return null;
    }


    public List<String> readAllRecords() {
        List<String> records = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("error");
        }
        return records;
    }


    public void writeRecord(int recordNum, String data) {
        List<String> records = readAllRecords();

        if (recordNum < 0 || recordNum >= records.size()) {
            System.out.println("Invalid record number!");
            return;
        }
        records.set(recordNum, data);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("error ");
        }
    }


    public void addUser(User user) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
            writer.write(user.toString());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            System.out.println("erroor");
        }
    }


    public void removeRecord(int recordNum) {
        List<String> records = readAllRecords();
        if (recordNum < 0 || recordNum >= records.size()) {
            System.out.println("Invalid record number!");
            return;
        }
        records.remove(recordNum); // Removes record
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < records.size(); i++) {
                writer.write(records.get(i));
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("eroor");
        }
    }
}