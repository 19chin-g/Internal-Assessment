import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDate;

public class GUI extends JFrame {
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JPanel loginPanel;
    private final CardLayout cardLayout;
    private final Login login;

    Color backgroundColor = new Color(255, 255, 255); // Very light gray, almost white


    public GUI() {
        login = new Login("users.txt");

        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        LoginPanel();
        add(loginPanel, "login");

        setVisible(true);
    }

    private void LoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(Color.getHSBColor(50,60,30));
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
        JButton loginButton = new JButton("Log in");
        // SIGN UP BUTTON
        JButton signupButton = new JButton("Sign up");

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
        int currentDay = currentDate.getDayOfMonth();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        TaskCalendar tc = new TaskCalendar(userID, currentDay, currentMonth, currentYear, "tasks.txt");
        JPanel calendarPanel = tc.getCalendarPanel();

        JLabel title = new JLabel(currentDate.getMonth() + " " + currentYear);
        title.setFont(new Font("Century Gothic", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton logoutButton = getjButton();

        // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new LineBorder(Color.DARK_GRAY, 2));
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // SIDE PANEL
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(157, 157, 255));
        sidePanel.setMinimumSize(new Dimension(150, 0));
        sidePanel.setPreferredSize(new Dimension(getWidth() / 7, 5));

// TIMER BUTTON
        JButton timerBtn = new JButton("My Tasks");
        timerBtn.setFocusable(false);
        timerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerBtn.setBackground(Color.lightGray);

// Wrap button in panel with padding
        JPanel timerWrapper = new JPanel();
        timerWrapper.setLayout(new BorderLayout());
        timerWrapper.setBackground(new Color(157, 157, 255));
        timerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, getHeight() - 30, 10)); // Top, Left, Bottom, Right
        timerWrapper.add(timerBtn, BorderLayout.CENTER);

        sidePanel.add(timerWrapper);
        sidePanel.add(Box.createVerticalStrut(15)); // Space before next component if needed

// MAIN CONTENT PANEL
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(calendarPanel, BorderLayout.CENTER);
        mainContent.add(sidePanel, BorderLayout.WEST);

// Resize behavior
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int newWidth = getWidth() / 7;
                sidePanel.setPreferredSize(new Dimension(newWidth, getHeight()));
                sidePanel.revalidate();
            }
        });


        add(mainContent, "main");
        cardLayout.show(getContentPane(), "main");
    }


    private JButton getjButton() {
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Century Gothic", Font.BOLD, 12));
        logoutButton.setFocusable(false);
        logoutButton.setBackground(new Color(220, 53, 69)); // Soft red (like Bootstrap's danger)
        logoutButton.setForeground(Color.WHITE);            // White text
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 0, 0), 1), // Thin red border
                BorderFactory.createEmptyBorder(5, 15, 5, 15)            // Padding inside
        ));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));      // Hand cursor on hover

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 40, 55)); // Darker red on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69)); // Original red
            }
        });

        // Clear username and password fields, return to login screen
        logoutButton.addActionListener(e -> {
            textPassword.setText("");
            textUsername.setText("");
            cardLayout.show(getContentPane(), "login");
        });
        return logoutButton;
    }
}
