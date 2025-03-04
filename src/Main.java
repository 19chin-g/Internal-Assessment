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
        System.out.println("3) Exit");

        Boolean exit  = false;

        while(!exit) {
            Login user = new Login("users.txt");

            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            if (choice.equals("1")) {
                exit = user.login(); // log in
            } else if (choice.equals("2")) {
                user.register(); // register new user
            } else if (choice.equals("3")) {
                exit = true;
            } else {
                System.out.print("Invalid choice. ");
            }

        }

        Calendar calendar = new Calendar();
        calendar.displayCurrentDate();
        calendar.displayCurrentTime();
        calendar.displayCalendar();

    }
}