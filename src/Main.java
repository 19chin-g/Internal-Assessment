import java.time.LocalDate;
import java.util.Scanner;

/* users.txt format:
username1 password1
username2 password2
 */



public class Main {
    public static void main(String[] args) {

        //COLOURS
        String RESET = "\u001B[0m";
        String DARK_RED = "\033[0;31m";
        String BOLD = "\033[1m";  // Bold


        Scanner scanner = new Scanner(System.in);
        System.out.println(BOLD + "Welcome to Study Planner!" + RESET);
        System.out.println("1) Login");
        System.out.println("2) Register");

        Boolean exit  = false;

        while(!exit) {
            Login user = new Login("users.txt");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> exit = user.login(); // log in
                case "2" -> user.register(); // register new user
                default -> System.out.print(DARK_RED + "Invalid choice. " + RESET);
            }
        }
        exit = false;



        LocalDate today = LocalDate.now();
        TaskCalendar calendar = new TaskCalendar(today.getDayOfMonth(), today.getMonthValue(), today.getYear()) {
        };

        System.out.println();
        calendar.displayCalendar();

        while (!exit) {
            System.out.println("1) Previous month");
            System.out.println("2) Next month");
            System.out.println("3) Set task");
            System.out.println("4) Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> { calendar.prevMonth(); calendar.displayCalendar(); }
                case "2" -> { calendar.nextMonth(); calendar.displayCalendar(); }
                case "3" -> { calendar.addTask(); }
                case "4" -> exit = true;
                default -> System.out.println(DARK_RED + "Invalid choice. " + RESET);
            }

        }
    }
}