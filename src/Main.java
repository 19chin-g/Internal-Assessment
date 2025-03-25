import java.time.LocalDate;
import java.util.Scanner;

/* users.txt format:
username1 password1
username2 password2
 */

public class Main {
    public static void main(String[] args) {



        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        System.out.println("1) Login");
        System.out.println("2) Register");

        Boolean exit  = false;

        while(!exit) {
            Login user = new Login("users.txt");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> exit = user.login(); // log in
                case "2" -> user.register(); // register new user
                default -> System.out.print("Invalid choice. ");
            }
        }
        exit = false;



        LocalDate today = LocalDate.now();
        Calendar calendar = new Calendar(today.getMonthValue(), today.getYear());


        System.out.println();
        System.out.println("============CALENDAR===========");
        calendar.displayCalendar();

        System.out.println("1) Previous month");
        System.out.println("2) Next month");
        System.out.println("3) Set task");
        System.out.println("4) Exit");
        Task task = new Task();



        while (!exit) {

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> { calendar.prevMonth(); calendar.displayCalendar(); }
                case "2" -> { calendar.nextMonth(); calendar.displayCalendar(); }
                case "3" -> { task.addTask(); }
                case "4" -> exit = true;

                default -> System.out.println("Invalid choice. ");
            }

        }
    }
}