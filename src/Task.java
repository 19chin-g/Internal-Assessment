import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Task {
    LocalDate date; // Store date as LocalDate
    int type;
    String info;

    public Task() {
    }

    public void addTask() {
        Scanner scanner = new Scanner(System.in);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        while (true) {
            System.out.print("Enter date in DD/MM/YY format: ");
            String dateString = scanner.nextLine().trim();

            try {
                date = LocalDate.parse(dateString, formatter);
                System.out.println("Date stored: " + date);
                break; // Exit loop if parsing is successful
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please enter a valid date (DD/MM/YY).");
            }
        }

        System.out.println("1) Academic");
        System.out.println("2) Social & Personal");
        System.out.println("3) Health & Wellbeing");
        System.out.print("Enter task type: ");
        type = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Task type: " + type);
    }
}