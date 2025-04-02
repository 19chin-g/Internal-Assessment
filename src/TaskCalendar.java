import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.format.DateTimeParseException;
import java.util.*;

public class TaskCalendar {
    int day;
    int month;
    int year;
    Database taskFile;

    LocalDate today = LocalDate.now();
    int currentDay = today.getDayOfMonth();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    List<Task> tasks = new ArrayList<>();  // Store tasks in a list

    //COLOURS
    String RESET = "\u001B[0m";
    String DARK_RED = "\033[0;31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String CYAN = "\033[0;36m";
    String BLACK = "\u001B[30m";
    String WHITE_BG = "\u001B[47m";
    String BOLD = "\033[1m";
    String LIGHT_GRAY = "\033[0;37m"; // Light Gray (Bright White)


    public TaskCalendar(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
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

        // prints full date with day if on current month otherwise display month and year only
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

                    if (hasTask(LocalDate.of(year, month, currentDayInMonth))) {
                        System.out.printf("[%s%2d%s] ", DARK_RED, currentDayInMonth, RESET);
                    } else {
                        System.out.printf("[%s%2d%s] ", CYAN, currentDayInMonth, RESET);
                    }

                } else if (hasTask(LocalDate.of(year, month, currentDayInMonth))) {
                    if (currentDayInMonth < currentDay && currentMonth == month && currentYear == year) {
                        //  tasks with passed due dates are red
                        System.out.printf(" %s%2d%s  ", DARK_RED, currentDayInMonth, RESET);
                    } else {
                        // future days with tasks are Green
                        System.out.printf(" %s%2d%s  ", GREEN, currentDayInMonth, RESET);
                    }
                } else {
                    // Normal days Light gray
                    System.out.printf(" %s%2d%s  ", LIGHT_GRAY, currentDayInMonth, RESET);
                }
            }
            System.out.println(); // Move to the next week row
        }
    }

    public boolean hasTask(LocalDate date) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDate().equals(date)) {
                return true;  // Return true if task is found
            }
        }
        return false;  // Return false if no task is found for the date
    }

    // Add a task to a specific date
    public void addTask() {
        Scanner scanner = new Scanner(System.in);

        LocalDate taskDate = null;
        String taskType = "";

        System.out.println(BOLD + YELLOW + "======== TASK CREATION ========" + RESET);
        System.out.print("Enter date (DD/MM/YY or D/M/YY) to add task: ");

        while (taskDate == null) {
            // Prompt for date
            String date = scanner.nextLine().trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy");

            try {
                taskDate = LocalDate.parse(date, formatter);
                System.out.println(taskDate);
                System.out.println(taskDate.getDayOfMonth() + "" + today.lengthOfMonth());
                if (taskDate.getDayOfMonth() > today.lengthOfMonth()) {
                    System.out.print(DARK_RED + "Invalid date! Please use a date within the month: " + RESET);
                }

            } catch (DateTimeParseException e) {
                System.out.print(DARK_RED + "Invalid date format! Please use the correct format: " + RESET);
            }
        }

        boolean exit = false;

        // Prompt for task type and information
        System.out.println("1) Academic");
        System.out.println("2) Social & Personal");
        System.out.println("3) Health & Wellbeing");

        while (!exit) {

            System.out.print("Enter task type: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    taskType = "Academic";
                    exit = true;
                }
                case "2" -> {
                    taskType = "Social & Personal";
                    exit = true;
                }
                case "3" -> {
                    taskType = "Health & Wellbeing";
                    exit = true;
                }
                default -> System.out.print(DARK_RED + "Invalid choice. " + RESET);
            }
        }

        System.out.println("Enter task info if needed: ");

        String taskInfo = scanner.nextLine().trim();

        // Create the task
        Task task = new Task(taskDate, taskType, taskInfo);
        tasks.add(task);  // Add task to list

    }

    public void saveTaskToFile(LocalDate date, String type, String info) {

    }


    // Navigate to previous or next month
    public void prevMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
    }

    public void nextMonth() {
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
    }

    // Display current date and time
    public void displayCurrentDate() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        System.out.println(date.format(formatter));
    }

    public void displayCurrentTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println(time.format(formatter));
    }
}

