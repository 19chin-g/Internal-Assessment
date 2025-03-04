import java.time.*;
import java.time.format.DateTimeFormatter;

public class Calendar {
    int month;
    int year;

    public Calendar(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public void displayCalendar() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = LocalDate.of(year, month, 1); //first day of the month
        int monthLength = firstDay.lengthOfMonth();
        int startDay = (firstDay.getDayOfWeek().getValue());

        System.out.println(firstDay.getMonth() + " " + year);
        System.out.println("Mon  Tue  Wed  Thu  Fri  Sat  Sun");

        // Print spaces before the first day
        for (int i = 1; i < startDay; i++) {
            System.out.print("     ");
        }

        // Print days
        for (int day = 1; day <= monthLength; day++) {
            if (day == today.getDayOfMonth() && today.getMonthValue() == month && today.getYear() == year) {
                System.out.printf("(%2d) ", day); // Highlight today
            } else {
                System.out.printf(" %2d  ", day);
            }

            if ((day + startDay) % 7 == 1) {
                System.out.println(); // New line every week
            }
        }
        System.out.println();
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