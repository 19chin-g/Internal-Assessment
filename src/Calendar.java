import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Calendar {
    int month;
    int year;

    public static final String RESET = "\033[0m";  // Reset all styles
    public static final String BOLD_ITALIC = "\033[1;3m"; // Bold + Italic
    public static final String BOLD = "\033[0;1m"; // Bold + Italic

    public static final String BLUE = "\033[34;5;196m"; // Red color



    public Calendar(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public void displayCalendar() {

        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(year, month, 1); //first day of the month
        int monthLength = firstDay.lengthOfMonth();
        int startDay = (firstDay.getDayOfWeek().getValue());
        ArrayList<ArrayList<Integer>> monthList = createCalendar();

        System.out.println(BOLD + BLUE + today.getDayOfWeek() + " " + today.getDayOfWeek().getValue() + " " + firstDay.getMonth() + RESET);
        System.out.println(BOLD + firstDay.getMonth() + " " + year + RESET);

        System.out.println(BOLD + "Mon  Tue  Wed  Thu  Fri  Sat  Sun" + RESET);


        for (ArrayList<Integer> integers : monthList) {
            for (Integer integer : integers) {
                if (integer == null) {
                    System.out.print("     ");
                } else if (integer == today.getDayOfMonth() && today.getMonthValue() == month && today.getYear() == year) {
                    System.out.printf("%s%s[%2d]%s ", BOLD_ITALIC, BLUE, integer, RESET); // Bold, italic, red
                } else {
                    System.out.printf(" %2d  ", integer);
                }
            }
            System.out.println();
        }
    }

    public ArrayList<ArrayList<Integer>> createCalendar() {

        // sets up 2D arraylist with max 6 rows for each week
        ArrayList<ArrayList<Integer>> monthList = new ArrayList<>();
        for(int rows = 0; rows < 6; rows++) {
            monthList.addFirst(new ArrayList<Integer>());
        }

        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(year, month, 1); //first day of the month
        int monthLength = firstDay.lengthOfMonth();
        int startDay = (firstDay.getDayOfWeek().getValue());

        // Add spaces before the first day of month
        for (int i = 1; i < startDay; i++) {
            monthList.getFirst().add(null);
        }

        // Print days
        int nextList = 0;
        for (int day = 1; day <= monthLength; day++) {
            monthList.get(nextList).add(day);
            if ((day + startDay) % 7 == 1) {
                nextList++;
            }
        }
        return monthList;

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