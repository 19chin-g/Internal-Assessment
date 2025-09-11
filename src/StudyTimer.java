import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;

public class StudyTimer extends JFrame {
    private Timer timer;
    private int timeLeft;
    private int totalTime;
    private boolean isPaused = true;
    private boolean onBreak = false;
    private int pomodoroCount = 0;

    private final JLabel titleLabel;
    private final JLabel timerLabel;
    private final JComboBox<Integer> minuteSelector;
    private final JButton startButton;
    private final JButton pauseButton;
    private final JButton resetButton;
    private final JButton endSessionButton;
    private final JLabel statusLabel;
    private final JLabel nextBreakLabel;

    // Colours and fonts
    public final Color backgroundColour = new Color(34, 34, 34);
    public final Color btnColour = new Color(0, 120, 215);
    public final Color textColor = Color.WHITE;
    public final Font titleFont = new Font("Segoe UI", Font.BOLD, 33);
    public final Font timerFont = new Font("Segoe UI", Font.BOLD, 64);
    public final Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
    public final Font buttonFont = new Font("Segoe UI", Font.PLAIN, 18);

    // Sets up the timer window
    public StudyTimer() {
        setTitle("Pomodoro Timer");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel layout
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(backgroundColour);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title
        titleLabel = new JLabel("Pomodoro Timer");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Timer display
        timerLabel = new JLabel("00:00");
        timerLabel.setFont(timerFont);
        timerLabel.setForeground(textColor);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Duration selector
        minuteSelector = new JComboBox<>(new Integer[]{10, 20, 25, 30, 45, 60});
        minuteSelector.setSelectedItem(25);
        minuteSelector.setMaximumSize(new Dimension(120, 35));
        minuteSelector.setBackground(new Color(30, 30, 30));
        minuteSelector.setForeground(textColor);
        minuteSelector.setFont(labelFont);
        minuteSelector.setFocusable(false);
        minuteSelector.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel selectLabel = new JLabel("Select Study Duration (minutes)");
        selectLabel.setFont(labelFont);
        selectLabel.setForeground(textColor);
        selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectLabel.setBorder(new EmptyBorder(10, 0, 15, 0));

        // Status + next break info
        statusLabel = new JLabel("Timer is paused.");
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(new Color(255, 255, 255));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(15, 0, 5, 0));

        nextBreakLabel = new JLabel("");
        nextBreakLabel.setFont(labelFont);
        nextBreakLabel.setForeground(new Color(180, 180, 180));
        nextBreakLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nextBreakLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(backgroundColour);

        Dimension btnSize = new Dimension(90, 40);
        startButton = createButton("Start");
        pauseButton = createButton("Pause");
        resetButton = createButton("Reset");
        endSessionButton = createButton("End session");

        startButton.setPreferredSize(btnSize);
        pauseButton.setPreferredSize(btnSize);
        resetButton.setPreferredSize(btnSize);
        endSessionButton.setPreferredSize(new Dimension(180, 40));

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(endSessionButton);

        // Add components
        mainPanel.add(titleLabel);
        mainPanel.add(selectLabel);
        mainPanel.add(minuteSelector);
        mainPanel.add(timerLabel);
        mainPanel.add(statusLabel);
        mainPanel.add(nextBreakLabel);
        mainPanel.add(buttonPanel);
        add(mainPanel);

        // Setup actions and defaults
        setupActions();
        totalTime = ((int) minuteSelector.getSelectedItem()) * 60;
        timeLeft = totalTime;
        updateTimerLabel();
        updateNextBreakLabel();
        pauseButton.setEnabled(false);

        setVisible(true);
    }

    // Creates a styled button
    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(buttonFont);
        btn.setBackground(btnColour);
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Adds button functionality
    private void setupActions() {
        // Start or resume timer
        startButton.addActionListener(e -> {
            if (timer == null) startTimer();
            else if (isPaused) resumeTimer();
        });

        // Pause timer
        pauseButton.addActionListener(e -> {
            if (timer != null && !isPaused) pauseTimer();
        });

        // Reset timer
        resetButton.addActionListener(e -> resetTimer());

        // End session early (switch to break or next session)
        endSessionButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
                timer = null;
            }
            if (onBreak) {
                updateStatus("Break skipped. Back to work.");
                onBreak = false;
            } else {
                pomodoroCount++;
                onBreak = true;
                updateStatus("Session skipped. Take a break.");
            }
            timeLeft = onBreak ? getBreakLength() : ((int) minuteSelector.getSelectedItem()) * 60;
            totalTime = timeLeft;
            updateTimerLabel();
            updateNextBreakLabel();
            timer = null;
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            minuteSelector.setEnabled(!onBreak);
        });

        // Update duration when selector changes
        minuteSelector.addActionListener(e -> {
            if (!onBreak && (timer == null || isPaused)) {
                totalTime = ((int) minuteSelector.getSelectedItem()) * 60;
                timeLeft = totalTime;
                updateTimerLabel();
                updateStatus("Duration set to " + (totalTime / 60) + " minutes.");
                updateNextBreakLabel();
            }
        });
    }

    // Starts a new timer session
    private void startTimer() {
        isPaused = false;
        totalTime = onBreak ? getBreakLength() : ((int) minuteSelector.getSelectedItem()) * 60;
        timeLeft = totalTime;

        timer = new Timer(1, evt -> {
            if (!isPaused) {
                timeLeft--;
                updateTimerLabel();
                if (timeLeft <= 0) {
                    timer.stop();
                    Toolkit.getDefaultToolkit().beep();
                    if (onBreak) {
                        updateStatus("Break over. Ready to study!");
                        onBreak = false;
                    } else {
                        pomodoroCount++;
                        onBreak = true;
                        updateStatus("Study session over. Break time!");
                    }
                    JOptionPane.showMessageDialog(this, onBreak ? "Time for a break!" : "Back to work!", "Study Timer", JOptionPane.INFORMATION_MESSAGE);
                    timeLeft = onBreak ? getBreakLength() : ((int) minuteSelector.getSelectedItem()) * 60;
                    totalTime = timeLeft;
                    updateTimerLabel();
                    updateNextBreakLabel();
                    timer = null;
                    startButton.setEnabled(true);
                    pauseButton.setEnabled(false);
                    minuteSelector.setEnabled(!onBreak);
                }
            }
        });

        timer.start();
        updateStatus(onBreak ? "Break started." : "Timer started.");
        updateNextBreakLabel();
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        minuteSelector.setEnabled(!onBreak);
    }

    // Resumes a paused timer
    private void resumeTimer() {
        isPaused = false;
        updateStatus("Resumed.");
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
    }

    // Pauses the timer
    private void pauseTimer() {
        isPaused = true;
        updateStatus("Paused.");
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
    }

    // Resets timer to selected time
    private void resetTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        isPaused = true;
        onBreak = false;
        timeLeft = ((int) minuteSelector.getSelectedItem()) * 60;
        totalTime = timeLeft;
        updateTimerLabel();
        updateNextBreakLabel();
        updateStatus("Timer reset.");
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        minuteSelector.setEnabled(true);
    }

    // Break length - 5 min, 15 min every 4th break
    private int getBreakLength() {
        return (pomodoroCount % 4 == 0) ? 15 * 60 : 5 * 60;
    }

    // Updates timer display
    private void updateTimerLabel() {
        int mins = timeLeft / 60;
        int secs = timeLeft % 60;
        timerLabel.setText(String.format("%02d:%02d", mins, secs));
    }

    // Updates status text
    private void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    // Updates "next break" info
    private void updateNextBreakLabel() {
        if (!onBreak) {
            int nextBreak = (pomodoroCount + 1) % 4 == 0 ? 15 : 5;
            nextBreakLabel.setText("Next break: " + nextBreak + " min");
        } else {
            nextBreakLabel.setText("");
        }
    }
}
