import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.Month;

public class GUI extends JFrame {
    // Color scheme
    public final Color backgroundColor = new Color(34, 34, 34);
    public final Color sidePanelColor = new Color(24, 24, 24);
    public final Color topPanelColor = new Color(45, 45, 45);
    public final Color accentColor = new Color(0, 120, 215);
    public final Color textColor = Color.WHITE;

    // Fonts and layout
    private final Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final int SIDE_PANEL_FRACTION = 4; // side panel width relative to window

    private JPanel loginPanel;
    private JPanel sidePanel;
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JTextArea upcomingTasks;
    private JTextArea overdueTasks;
    private JLabel monthLabel, upcomingLabel, overdueLabel;
    private JButton timerBtn;

    private final CardLayout cardLayout;
    private final Login login;

    public GUI() {
        login = new Login("users.txt");
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        setupLoginPanel();
        add(loginPanel, "login");

        setVisible(true);
    }

    // Creates login screen
    private void setupLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);

        // Username label + field
        gbc.gridwidth = 1; gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(modernFont);
        userLabel.setForeground(textColor);
        loginPanel.add(userLabel, gbc);

        textUsername = new JTextField(15);
        styleField(textUsername);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textUsername, gbc);

        // Password label + field
        gbc.gridx = 0; gbc.gridy++; gbc.anchor = GridBagConstraints.EAST;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(modernFont);
        passLabel.setForeground(textColor);
        loginPanel.add(passLabel, gbc);

        textPassword = new JPasswordField(15);
        styleField(textPassword);
        char defaultEcho = textPassword.getEchoChar();
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(textPassword, gbc);

        // Show password checkbox
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(modernFont);
        showPassword.setBackground(backgroundColor);
        showPassword.setForeground(textColor);
        showPassword.addActionListener(e ->
                textPassword.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho)
        );
        gbc.gridx = 1; gbc.gridy++;
        loginPanel.add(showPassword, gbc);

        // Login + signup buttons
        JButton loginButton = createStyledButton("Log in", accentColor);
        JButton signupButton = createStyledButton("Sign up", new Color(60, 60, 60));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        // Login logic
        loginButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();
            if (login.findUser(username, password)) {
                setupMainMenu(login.getUserID());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Signup logic with validation
        signupButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();
            if (login.isUserTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username has been taken!", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else if (username.isBlank() || password.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter username and password", "Sign up Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                login.register(username, password);
                JOptionPane.showMessageDialog(this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    // Updates the month label text
    private void updateMonthLabel(TaskCalendar taskCalendar) {
        monthLabel.setText(Month.of(taskCalendar.getSelectedMonth()) + " " + taskCalendar.getSelectedYear());
    }

    // Creates the main planner interface
    private void setupMainMenu(int userID) {
        LocalDate now = LocalDate.now();
        TaskCalendar taskCalendar = new TaskCalendar(userID, now.getMonthValue(), now.getYear(), "tasks.txt");
        JPanel calendarPanel = taskCalendar.getCalendarPanel();

        // === Top panel with month navigation ===
        monthLabel = new JLabel(now.getMonth() + " " + now.getYear(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthLabel.setForeground(textColor);

        JButton prevMonthBtn = createStyledButton("<", new Color(50, 50, 50));
        JButton nextMonthBtn = createStyledButton(">", new Color(50, 50, 50));

        prevMonthBtn.addActionListener(e -> {
            taskCalendar.goToPreviousMonth();
            taskCalendar.refreshAll();
            updateMonthLabel(taskCalendar);
        });

        nextMonthBtn.addActionListener(e -> {
            taskCalendar.goToNextMonth();
            taskCalendar.refreshAll();
            updateMonthLabel(taskCalendar);
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(topPanelColor);

        JPanel centerNavPanel = new JPanel(new BorderLayout());
        centerNavPanel.setOpaque(false);
        centerNavPanel.add(prevMonthBtn, BorderLayout.WEST);
        centerNavPanel.add(monthLabel, BorderLayout.CENTER);
        centerNavPanel.add(nextMonthBtn, BorderLayout.EAST);

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutWrapper.setOpaque(false);
        logoutWrapper.add(getLogoutButton());

        topPanel.add(centerNavPanel, BorderLayout.CENTER);
        topPanel.add(logoutWrapper, BorderLayout.EAST);

        // === Side panel ===
        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setBackground(sidePanelColor);

        // Timer button
        timerBtn = createStyledButton("Study Timer", new Color(50, 50, 50));
        timerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerBtn.addActionListener(e -> new StudyTimer());

        JPanel timerWrapper = new JPanel(new BorderLayout());
        timerWrapper.setBackground(sidePanelColor);
        timerWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        timerWrapper.add(timerBtn, BorderLayout.CENTER);
        sidePanel.add(timerWrapper, BorderLayout.NORTH);

        // === Upcoming tasks section ===
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

        JScrollPane upcomingScrollPane = new JScrollPane(upcomingTasks);
        upcomingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        upcomingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        upcomingScrollPane.setBorder(null);
        upcomingScrollPane.getVerticalScrollBar().setUnitIncrement(10);

        JPanel upcomingWrapper = new JPanel(new BorderLayout());
        upcomingWrapper.setBackground(sidePanelColor);
        upcomingWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        upcomingWrapper.add(upcomingLabel, BorderLayout.NORTH);
        upcomingWrapper.add(upcomingScrollPane, BorderLayout.CENTER);

        taskCalendar.setUpcomingTextArea(upcomingTasks);
        updateTasksDisplay(taskCalendar);

        // === Overdue tasks section ===
        overdueLabel = new JLabel("Overdue Tasks");
        overdueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        overdueLabel.setForeground(textColor);

        overdueTasks = new JTextArea();
        overdueTasks.setFont(modernFont);
        overdueTasks.setEditable(false);
        overdueTasks.setBackground(new Color(40, 40, 40));
        overdueTasks.setForeground(textColor);
        overdueTasks.setLineWrap(true);
        overdueTasks.setWrapStyleWord(true);

        JScrollPane overdueScrollPane = new JScrollPane(overdueTasks);
        overdueScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        overdueScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        overdueScrollPane.setBorder(null);
        overdueScrollPane.getVerticalScrollBar().setUnitIncrement(10);

        JPanel overdueWrapper = new JPanel(new BorderLayout());
        overdueWrapper.setBackground(sidePanelColor);
        overdueWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        overdueWrapper.add(overdueLabel, BorderLayout.NORTH);
        overdueWrapper.add(overdueScrollPane, BorderLayout.CENTER);

        taskCalendar.setOverdueTextArea(overdueTasks);
        updateTasksDisplay(taskCalendar);

        // Container for both task sections
        JPanel tasksContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        tasksContainer.setBackground(sidePanelColor);
        tasksContainer.add(upcomingWrapper);
        tasksContainer.add(overdueWrapper);
        sidePanel.add(tasksContainer, BorderLayout.CENTER);

        // === Main layout ===
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(calendarPanel, BorderLayout.CENTER);
        mainContent.add(sidePanel, BorderLayout.WEST);

        // For scaling fonts and side panel to screen size
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int newSideWidth = width / SIDE_PANEL_FRACTION;
                sidePanel.setPreferredSize(new Dimension(newSideWidth, getHeight()));

                float scale = Math.max(8f, width / 70f);
                Font scaledFont = modernFont.deriveFont(scale);
                Font scaledBold = scaledFont.deriveFont(Font.BOLD);

                monthLabel.setFont(scaledBold.deriveFont(scale + 10f));
                upcomingLabel.setFont(scaledBold);
                overdueLabel.setFont(scaledBold);
                upcomingTasks.setFont(scaledFont);
                overdueTasks.setFont(scaledFont);
                timerBtn.setFont(scaledFont);

                SwingUtilities.updateComponentTreeUI(GUI.this);
            }
        });

        add(mainContent, "main");
        cardLayout.show(getContentPane(), "main");
        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
    }

    // Styles input fields
    private void styleField(JTextField field) {
        field.setFont(modernFont);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    // Creates a styled button
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(modernFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Creates logout button with hover effect
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
            textUsername.setText("");
            textPassword.setText("");
            cardLayout.show(getContentPane(), "login");
        });

        return logoutButton;
    }

    // Refreshes tasks + calendar
    public void updateTasksDisplay(TaskCalendar calendar) {
        calendar.refreshAll();
    }
}
