import java.time.LocalDate;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import static javax.swing.SwingConstants.CENTER;


public class MainMenu extends JFrame {
        private JPanel calendarPanel;
        private JLabel monthLabel;
        private LocalDate currentDate;
        private int userID;  // store logged in user ID


    public MainMenu(int userID) {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.width * 0.75);
        int height = (int) (screenSize.height * 0.8);

        setSize(width, height);
        setLocation(screenSize.width - width, (screenSize.height - height) / 2);



        setTitle("Study Planner Calendar");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        currentDate = LocalDate.now();

        // Top panel
        JPanel topPanel = new JPanel();
        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Century Gothic", Font.BOLD, 18));
        topPanel.add(monthLabel);
        add(topPanel, BorderLayout.EAST);

        // Calendar panel
        calendarPanel = new JPanel(new GridLayout(7, 7)); // 7x7: header + 6 weeks
        add(calendarPanel, BorderLayout.CENTER);
        TaskCalendar tc = new TaskCalendar(2025, 6); // example year/month
        JPanel calendarPanel = tc.getCalendarPanel();

        JFrame frame = new JFrame("Task Calendar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(calendarPanel);
        frame.setVisible(true);

        //updateCalendar(currentDate.getYear(), currentDate.getMonthValue());
        setVisible(true);
    }

    private void updateCalendar(int year, int month) {
        calendarPanel.removeAll();

        // update month label
        LocalDate date = LocalDate.of(year, month, 1);
        monthLabel.setText(date.getMonth() + " " + year);

        // day headers
        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : days) {
            JLabel label = new JLabel(day, CENTER);
            label.setFont(new Font("Century Gothic", Font.BOLD, 12));
            calendarPanel.add(label);
        }

        // generates a calendar
        TaskCalendar taskCal = new TaskCalendar(year, month);
        ArrayList<ArrayList<Integer>> calendarData = taskCal.createCalendar();

        for (ArrayList<Integer> week : calendarData) {
            for (int i = 0; i < 7; i++) {
                Integer day = (i < week.size()) ? week.get(i) : null;
                String text = (day == null) ? "" : String.valueOf(day);
                JLabel label = new JLabel(text, CENTER);
                calendarPanel.add(label);
            }
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
        }
    }
/*


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

        // USER LABEL
        JLabel nameDisplay = new JLabel("User: " + userID);
        nameDisplay.setBounds(10, 10, 200, WIDGET_HEIGHT);
        nameDisplay.setFont(new Font("Arial", Font.BOLD, 14));
        nameDisplay.setFocusable(false);
        add(nameDisplay);


        //ADD TASK BUTTON
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.setBounds(10, 120, 150, WIDGET_HEIGHT);
        addTaskButton.setFocusable(false);
        add(addTaskButton);

        //VIEW TASKS BUTTON
        JButton viewTasksButton = new JButton("View Tasks");
        viewTasksButton.setBounds(10, 160, 150, WIDGET_HEIGHT);
        viewTasksButton.setFocusable(false);
        add(viewTasksButton);


        //COMPLETE TASKS BUTTON
        JButton completeButton = new JButton("Complete task");
        completeButton.setBounds(10, 200, 150, WIDGET_HEIGHT);
        completeButton.setFocusable(false);
        add(completeButton);

        //LOGOUT BUTTON
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(10, 240, 150, WIDGET_HEIGHT);
        logoutButton.setFocusable(false);
        add(logoutButton);

        setVisible(true);

        LocalDate today = LocalDate.now();
        TaskCalendar calendar = new TaskCalendar(userID, today.getDayOfMonth(), today.getMonthValue(), today.getYear(), "tasks.txt");

        addTaskButton.addActionListener(e -> {
            System.out.println("Add Task " + userID);
            calendar.addTask();

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
*/