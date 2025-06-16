import java.time.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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


    public TaskCalendar(int year, int month) {
        this.userID = userID;
        this.day = day;
        this.month = month;
        this.year = year;
        //this.taskFile = new Database(filename);
    }

    public JPanel getCalendarPanel() {
        ArrayList<ArrayList<Integer>> calendarData = createCalendar();
        JPanel calendarPanel = new JPanel(new GridLayout(6, 7, 5, 5)); // 6 rows x 7 days

        for (ArrayList<Integer> week : calendarData) {
            for (Integer day : week) {
                if (day == null) {
                    calendarPanel.add(new JLabel("")); // empty cell
                } else {
                    JButton dayButton = new JButton(String.valueOf(day));
                    dayButton.addActionListener(e -> {
                        // Replace this with what you want to do
                        JOptionPane.showMessageDialog(null, "You clicked on day " + day);
                        // Optional: open task view / create task
                    });
                    calendarPanel.add(dayButton);
                }
            }
        }

        return calendarPanel;
    }


    public ArrayList<ArrayList<Integer>> createCalendar() {
        ArrayList<ArrayList<Integer>> monthList = new ArrayList<>();
        for (int rows = 0; rows < 6; rows++) {
            monthList.add(new ArrayList<>());
        }

        LocalDate selectedMonth = LocalDate.of(year, month, 1);
        int monthLength = selectedMonth.lengthOfMonth();
        int startDay = selectedMonth.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)

        for (int i = 1; i < startDay; i++) {
            monthList.get(0).add(null); // fill in empty cells
        }

        int nextList = 0;
        for (int day = 1; day <= monthLength; day++) {
            monthList.get(nextList).add(day);
            if ((day + startDay - 1) % 7 == 0) {
                nextList++;
            }
        }

        return monthList;
    }

/*
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

    // check if a task exists on a specific date for the current use
    public boolean hasTask(int userID, LocalDate date) {
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                if (taskDetails.length >= 3 && taskDetails[0].equals(String.valueOf(userID)) && taskDetails[1].equals(date.toString())) {
                    return true;  // task found for this exact user on the given date
                }
            }
        }
        return false;  // No task found for this user on this date
    }

    public void removeTask() {
        Scanner scanner = new Scanner(System.in);
        LocalDate taskDate = null;
        while (taskDate == null) {
            System.out.print("Enter date (DD/MM/YY or D/M/YY) of completed task: ");
            String input = scanner.nextLine().trim();
            taskDate = stringToLocalDate(input);
        }

        if (hasTask(userID, taskDate)) {
            ArrayList<String> records = taskFile.readAllRecords();
            int recordNum = 0;
            if (records != null) {
                for (String record : records) {

                    String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                    if (taskDetails.length >= 3 && taskDetails[0].equals(String.valueOf(userID)) && taskDetails[1].equals(taskDate.toString())) {
                        taskFile.removeRecord(recordNum);
                        System.out.println(GREEN + "Task removed successfully." + RESET);
                    }
                    recordNum++;

                }
            }
        } else {
            System.out.println(DARK_RED + "No tasks on specified date." + RESET);
        }
    }

        public void viewTask() {
            Scanner scanner = new Scanner(System.in);
            LocalDate taskDate = null;
            while (taskDate == null) {
                System.out.print("Enter date (DD/MM/YY or D/M/YY) of task: ");
                String input = scanner.nextLine().trim();
                taskDate = stringToLocalDate(input);
            }

            if (hasTask(userID, taskDate)) {
                ArrayList<String> records = taskFile.readAllRecords();
                int recordNum = 0;
                if (records != null) {
                    for (String record : records) {
                        recordNum++;
                        String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                        if (taskDetails.length >= 3 && taskDetails[0].equals(String.valueOf(userID)) && taskDetails[1].equals(taskDate.toString())) {

                            System.out.println(GREEN + "Task date: " + RESET + taskDetails[1]);
                            System.out.println(GREEN + "Task type: " + RESET + taskDetails[2]);
                            System.out.println(GREEN + "Task info: " + RESET + taskDetails[3]);

                        }

                    }
                }
            } else {
                System.out.println("No tasks on specified date.");
            }
        }

    */
    public void viewTask() {
        ArrayList<String> records = taskFile.readAllRecords();
        ArrayList<String> tasks = new ArrayList<>();
        if (records != null) {
            for (String record : records) {
                String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                if (taskDetails[0].equals(String.valueOf(userID))) {
                    tasks.add(record);
                    System.out.print(GREEN + "Task date: " + RESET + taskDetails[1] + " ");
                    System.out.print(GREEN + "Task type: " + RESET + taskDetails[2] + " ");
                    System.out.println(GREEN + "Task info: " + RESET + taskDetails[3] + " ");

                }

            }
            if (tasks.isEmpty() ) {
                System.out.println(DARK_RED + "No tasks found." + RESET);
            }
        }
    }



    public LocalDate stringToLocalDate(String date) {
        try {
            String[] dateParts = date.split("/");

            int inputDay = Integer.parseInt(dateParts[0]);
            int inputMonth = Integer.parseInt(dateParts[1]);
            int inputYear = Integer.parseInt(dateParts[2]);

            // Optional: reject if parts are too long (e.g. "123/45/67")
            if (dateParts[0].length() > 2 || dateParts[1].length() > 2 || dateParts[2].length() > 2) {
                System.out.println(DARK_RED + "1Invalid format! Use DD/MM/YY format. " + RESET);
                return null;
            }

            if (inputMonth < 1 || inputMonth > 12) {
                System.out.println(DARK_RED + "Invalid month! Use DD/MM/YY format. " + RESET);
                return null;
            }

            if (inputYear < 0 || inputYear > 99) {
                System.out.println(DARK_RED + "Invalid year! Use DD/MM/YY format. " + RESET);
                return null;
            }

            inputYear += 2000;
            int daysInMonth = YearMonth.of(inputYear, inputMonth).lengthOfMonth();

            if (inputDay < 1 || inputDay > daysInMonth) {
                System.out.println(DARK_RED + "Invalid day for given month/year. " + RESET);
                return null;
            }

            return LocalDate.of(inputYear, inputMonth, inputDay);

        } catch (Exception e) {
            System.out.println(DARK_RED + "Invalid input! Use DD/MM/YY format with numbers. " + RESET);
            return null;
        }
    }



    // Add a task to a specific date
    public void addTask() {
        Scanner scanner = new Scanner(System.in);
        LocalDate taskDate = null;
        String taskType = "";
        String taskInfo = "";

        System.out.println(BOLD + YELLOW + "======== TASK CREATION ========" + RESET);
        while (taskDate == null) {
            System.out.print("Enter date (DD/MM/YY or D/M/YY) to add task: ");
            String input = scanner.nextLine().trim();
            taskDate = stringToLocalDate(input);
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