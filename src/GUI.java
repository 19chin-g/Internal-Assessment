import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class GUI extends JFrame {
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JButton buttonOK;
    private int fontSize = 14;
    private JPanel calendarPanel;

    private LocalDate currentDate;
    private int currentMonth;
    private int currentYear;

    private final int WIDGET_HEIGHT = 35;
    private final Login login;

    public GUI() {
        login = new Login("users.txt");

        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLayout(null);
        setLocationRelativeTo(null);  // Center on screen

        // TITLE
        JLabel title = new JLabel("Study Planner");
        title.setBounds(250, 30, 300, WIDGET_HEIGHT);
        title.setFont(new Font("Century Gothic", Font.BOLD, 28));
        add(title);

        // USERNAME LABEL AND TEXT FIELD
        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(170, 90, 80, WIDGET_HEIGHT);
        labelUsername.setFont(new Font("Century Gothic", Font.BOLD, fontSize));
        add(labelUsername);

        textUsername = new JTextField();
        textUsername.setBounds(250, 90, 200, WIDGET_HEIGHT);
        add(textUsername);

        // PASSWORD LABEL AND TEXT FIELD
        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(170, 140, 80, WIDGET_HEIGHT);
        labelPassword.setFont(new Font("Century Gothic", Font.BOLD, fontSize));
        add(labelPassword);

        textPassword = new JPasswordField();
        textPassword.setBounds(250, 140, 200, WIDGET_HEIGHT);
        add(textPassword);

        // SHOW PASSWORD CHECKBOX
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Century Gothic", Font.BOLD, fontSize));
        showPassword.setFocusable(false);

        showPassword.setBounds(250, 180, 200, WIDGET_HEIGHT);
        add(showPassword);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                textPassword.setEchoChar((char) 0);  // Show password
            } else {
                textPassword.setEchoChar('•');  // Hide password
            }
        });

        // LOG IN BUTTON
        buttonOK = new JButton("Log in");
        buttonOK.setBounds(300, 220, 100, WIDGET_HEIGHT);
        add(buttonOK);


        buttonOK.addActionListener(e -> {
            // stores username and password
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();

            // if user found in database then login
            if (login.findUser(username, password)) {
                int userID = login.getUserID();
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " + username,
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                openMainMenu(userID); // logs in
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // SIGN UP BUTTON
        buttonOK = new JButton("Sign up");
        buttonOK.setBounds(300, 260, 100, WIDGET_HEIGHT);
        add(buttonOK);

        buttonOK.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();

            if (login.isUserTaken(username)) {
                JOptionPane.showMessageDialog(this,
                        "Username has been taken!",
                        "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Enter username or password",
                        "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Sign up successful!",
                        "Sign up Successful", JOptionPane.INFORMATION_MESSAGE);
                login.register(username, password);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openMainMenu(int userID) {
        currentDate = LocalDate.now();
        currentMonth = currentDate.getMonthValue();
        currentYear = currentDate.getYear();

        TaskCalendar tc = new TaskCalendar(currentYear, currentMonth);
        JPanel calendarPanel = tc.getCalendarPanel();

        // Title and logout
        JLabel title = new JLabel(currentDate.getMonth() + " " + currentYear);
        title.setFont(new Font("Century Gothic", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Century Gothic", Font.BOLD, 12));
        setFocusable(false);
        logoutButton.addActionListener(e -> {
            getContentPane().removeAll();
            repaint();
            revalidate();
            new GUI();
            dispose();
        });


        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // SIDE PANEL
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS)); // Vertical layout
        sidePanel.setBackground(new Color(230, 230, 250));
        sidePanel.setPreferredSize(new Dimension(getWidth()/5, getHeight()));

        // Sidebar buttons
        JButton tasksBtn = new JButton("My Tasks");
        JButton todayBtn = new JButton("Study Timer");


        for (JButton btn : new JButton[]{todayBtn, tasksBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(120, 30));
            sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            sidePanel.add(btn);
        }

        // Final layout
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(calendarPanel, BorderLayout.CENTER);
        mainContent.add(sidePanel, BorderLayout.WEST);

        setContentPane(mainContent);
        revalidate();
        repaint();
    }











    // Entry point for testing
    public static void main(String[] args) {
        new GUI();
    }
}
