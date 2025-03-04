import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.DayOfWeek;
import java.time.YearMonth;


public class Calendar {

    public void displayCurrentDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy");
        System.out.println(date.format(formatter));
    }
    public void displayCurrentTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        System.out.println(date.format(formatter));
    }
    public void displayCalendar() {
        LocalDate today = LocalDate.now();
        System.out.println(today);
        YearMonth month = YearMonth.now();
        System.out.println(month);
        int daysInMonth = month.lengthOfMonth();
        System.out.println(daysInMonth);
        LocalDate firstDay = month.atDay(1);
        System.out.println(firstDay);
        DayOfWeek startDay = firstDay.getDayOfWeek();
        System.out.println(startDay);
    }

}
