import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;

public class TaskCalendar {
    // User and date tracking
    int userID;
    int month;
    int year;

    Database taskFile; // Handles task storage
    private JTextArea upcomingTasksArea; // Area to show upcoming tasks
    private JTextArea overdueTasksArea; // Area to show overdue tasks

    LocalDate today = LocalDate.now(); // Today's date
    JPanel calendarPanel = new JPanel(); // Main calendar grid

    // Store references to day buttons and day labels for resizing fonts dynamically
    java.util.List<JButton> dayButtons = new ArrayList<>();
    java.util.List<JLabel> dayLabels = new ArrayList<>();

    // Constructor
    public TaskCalendar(int userID, int month, int year, String filename) {
        this.userID = userID;
        this.month = month;
        this.year = year;
        this.taskFile = new Database(filename); // Initialize task database
    }

    // Set references to the text areas for updating tasks
    public void setUpcomingTextArea(JTextArea upcomingTasksArea) {
        this.upcomingTasksArea = upcomingTasksArea;
    }

    public void setOverdueTextArea(JTextArea overdueTasksArea) {
        this.overdueTasksArea = overdueTasksArea;
    }

    public int getSelectedYear() {
        return year;
    }

    public int getSelectedMonth() {
        return month;
    }

    // Create a 2D ArrayList representing the calendar for the current month
    public ArrayList<ArrayList<Integer>> createCalendar() {
        ArrayList<ArrayList<Integer>> calendar = new ArrayList<>();
        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        int startDay = firstDay.getDayOfWeek().getValue(); // Monday=1 ... Sunday=7

        int day = 1;
        for (int week = 0; week < 6; week++) {
            ArrayList<Integer> weekRow = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                if (week == 0 && i < startDay) {
                    weekRow.add(null); // Empty cells before the first day
                } else if (day <= daysInMonth) {
                    weekRow.add(day++);
                } else {
                    weekRow.add(null); // Empty cells after the last day
                }
            }
            calendar.add(weekRow);
        }
        return calendar;
    }

    // Navigate to previous month
    public void goToPreviousMonth() {
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }
    }

    // Navigate to next month
    public void goToNextMonth() {
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
    }

    // Build and return the calendar JPanel
    public JPanel getCalendarPanel() {
        calendarPanel.setLayout(new GridLayout(7, 7));
        calendarPanel.setBackground(new Color(34, 34, 34)); // Dark background

        dayButtons.clear();
        dayLabels.clear();

        // Add weekday labels
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setForeground(Color.LIGHT_GRAY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            calendarPanel.add(label);
            dayLabels.add(label);
        }

        // Add day buttons to calendar
        ArrayList<ArrayList<Integer>> calendar = createCalendar();
        for (ArrayList<Integer> week : calendar) {
            for (Integer day : week) {
                if (day == null) {
                    JLabel emptyLabel = new JLabel(""); // Empty day cell
                    calendarPanel.add(emptyLabel);
                } else {
                    JButton button = getButton(day); // Generate button for the day
                    calendarPanel.add(button);
                    dayButtons.add(button);
                }
            }
        }

        // Resize fonts when panel size changes
        calendarPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeFonts();
            }
        });

        return calendarPanel;
    }

    // Adjust fonts of labels and buttons dynamically
    private void resizeFonts() {
        int height = calendarPanel.getHeight();
        if (height == 0) return;

        int headerFontSize = Math.max(12, height / 25);
        Font headerFont = new Font("Segoe UI", Font.BOLD, headerFontSize);
        for (JLabel label : dayLabels) {
            label.setFont(headerFont);
        }

        int buttonFontSize = Math.max(10, height / 35);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, buttonFontSize);
        for (JButton button : dayButtons) {
            button.setFont(buttonFont);
        }
    }

    // Refresh the calendar display
    public void refreshCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(7, 7));
        calendarPanel.setBackground(new Color(34, 34, 34));

        dayButtons.clear();
        dayLabels.clear();

        // Re-add weekday labels
        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setForeground(Color.LIGHT_GRAY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            calendarPanel.add(label);
            dayLabels.add(label);
        }

        // Re-add day buttons
        ArrayList<ArrayList<Integer>> calendar = createCalendar();
        for (ArrayList<Integer> week : calendar) {
            for (Integer day : week) {
                if (day == null) {
                    JLabel emptyLabel = new JLabel("");
                    calendarPanel.add(emptyLabel);
                } else {
                    JButton button = getButton(day);
                    calendarPanel.add(button);
                    dayButtons.add(button);
                }
            }
        }

        calendarPanel.revalidate();
        calendarPanel.repaint();
        resizeFonts();
    }

    // Refresh the upcoming tasks panel
    public void refreshSidePanel(JTextArea textArea, int daysAhead) {
        textArea.removeAll();
        ArrayList<String[]> upcoming = getUpcomingTasks(daysAhead);
        StringBuilder sb = new StringBuilder();

        if (upcoming.isEmpty()) {
            sb.append("No upcoming tasks.");
        } else {
            for (String[] task : upcoming) {
                LocalDate rawDate = LocalDate.parse(task[1]);
                String formattedDate = formatDate(rawDate);
                sb.append(" • ").append(formattedDate)
                        .append(" - ").append(task[2])
                        .append(": ").append(task[3]).append("\n");
            }
        }
        textArea.setText(sb.toString());
    }

    // Refresh the overdue tasks panel
    public void refreshOverduePanel(JTextArea textArea) {
        textArea.removeAll();
        ArrayList<String[]> overdue = getOverdueTasks();
        StringBuilder sb = new StringBuilder();

        if (overdue.isEmpty()) {
            sb.append("No overdue tasks.");
        } else {
            for (String[] task : overdue) {
                LocalDate rawDate = LocalDate.parse(task[1]);
                String formattedDate = formatDate(rawDate);
                sb.append(" • ").append(formattedDate)
                        .append(" - ").append(task[2])
                        .append(": ").append(task[3]).append("\n");
            }
        }
        textArea.setText(sb.toString());
    }

    // Refresh both calendar and side panels
    public void refreshAll() {
        refreshCalendar();
        if (upcomingTasksArea != null) refreshSidePanel(upcomingTasksArea, 14);
        if (overdueTasksArea != null) refreshOverduePanel(overdueTasksArea);
    }

    // Check if there is a task on a specific date
    private boolean hasTask(LocalDate date) {
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] parts = record.split(" ; ");
                if (parts.length >= 2 && parts[0].equals(String.valueOf(userID)) && parts[1].equals(date.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Get all tasks for a given date
    private ArrayList<String[]> getTasksOnDate(LocalDate date) {
        ArrayList<String[]> tasks = new ArrayList<>();
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] parts = record.split(" ; ");
                if (parts.length >= 4 && parts[0].equals(String.valueOf(userID)) && parts[1].equals(date.toString())) {
                    tasks.add(parts);
                }
            }
        }
        return tasks;
    }

    // Create day buttons with colors, hover effects, and click action
    private JButton getButton(Integer day) {
        Color defaultDayColor = new Color(50, 50, 50);
        Color futureTaskColor = new Color(70, 110, 80); // Greenish for upcoming task
        Color pastTaskColor = new Color(110, 70, 70);   // Redish for past task
        Color todayBorderColor = Color.WHITE;

        LocalDate thisDay = LocalDate.of(year, month, day);
        boolean isToday = thisDay.equals(LocalDate.now());
        boolean hasTask = hasTask(thisDay);
        boolean isPast = thisDay.isBefore(LocalDate.now());

        JButton button = new JButton(String.valueOf(day));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setFocusable(false);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(true);

        // Set background color depending on task presence and date
        if (hasTask) {
            button.setBackground(isPast ? pastTaskColor : futureTaskColor);
        } else {
            button.setBackground(defaultDayColor);
        }

        Border defaultBorder = isToday ? BorderFactory.createLineBorder(todayBorderColor, 3) :
                BorderFactory.createEmptyBorder(3,3,3,3);
        button.setBorder(defaultBorder);

        Color originalColor = button.getBackground();
        Color hoverColor = originalColor.brighter();
        Border hoverBorder = BorderFactory.createLineBorder(Color.GRAY, 2);
        Border finalDefaultBorder = defaultBorder;

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(hoverBorder);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
                button.setBorder(finalDefaultBorder);
            }
        });

        // Open task creation dialog on click
        LocalDate selectedDate = LocalDate.of(year, month, day);
        button.addActionListener(e -> openTaskCreation(selectedDate));

        return button;
    }

    // Format a LocalDate to "daySuffix Month Year" format
    private String formatDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix;
        if (day >= 11 && day <= 13) suffix = "th";
        else {
            switch (day % 10) {
                case 1 -> suffix = "st";
                case 2 -> suffix = "nd";
                case 3 -> suffix = "rd";
                default -> suffix = "th";
            }
        }
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = date.getYear();
        return day + suffix + " " + monthName + " " + year;
    }

    // Open task creation dialog for a given date
    private void openTaskCreation(LocalDate date) {
        // Dialog setup
        JDialog dialog = new JDialog();
        dialog.setTitle("Task Creation");
        dialog.setModal(true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        mainPanel.setBackground(new Color(34,34,34));

        JLabel titleLabel = new JLabel("Task Creation - " + formatDate(date));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(34,34,34));

        // Display existing tasks
        ArrayList<String[]> loggedTasks = getTasksOnDate(date);
        if (!loggedTasks.isEmpty()) {
            JLabel existingLabel = new JLabel("Existing Tasks (tick to complete):");
            existingLabel.setForeground(Color.LIGHT_GRAY);
            existingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            existingLabel.setFocusable(false);
            inputPanel.add(existingLabel);
            inputPanel.add(Box.createRigidArea(new Dimension(0,5)));

            JPanel tasksCheckboxPanel = new JPanel();
            tasksCheckboxPanel.setLayout(new BoxLayout(tasksCheckboxPanel, BoxLayout.Y_AXIS));
            tasksCheckboxPanel.setBackground(new Color(34,34,34));
            tasksCheckboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (int i=0;i<loggedTasks.size();i++) {
                String[] task = loggedTasks.get(i);
                String taskType = task[2];
                String taskInfo = task[3];
                JCheckBox taskCheckBox = new JCheckBox(taskType + ": " + taskInfo);
                taskCheckBox.setForeground(Color.WHITE);
                taskCheckBox.setBackground(new Color(34,34,34));
                taskCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                taskCheckBox.setFocusable(false);
                tasksCheckboxPanel.add(taskCheckBox);

                int recordIndex = findTaskIndex(userID,date,taskType,taskInfo);
                taskCheckBox.addActionListener(e -> {
                    if (taskCheckBox.isSelected() && recordIndex!=-1) {
                        taskFile.removeRecord(recordIndex);
                        tasksCheckboxPanel.remove(taskCheckBox);
                        tasksCheckboxPanel.revalidate();
                        tasksCheckboxPanel.repaint();
                        refreshAll();
                    }
                });
            }
            inputPanel.add(tasksCheckboxPanel);
        } else {
            JLabel noTasksLabel = new JLabel("No tasks for this day.");
            noTasksLabel.setForeground(Color.LIGHT_GRAY);
            noTasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            inputPanel.add(noTasksLabel);
        }

        // Task type selection
        inputPanel.add(Box.createRigidArea(new Dimension(0,15)));
        JLabel taskTypeLabel = new JLabel("Task Type:");
        taskTypeLabel.setForeground(Color.LIGHT_GRAY);
        taskTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskTypeLabel);

        String[] taskTypes = { "Work & Productivity", "Health & Wellbeing", "Social & Leisure" };
        JComboBox<String> taskTypeCombo = new JComboBox<>(taskTypes);
        taskTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskTypeCombo);
        inputPanel.add(Box.createRigidArea(new Dimension(0,10)));

        // Task information input
        JLabel taskInfoLabel = new JLabel("Task Information:");
        taskInfoLabel.setForeground(Color.LIGHT_GRAY);
        taskInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskInfoLabel);

        JTextArea taskInfoArea = new JTextArea(4,20);
        taskInfoArea.setLineWrap(true);
        taskInfoArea.setWrapStyleWord(true);
        taskInfoArea.setBackground(new Color(50,50,50));
        taskInfoArea.setForeground(Color.WHITE);
        taskInfoArea.setCaretColor(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(taskInfoArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
        inputPanel.add(scrollPane);
        inputPanel.add(Box.createRigidArea(new Dimension(0,15)));

        // Save task button
        JButton saveButton = new JButton("Save Task");
        saveButton.setBackground(new Color(60,120,60));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setPreferredSize(new Dimension(100,30));

        saveButton.addActionListener(e -> {
            String taskType = (String) taskTypeCombo.getSelectedItem();
            String taskInfo = taskInfoArea.getText().trim();
            if (taskInfo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,"Please enter task information.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            addTask(userID,date,taskType,taskInfo);
            dialog.dispose();
            refreshAll();
        });

        inputPanel.add(saveButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    // Add a new task to the database
    public void addTask(int userId, LocalDate date, String taskType, String taskInfo) {
        String taskRecord = userId + " ; " + date + " ; " + taskType + " ; " + taskInfo;
        taskFile.addRecord(taskRecord);
        taskFile.sortRecords();
    }

    // Retrieve upcoming tasks within N days
    public ArrayList<String[]> getUpcomingTasks(int daysAhead) {
        ArrayList<String[]> upcomingTasks = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysAhead);

        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] parts = record.split(" ; ");
                if (parts.length >= 4 && parts[0].equals(String.valueOf(userID))) {
                    LocalDate taskDate;
                    try { taskDate = LocalDate.parse(parts[1]); }
                    catch (Exception e) { continue; }
                    if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                        upcomingTasks.add(parts);
                    }
                }
            }
        }
        return upcomingTasks;
    }

    // Retrieve overdue tasks
    public ArrayList<String[]> getOverdueTasks() {
        ArrayList<String[]> overdueTasks = new ArrayList<>();
        LocalDate today = LocalDate.now();
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] parts = record.split(" ; ");
                if (parts.length >= 4 && parts[0].equals(String.valueOf(userID))) {
                    LocalDate taskDate;
                    try { taskDate = LocalDate.parse(parts[1]); }
                    catch (Exception e) { continue; }
                    if (taskDate.isBefore(today)) overdueTasks.add(parts);
                }
            }
        }
        return overdueTasks;
    }

    // Find the index of a task in the database
    private int findTaskIndex(int userID, LocalDate date, String type, String info) {
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (int i=0;i<records.size();i++) {
                String[] parts = records.get(i).split(" ; ");
                if (parts.length>=4 && parts[0].equals(String.valueOf(userID)) && parts[1].equals(date.toString())
                        && parts[2].equals(type) && parts[3].equals(info)) return i;
            }
        }
        return -1;
    }
}
