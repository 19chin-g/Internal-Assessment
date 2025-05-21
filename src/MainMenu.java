import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    private final int WIDGET_HEIGHT = 30;
    private int userID;  // store logged-in user ID

    public MainMenu(int userID) {
        this.userID = userID;

        setTitle("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLayout(null);
        setLocationRelativeTo(null);

        // TITLE
        JLabel title = new JLabel("Main Menu");
        title.setBounds(280, 50, 200, WIDGET_HEIGHT);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setFocusable(false);
        add(title);


        //ADD TASK BUTTON
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.setBounds(280, 120, 150, WIDGET_HEIGHT);
        addTaskButton.setFocusable(false);
        add(addTaskButton);

        //VIEW TASKS BUTTON
        JButton viewTasksButton = new JButton("View Tasks");
        viewTasksButton.setBounds(280, 160, 150, WIDGET_HEIGHT);
        viewTasksButton.setFocusable(false);
        add(viewTasksButton);


        //COMPLETE TASKS BUTTON
        JButton completeButton = new JButton("Complete task");
        completeButton.setBounds(280, 200, 150, WIDGET_HEIGHT);
        completeButton.setFocusable(false);
        add(completeButton);

        //LOGOUT BUTTON
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(280, 240, 150, WIDGET_HEIGHT);
        logoutButton.setFocusable(false);
        add(logoutButton);

        setVisible(true);

        addTaskButton.addActionListener(e -> {
            System.out.println("Add Task " + userID);

        });

        viewTasksButton.addActionListener(e -> {
            System.out.println("View Tasks " + userID);

        });

        logoutButton.addActionListener(e -> {
            System.out.println("Logout " + userID);

            this.dispose();
        });
    }
}
