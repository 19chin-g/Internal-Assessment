import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

public class GUI extends JFrame {
    // Colours
    public final Color backgroundColor = new Color(34, 34, 34);
    public final Color sidePanelColor = new Color(24, 24, 24);
    public final Color topPanelColor = new Color(45, 45, 45);
    public final Color accentColor = new Color(0, 120, 215);
    public final Color textColor = Color.WHITE;

    private final Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final int SIDE_PANEL_FRACTION = 4;

    // GUI components
    private JPanel loginPanel;
    private JPanel sidePanel;
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JTextArea upcomingTasks;
    private JLabel dynamicTitle, upcomingLabel;
    private JButton timerBtn;

    private final CardLayout cardLayout;
    private final Login login;
    private TaskCalendar taskCalendar;

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

    // Login screen creation
    private void setupLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginPanel.add(title, gbc);

        // Username
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

        // Password
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

        // Show password toggle
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setFont(modernFont);
        showPassword.setBackground(backgroundColor);
        showPassword.setForeground(textColor);
        showPassword.addActionListener(e ->
                textPassword.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho)
        );
        gbc.gridx = 1; gbc.gridy++;
        loginPanel.add(showPassword, gbc);

        // Login and sign up button
        JButton loginButton = createStyledButton("Log in", accentColor);
        JButton signupButton = createStyledButton("Sign up", new Color(60, 60, 60));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        loginPanel.add(buttonPanel, gbc);

        // Login button logic
        loginButton.addActionListener(e -> {
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();
            if (login.findUser(username, password)) {
                setupMainMenu(login.getUserID());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Validation checks - no same username
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

    // Opens the main planner interface
    private void setupMainMenu(int userID) {
        LocalDate now = LocalDate.now();
        taskCalendar = new TaskCalendar(userID, now.getMonthValue(), now.getYear(), "tasks.txt");
        JPanel calendarPanel = taskCalendar.getCalendarPanel();

        // Top panel
        dynamicTitle = new JLabel(now.getMonth() + " " + now.getYear(), SwingConstants.CENTER);
        dynamicTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dynamicTitle.setForeground(textColor);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(topPanelColor);
        topPanel.add(dynamicTitle, BorderLayout.CENTER);
        topPanel.add(getLogoutButton(), BorderLayout.EAST);

        // Side panel setup
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(sidePanelColor);

        timerBtn = createStyledButton("Study Timer", new Color(50, 50, 50));
        timerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerBtn.addActionListener(e -> new StudyTimer());

        JPanel timerWrapper = new JPanel(new BorderLayout());
        timerWrapper.setBackground(sidePanelColor);
        timerWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        timerWrapper.add(timerBtn, BorderLayout.CENTER);

        // Upcoming tasks section
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

        JScrollPane scrollPane = new JScrollPane(upcomingTasks);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        JPanel upcomingWrapper = new JPanel(new BorderLayout());
        upcomingWrapper.setBackground(sidePanelColor);
        upcomingWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        upcomingWrapper.add(upcomingLabel, BorderLayout.NORTH);
        upcomingWrapper.add(scrollPane, BorderLayout.CENTER);
        upcomingWrapper.setPreferredSize(new Dimension(200, 200));

        taskCalendar.setUpcomingTextArea(upcomingTasks);
        sidePanel.add(timerWrapper);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(upcomingWrapper);
        updateUpcomingTasksDisplay(taskCalendar);

        // Main layout
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

    /** Helper to apply consistent field style **/
    private void styleField(JTextField field) {
        field.setFont(modernFont);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    /** Returns a consistently styled button **/
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(modernFont);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Returns logout button with hover effect and action **/
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

    /** Updates calendar and upcoming tasks */
    public void updateUpcomingTasksDisplay(TaskCalendar calendar) {
        calendar.refreshAll();
    }
}
