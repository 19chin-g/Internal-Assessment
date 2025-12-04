import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class Database {
    private final String filename;
    private boolean available;

    public Database(String filename) {
        this.filename = filename;
        available = true;

        // Check if file exists
        File file = new File(filename);
        if (!file.exists()) {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "The file \"" + filename + "\" does not exist. Do you want to create it?",
                    "File not found",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                try {
                    if (file.createNewFile()) {
                        JOptionPane.showMessageDialog(null, "File created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create file.", "Error", JOptionPane.ERROR_MESSAGE);
                        available = false;
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error creating file: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                    available = false;
                }
            } else {
                // User chose not to create
                available = false;
            }
        }
    }

    // Check if database is usable
    public boolean isAvailable() {
        return available;
    }

    // Read all records
    public ArrayList<String> readAllRecords() {
        ArrayList<String> records = new ArrayList<>();
        if (!available) return records;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading file:\n" + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        return records;
    }

    // Add a record
    public void addRecord(String data) {
        if (!available) {
            JOptionPane.showMessageDialog(null, "Cannot add record. File is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file:\n" + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Remove a record by index
    public void removeRecord(int recordNum) {
        if (!available) return;

        ArrayList<String> records = readAllRecords();
        if (recordNum < 0 || recordNum >= records.size()) {
            JOptionPane.showMessageDialog(null, "Invalid record number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        records.remove(recordNum);
        writeAllRecords(records);
    }

    // Overwrite file with given records
    public void writeAllRecords(ArrayList<String> records) {
        if (!available) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error writing to file:\n" + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Sort records alphabetically using insertion sort
    public void sortRecords() {
        ArrayList<String> records = readAllRecords();

        if (records.size() <= 1) return;

        for (int i = 1; i < records.size(); i++) {
            String current = records.get(i); // Store current element
            int j = i - 1;

            while (j >= 0 && records.get(j).compareToIgnoreCase(current) > 0) {
                records.set(j + 1, records.get(j));
                j--;
            }

            records.set(j + 1, current);
        }
        writeAllRecords(records);
    }


    // Search for a record
    public int searchRecord(String target) {
        ArrayList<String> records = readAllRecords();
        return binarySearch(records, target, 0, records.size() - 1);
    }

    // Recursive binary search
    private int binarySearch(ArrayList<String> records, String target, int low, int high) {
        if (low > high) return -1;

        int mid = (low + high) / 2;
        int cmp = records.get(mid).compareToIgnoreCase(target);

        if (cmp == 0) return mid;
        else if (cmp < 0) return binarySearch(records, target, mid + 1, high);
        else return binarySearch(records, target, low, mid - 1);
    }
}
