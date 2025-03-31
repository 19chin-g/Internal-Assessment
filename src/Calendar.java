import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Calendar {
    int day;
    int month;
    int year;

    List<Task> tasks = new ArrayList<>();  // Store tasks in a list

    String RESET = "\u001B[0m";
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String CYAN = "\033[0;36m";
    String BLACK = "\u001B[30m";
    String WHITE_BG = "\u001B[47m";

    public Calendar(int day, int month, int year) {
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
        LocalDate today = LocalDate.now();
        int currentDay = today.getDayOfMonth();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        if (selectedMonth.getMonthValue() == currentMonth && year == currentYear) {
            System.out.println(CYAN + day + " " + selectedMonth.getMonth() + " " + year + RESET);
        } else {
            System.out.println(CYAN + selectedMonth.getMonth() + " " + year + RESET);
        }

        System.out.println("Mon  Tue  Wed  Thu  Fri  Sat  Sun");

        for (int week = 0; week < monthList.size(); week++) {
            for (int d = 0; d < monthList.get(week).size(); d++) {
                Integer currentDayInMonth = monthList.get(week).get(d);

                if (currentDayInMonth == null) {
                    System.out.print("     ");
                } else if (currentDayInMonth == currentDay && currentMonth == month && currentYear == year) {
                    // Highlight the current day
                    System.out.print(" " + BLACK + WHITE_BG + currentDayInMonth + RESET + "  ");
                } else if (hasTask(LocalDate.of(year, month, currentDayInMonth))) {
                    // Highlight days with tasks
                    System.out.print(" " + GREEN + currentDayInMonth + "!" + RESET + " ");
                } else {
                    System.out.printf(" %2d  ", currentDayInMonth);  // Display normally
                }
            }
            System.out.println();
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

        // Prompt for date
        System.out.print("Enter date (DD/MM/YY) to add task: ");
        String dateString = scanner.nextLine().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        try {
            LocalDate taskDate = LocalDate.parse(dateString, formatter);

            // Prompt for task type and information
            System.out.println("Enter task type: ");
            System.out.println("1) Academic");
            System.out.println("2) Social & Personal");
            System.out.println("3) Health & Wellbeing");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> { String taskType = scanner.nextLine().trim();; }
                case "2" -> { calendar.nextMonth(); calendar.displayCalendar(); }
                case "3" -> { calendar.addTask(); }
                case "4" -> exit = true;

                default -> System.out.println("Invalid choice. ");
            }

            // Create the task
            Task task = new Task(taskDate, taskType, taskInfo);
            tasks.add(task);  // Add task to list

            System.out.println("Task added for: " + taskDate);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format!");
        }
        System.out.println(tasks);
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
