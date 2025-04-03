import java.time.*;
import java.util.*;

public class TaskCalendar {
    int userID;
    int day;
    int month;
    int year;
    Database taskFile;

    LocalDate today = LocalDate.now();
    int currentDay = today.getDayOfMonth();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    // COLOURS
    String RESET = "\u001B[0m";
    String DARK_RED = "\033[0;31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String CYAN = "\033[0;36m";
    String LIGHT_GRAY = "\033[0;37m"; // Light Gray (Bright White)
    String BOLD = "\033[1m";


    public TaskCalendar(int userID, int day, int month, int year, String filename) {
        this.userID = userID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.taskFile = new Database(filename);
    }



    // Create the calendar
    public ArrayList<ArrayList<Integer>> createCalendar() {
        ArrayList<ArrayList<Integer>> monthList = new ArrayList<>();
        for (int rows = 0; rows < 6; rows++) {
            monthList.add(new ArrayList<>());
        }

        LocalDate selectedMonth = LocalDate.of(year, month, 1);
        int monthLength = selectedMonth.lengthOfMonth();
        int startDay = selectedMonth.getDayOfWeek().getValue();

        for (int i = 1; i < startDay; i++) {
            monthList.get(0).add(null);
        }

        int nextList = 0;
        for (int day = 1; day <= monthLength; day++) {
            monthList.get(nextList).add(day);
            if ((day + startDay) % 7 == 1) {
                nextList++;
            }
        }

        return monthList;
    }

    // Display the calendar with tasks highlighted
    public void displayCalendar() {
        ArrayList<ArrayList<Integer>> monthList = createCalendar();
        LocalDate selectedMonth = LocalDate.of(year, month, 1);

        System.out.println(BOLD + "============ CALENDAR ============" + RESET);

        // Prints full date with day if on current month otherwise display month and year only
        if (selectedMonth.getMonthValue() == currentMonth && year == currentYear) {
            System.out.println(CYAN + day + " " + selectedMonth.getMonth() + " " + year + RESET);
        } else {
            System.out.println(CYAN + selectedMonth.getMonth() + " " + year + RESET);
        }

        System.out.println("Mon  Tue  Wed  Thu  Fri  Sat  Sun");

        for (int week = 0; week < monthList.size(); week++) {
            for (int day = 0; day < monthList.get(week).size(); day++) {
                Integer currentDayInMonth = monthList.get(week).get(day);

                if (currentDayInMonth == null) {
                    System.out.print("     "); // Empty space for alignment
                } else if (currentDayInMonth == currentDay && currentMonth == month && currentYear == year) {
                    if (hasTask(userID, LocalDate.of(year, month, currentDayInMonth))) {
                        System.out.printf("[%s%2d%s] ", DARK_RED, currentDayInMonth, RESET);
                    } else {
                        System.out.printf("[%s%2d%s] ", CYAN, currentDayInMonth, RESET);
                    }
                } else if (hasTask(userID, LocalDate.of(year, month, currentDayInMonth))) {
                    if (currentDayInMonth < currentDay && currentMonth == month && currentYear == year) {
                        // Tasks with passed due dates are red
                        System.out.printf(" %s%2d%s  ", DARK_RED, currentDayInMonth, RESET);
                    } else {
                        // Future days with tasks are Green
                        System.out.printf(" %s%2d%s  ", GREEN, currentDayInMonth, RESET);
                    }
                } else {
                    // Normal days are light gray
                    System.out.printf(" %s%2d%s  ", LIGHT_GRAY, currentDayInMonth, RESET);
                }
            }
            System.out.println(); // Move to the next week row
        }
    }

    // check if a task exists on a specific date for the current user
    public boolean hasTask(int userID, LocalDate date) {
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                if (taskDetails.length >= 2 && taskDetails[0].equals(String.valueOf(userID)) && taskDetails[1].equals(date.toString())) {
                    return true;  // task found for this exact user on the given date
                }
            }
        }
        return false;  // No task found for this user on this date
    }


    // Add a task to a specific date
    public void addTask() {
        Scanner scanner = new Scanner(System.in);
        LocalDate taskDate = null;
        String taskType = "";
        String taskInfo = "";

        System.out.println(BOLD + YELLOW + "======== TASK CREATION ========" + RESET);
        System.out.print("Enter date (DD/MM/YY or D/M/YY) to add task: ");

        while (taskDate == null) {
            String input = scanner.nextLine().trim();
            String[] dateParts = input.split("/");

            if (dateParts.length != 3) {
                System.out.print(DARK_RED + "Invalid format! Please use DD/MM/YY: " + RESET);
            } else {
                try {
                    int inputDay = Integer.parseInt(dateParts[0]);
                    int inputMonth = Integer.parseInt(dateParts[1]);
                    int inputYear = Integer.parseInt(dateParts[2]);

                    inputYear += 2000; // Converts to full year: 25 -> 2025
                    int daysInMonth = YearMonth.of(inputYear, inputMonth).lengthOfMonth();

                    // ensures it is a valid date within the month
                    if (inputDay >= 1 && inputDay <= daysInMonth) {
                        taskDate = LocalDate.of(inputYear, inputMonth, inputDay);
                    } else {
                        System.out.print(DARK_RED + "Invalid date! Please use valid date: " + RESET);
                    }

                } catch (NumberFormatException e) { // Ensures numbers are used in between /
                    System.out.print(DARK_RED + "Invalid numbers! Use DD/MM/YY format: " + RESET);
                }
            }
        }

        // prompt for task type and information
        System.out.println("1) Academic");
        System.out.println("2) Social & Personal");
        System.out.println("3) Health & Wellbeing");

        while (taskType.isEmpty()) {
            System.out.print("Enter task type: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> taskType = "Academic";
                case "2" -> taskType = "Social & Personal";
                case "3" -> taskType = "Health & Wellbeing";
                default -> System.out.println(DARK_RED + "Invalid choice!" + RESET);
            }
        }

        // Task description
        System.out.println("Enter task info: ");
        while (taskInfo.trim().isEmpty()) {
            taskInfo = scanner.nextLine().trim();
            if (taskInfo.isEmpty()) {
                System.out.print(DARK_RED + "Task info cannot be empty. Please enter some information: " + RESET);
            }
        }

        saveTask(userID, taskDate, taskType, taskInfo);
    }

    // Save the task to the file
    public void saveTask(int userID, LocalDate taskDate, String type, String info) {
        String taskData = userID + " ; " + taskDate.toString() + " ; " + type + " ; " + info; // format it
        taskFile.addRecord(taskData);  // store in the text file
        System.out.println(GREEN + "Task saved successfully!" + RESET);
    }


    // Navigate to the previous month
    public void prevMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
    }

    // Navigate to the next month
    public void nextMonth() {
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
    }

}
