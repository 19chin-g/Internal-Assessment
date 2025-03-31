import java.time.LocalDate;


public class Task {
    LocalDate date;  // Store date as LocalDate
    String type;
    String info;

    public Task(LocalDate date, String type, String info) {
        this.date = date;
        this.type = type;
        this.info = info;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}