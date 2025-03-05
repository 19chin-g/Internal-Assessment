import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar {
    int month;
    int year;

    String RESET = "\u001B[0m";
    String RED = "\u001B[31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String CYAN = "\033[0;36m";
    String BLACK = "\u001B[30m";
    String WHITE_BG = "\u001B[47m";

    public Calendar(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public void displayCalendar() {

        // sets up 2D arraylist with max 6 rows for each week
        ArrayList<ArrayList<Integer>> monthList = new ArrayList<>();
        for(int rows = 0; rows < 6; rows++) {
            monthList.addFirst(new ArrayList<Integer>());
        }
        
        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(year, month, 1); //first day of the month
        int monthLength = firstDay.lengthOfMonth();
        int startDay = (firstDay.getDayOfWeek().getValue());

        System.out.println(CYAN + firstDay.getDayOfWeek().getValue() + " " + firstDay.getMonth() + " " + year + RESET);
        System.out.println("Mon  Tue  Wed  Thu  Fri  Sat  Sun");

        // Print spaces before the first day
        for (int i = 1; i < startDay; i++) {
            System.out.print("     ");
            monthList.get(0).add(null);
        }

        // Print days
        int nextList = 0;
        for (int day = 1; day <= monthLength; day++) {
            if (day == today.getDayOfMonth() && today.getMonthValue() == month && today.getYear() == year) {
                System.out.print("  " + BLACK + WHITE_BG + day +  RESET + "  "); // Highlight today
            } else {
                System.out.printf(" %2d  ", day);
            }
            monthList.get(nextList).add(day);

            if ((day + startDay) % 7 == 1) {
                System.out.println(); // New line every week
                nextList++;
            }
        }
        System.out.println();
        System.out.println(monthList);

    }

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