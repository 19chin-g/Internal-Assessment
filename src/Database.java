import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final String filename;

    public Database(String filename) {
        this.filename = filename;
    }


    // Read all records from the file and return them as list of strings
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
            writer.flush();
        } catch (IOException e) {
            System.out.println("Error writing to file");
        }
    }

    // Remove a specific record from the file
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

    // Rewrite file with given records
    public void writeAllRecords(ArrayList<String> records) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sorts alphabetically with bubble sort
    public void sortRecords() {
        ArrayList<String> records = readAllRecords();
        if (records == null || records.size() <= 1) return; // no need to sort

        // Bubble sort by full record string, case-insensitive
        for (int i = 0; i < records.size() - 1; i++) {
            for (int j = 0; j < records.size() - i - 1; j++) {
                String record1 = records.get(j);
                String record2 = records.get(j + 1);

                if (record1.compareToIgnoreCase(record2) > 0) {
                    // Swap
                    records.set(j, record2);
                    records.set(j + 1, record1);
                }
            }
        }

        writeAllRecords(records);
    }

    // Searches with binary search to find specific user or task
    public int searchRecord(String target) {
        ArrayList<String> records = readAllRecords();
        return binarySearch(records, target, 0, records.size() - 1);
    }


    private int binarySearch(ArrayList<String> records, String target, int low, int high) {
        if (low > high) {
            return -1; // Not found
        }
        int mid = (low + high) / 2;
        String record = records.get(mid);
        int comparison = record.compareToIgnoreCase(target);

        if (comparison == 0) {
            return mid; // Found record
        } else if (comparison < 0) {
            return binarySearch(records, target, mid + 1, high); //  right half
        } else {
            return binarySearch(records, target, low, mid - 1); //  left half
        }
    }





}
