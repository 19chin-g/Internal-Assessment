import java.util.Scanner;

import static javax.swing.UIManager.getInt;

public class Task{
    int day;
    int month;
    int year;
    int type;
    String info;


    public Task() {
    }

    public void addTask() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter date in DD/MM/YY format: ");
        String date = scanner.nextLine().trim();
        String temp = Character.toString(date.charAt(0)) +  Character.toString((date.charAt(1)));
        day = Integer.parseInt(temp);

        System.out.println(day);

        System.out.println("1) Academic");
        System.out.println("2) Social & Personal");
        System.out.println("3) Health & Wellbeing");
        System.out.print("Enter task type: ");
        type = Integer.parseInt(scanner.nextLine().trim());


    }

}
