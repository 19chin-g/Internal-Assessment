import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class GUI extends JFrame {
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JButton loginButton, signupButton;
    private JPanel loginPanel;
    private CardLayout cardLayout;
    private final Login login;

    public GUI() {
        login = new Login("users.txt");

        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        initLoginPanel();
        add(loginPanel, "login");

        setVisible(true);
    }

    private void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        int fontSize = 14;

        // TITLE
        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("Century Gothic", Font.BOLD, 28));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(title, gbc);

        // USERNAME
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Username:"), gbc);

        textUsername = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textUsername, gbc);

        // PASSWORD
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(new JLabel("Password:"), gbc);

        textPassword = new JPasswordField(15);
        char defaultEchoChar = textPassword.getEchoChar();
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textPassword, gbc);

        // SHOW PASSWORD CHECKBOX
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(new Font("Arial", Font.BOLD, fontSize));
        showPassword.setFocusable(false);
        showPassword.addActionListener(e -> {
            textPassword.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEchoChar);
        });

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(showPassword, gbc);

        // LOGIN BUTTON
        loginButton = new JButton("Log in");
        signupButton = new JButton("Sign up");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(buttonPanel, gbc);

        // LOGIN ACTION
        loginButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();

            if (login.findUser(username, password)) {
                int userID = login.getUserID();
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + username);
                openMainMenu(userID);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // SIGNUP ACTION
        signupButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();

            if (login.isUserTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username has been taken!", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter username or password", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                login.register(username, password);
                JOptionPane.showMessageDialog(this, "Sign up successful!", "Sign up Successful", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void openMainMenu(int userID) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        TaskCalendar tc = new TaskCalendar(currentYear, currentMonth);
        JPanel calendarPanel = tc.getCalendarPanel();

        JLabel title = new JLabel(currentDate.getMonth() + " " + currentYear);
        title.setFont(new Font("Century Gothic", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Century Gothic", Font.BOLD, 12));
        logoutButton.setFocusable(false);
        logoutButton.addActionListener(e -> {
            cardLayout.show(getContentPane(), "login");
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(230, 230, 250));
        sidePanel.setPreferredSize(new Dimension(getWidth() / 5, getHeight()));

        JButton tasksBtn = new JButton("My Tasks");
        JButton todayBtn = new JButton("Study Timer");

        for (JButton btn : new JButton[]{todayBtn, tasksBtn}) {
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(120, 30));
            sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
            sidePanel.add(btn);
        }

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(calendarPanel, BorderLayout.CENTER);
        mainContent.add(sidePanel, BorderLayout.WEST);

        add(mainContent, "main");
        cardLayout.show(getContentPane(), "main");
    }
}
