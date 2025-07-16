import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

public class GUI extends JFrame {
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JPanel loginPanel;
    private final CardLayout cardLayout;
    private final Login login;
    private TaskCalendar taskCalendar;

    private final Color backgroundColor = new Color(34, 34, 34);
    private final Color sidePanelColor = new Color(24, 24, 24);
    private final Color topPanelColor = new Color(45, 45, 45);
    private final Color accentColor = new Color(0, 120, 215);
    private final Color textColor = Color.WHITE;

    private Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final int SIDE_PANEL_FRACTION = 4;

    private JLabel dynamicTitle;
    private JLabel upcomingLabel;
    private JTextArea upcomingTasks;
    private JButton timerBtn;
    private JPanel sidePanel;
    private JPanel topPanel;
    private JPanel calendarPanel;

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
        loginPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(textColor);
        userLabel.setFont(modernFont);
        loginPanel.add(userLabel, gbc);

        textUsername = new JTextField(15);
        styleField(textUsername);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(textColor);
        passLabel.setFont(modernFont);
        loginPanel.add(passLabel, gbc);

        textPassword = new JPasswordField(15);
        char defaultEchoChar = textPassword.getEchoChar();
        styleField(textPassword);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textPassword, gbc);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(modernFont);
        showPassword.setFocusable(false);
        showPassword.setBackground(backgroundColor);
        showPassword.setForeground(textColor);
        showPassword.addActionListener(e ->
                textPassword.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEchoChar)
        );
        gbc.gridx = 1;
        gbc.gridy++;
        loginPanel.add(showPassword, gbc);

        JButton loginButton = createStyledButton("Log in", accentColor);
        JButton signupButton = createStyledButton("Sign up", new Color(60, 60, 60));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();
            if (login.findUser(username, password)) {
                int userID = login.getUserID();
                openMainMenu(userID);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();
            if (login.isUserTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username has been taken!", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter username or password", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                login.register(username, password);
                JOptionPane.showMessageDialog(this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void updateUpcomingTasksDisplay(TaskCalendar calendar, int daysAhead) {
        ArrayList<String[]> upcoming = calendar.getUpcomingTasks(daysAhead);
        StringBuilder sb = new StringBuilder();

        if (upcoming.isEmpty()) {
            sb.append("No upcoming tasks in the next ").append(daysAhead).append(" days.");
        } else {
            for (String[] task : upcoming) {
                sb.append("• ").append(task[1]).append(" - ").append(task[2]).append(": ").append(task[3]).append("\n");
            }
        }

        upcomingTasks.setText(sb.toString());
    }

    public void refreshUI() {
        updateUpcomingTasksDisplay(taskCalendar, 14);
    }

    private void openMainMenu(int userID) {
        LocalDate now = LocalDate.now();
        taskCalendar = new TaskCalendar(userID, now.getMonthValue(), now.getYear(), "tasks.txt");
        taskCalendar.setGuiReference(this); // 🔁 Allow TaskCalendar to call refreshUI()

        calendarPanel = taskCalendar.getCalendarPanel();

        dynamicTitle = new JLabel(now.getMonth() + " " + now.getYear(), SwingConstants.CENTER);
        dynamicTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dynamicTitle.setForeground(textColor);

        JButton logoutButton = getLogoutButton();

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(topPanelColor);
        topPanel.add(dynamicTitle, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(sidePanelColor);

        timerBtn = createStyledButton("Study Timer", new Color(50, 50, 50));
        timerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel timerWrapper = new JPanel(new BorderLayout());
        timerWrapper.setBackground(sidePanelColor);
        timerWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        timerWrapper.add(timerBtn, BorderLayout.CENTER);

        upcomingLabel = new JLabel("Upcoming Tasks");
        upcomingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        upcomingLabel.setForeground(textColor);

        upcomingTasks = new JTextArea();
        upcomingTasks.setFont(modernFont);
        upcomingTasks.setEditable(false);
        upcomingTasks.setBackground(new Color(40, 40, 40));
        upcomingTasks.setForeground(textColor);
        upcomingTasks.setLineWrap(true);
        upcomingTasks.setWrapStyleWord(true);

        JPanel upcomingWrapper = new JPanel(new BorderLayout());
        upcomingWrapper.setBackground(sidePanelColor);
        upcomingWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        upcomingWrapper.add(upcomingLabel, BorderLayout.NORTH);
        upcomingWrapper.add(new JScrollPane(upcomingTasks), BorderLayout.CENTER);

        sidePanel.add(timerWrapper);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(upcomingWrapper);

        updateUpcomingTasksDisplay(taskCalendar, 14);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(calendarPanel, BorderLayout.CENTER);
        mainContent.add(sidePanel, BorderLayout.WEST);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int newSideWidth = width / SIDE_PANEL_FRACTION;
                sidePanel.setPreferredSize(new Dimension(newSideWidth, getHeight()));

                float scale = Math.max(8f, width / 70f);
                Font scaledFont = modernFont.deriveFont(scale);
                Font scaledBold = scaledFont.deriveFont(Font.BOLD);

                dynamicTitle.setFont(scaledBold.deriveFont(scale + 10f));
                upcomingLabel.setFont(scaledBold);
                upcomingTasks.setFont(scaledFont);
                timerBtn.setFont(scaledFont);

                SwingUtilities.updateComponentTreeUI(GUI.this);
            }
        });

        add(mainContent, "main");
        cardLayout.show(getContentPane(), "main");
        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
    }

    private void styleField(JTextField field) {
        field.setFont(modernFont);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    public void refreshCalendar(int newMonth, int newYear) {
        taskCalendar = new TaskCalendar(taskCalendar.userID, newMonth, newYear, "tasks.txt");
        taskCalendar.setGuiReference(this);

        JPanel newCalendarPanel = taskCalendar.getCalendarPanel();
        Container contentPane = getContentPane();
        JPanel mainContent = (JPanel) contentPane.getComponent(1);
        mainContent.remove(1);
        mainContent.add(newCalendarPanel, BorderLayout.CENTER);

        updateUpcomingTasksDisplay(taskCalendar, 14);
        mainContent.revalidate();
        mainContent.repaint();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(modernFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton getLogoutButton() {
        JButton logoutButton = createStyledButton("Log Out", new Color(178, 34, 34));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(150, 30, 30));
            }
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(178, 34, 34));
            }
        });
        logoutButton.addActionListener(e -> {
            textPassword.setText("");
            textUsername.setText("");
            cardLayout.show(getContentPane(), "login");
        });
        return logoutButton;
    }
}
