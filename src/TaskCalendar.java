import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;

public class TaskCalendar {
    int userID;
    int month;
    int year;
    Database taskFile;
    private JTextArea upcomingTasksArea;
    private JTextArea overdueTasksArea;

    LocalDate today = LocalDate.now();
    int currentDay = today.getDayOfMonth();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    JPanel calendarPanel = new JPanel();

    // Keep track of buttons/labels so fonts can resize properly
    java.util.List<JButton> dayButtons = new ArrayList<>();
    java.util.List<JLabel> dayLabels = new ArrayList<>();

    public TaskCalendar(int userID, int month, int year, String filename) {
        this.userID = userID;
        this.month = month;
        this.year = year;
        this.taskFile = new Database(filename);
    }

    // Link upcoming tasks panel
    public void setUpcomingTextArea(JTextArea upcomingTasksArea) {
        this.upcomingTasksArea = upcomingTasksArea;
    }

    // Link overdue tasks panel
    public void setOverdueTextArea(JTextArea overdueTasksArea) {
        this.overdueTasksArea = overdueTasksArea;
    }

    // Current year getter
    public int getSelectedYear() {
        return year;
    }

    // Current month getter
    public int getSelectedMonth() {
        return month;
    }

    // Creates a 6x7 calendar grid for this month
    public ArrayList<ArrayList<Integer>> createCalendar() {
        ArrayList<ArrayList<Integer>> calendar = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            calendar.add(new ArrayList<>(Collections.nCopies(7, 0)));
        }

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        int daysInMonth = firstDayOfMonth.lengthOfMonth();
        int startDay = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0

        int dayCounter = 1;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if ((row == 0 && col < startDay) || dayCounter > daysInMonth) {
                    calendar.get(row).set(col, 0);
                } else {
                    calendar.get(row).set(col, dayCounter++);
                }
            }
        }
        return calendar;
    }

    // Go to previous month
    public void goToPreviousMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
    }

    // Go to next month
    public void goToNextMonth() {
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
    }

    // Builds and returns the calendar JPanel
    public JPanel getCalendarPanel() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        calendarPanel.setBackground(Color.DARK_GRAY);

        dayButtons.clear();
        dayLabels.clear();

        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(Color.WHITE);
            calendarPanel.add(label);
            dayLabels.add(label);
        }

        // Calendar grid
        ArrayList<ArrayList<Integer>> calendar = createCalendar();
        for (ArrayList<Integer> week : calendar) {
            for (Integer day : week) {
                if (day == 0) {
                    JLabel empty = new JLabel();
                    calendarPanel.add(empty);
                    dayLabels.add(empty);
                } else {
                    JButton button = getButton(day);
                    calendarPanel.add(button);
                    dayButtons.add(button);
                }
            }
        }

        // Resize fonts dynamically
        calendarPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeFonts();
            }
        });

        return calendarPanel;
    }

    // Resizes fonts for buttons/labels based on panel size
    private void resizeFonts() {
        int panelWidth = calendarPanel.getWidth();
        int panelHeight = calendarPanel.getHeight();
        int cellSize = Math.min(panelWidth / 7, panelHeight / 7);

        int fontSize = Math.max(cellSize / 4, 12);
        Font newFont = new Font("SansSerif", Font.BOLD, fontSize);

        for (JButton button : dayButtons) {
            button.setFont(newFont);
        }
        for (JLabel label : dayLabels) {
            label.setFont(newFont);
        }
    }

    // Refreshes the calendar display
    public void refreshCalendar() {
        calendarPanel.removeAll();
        getCalendarPanel();
        calendarPanel.revalidate();
        calendarPanel.repaint();
    }

    // Updates side panel with tasks coming up soon
    public void refreshSidePanel(JTextArea textArea, int daysAhead) {
        ArrayList<String[]> upcomingTasks = getUpcomingTasks(daysAhead);
        textArea.setText("");
        for (String[] task : upcomingTasks) {
            textArea.append(formatDate(LocalDate.parse(task[1])) + " - " + task[2] + ": " + task[3] + "\n");
        }
    }

    // Updates side panel with overdue tasks
    public void refreshOverduePanel(JTextArea textArea) {
        ArrayList<String[]> overdueTasks = getOverdueTasks();
        textArea.setText("");
        for (String[] task : overdueTasks) {
            textArea.append(formatDate(LocalDate.parse(task[1])) + " - " + task[2] + ": " + task[3] + "\n");
        }
    }

    // Refreshes calendar and both side panels
    public void refreshAll() {
        refreshCalendar();
        if (upcomingTasksArea != null) refreshSidePanel(upcomingTasksArea, 7);
        if (overdueTasksArea != null) refreshOverduePanel(overdueTasksArea);
    }

    // Checks if a date has any tasks
    private boolean hasTask(LocalDate date) {
        ArrayList<String[]> tasks = getTasksOnDate(date);
        return !tasks.isEmpty();
    }

    // Get tasks on a specific date
    private ArrayList<String[]> getTasksOnDate(LocalDate date) {
        ArrayList<String[]> tasks = new ArrayList<>();
        ArrayList<String> allRecords = taskFile.readAllRecords();
        for (String record : allRecords) {
            String[] parts = record.split(",");
            if (parts.length == 4) {
                int recordUserId = Integer.parseInt(parts[0]);
                LocalDate recordDate = LocalDate.parse(parts[1]);
                if (recordUserId == userID && recordDate.equals(date)) {
                    tasks.add(parts);
                }
            }
        }
        return tasks;
    }

    // Creates a day button with hover/click behaviour
    private JButton getButton(Integer day) {
        JButton button = new JButton(day.toString());
        button.setFocusPainted(false);
        button.setBackground(Color.LIGHT_GRAY);

        LocalDate date = LocalDate.of(year, month, day);

        // Highlight today
        if (year == currentYear && month == currentMonth && day == currentDay) {
            button.setBackground(Color.CYAN);
        }

        // If task exists, add border
        if (hasTask(date)) {
            Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
            button.setBorder(redBorder);
        }

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GRAY);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (year == currentYear && month == currentMonth && day == currentDay) {
                    button.setBackground(Color.CYAN);
                } else {
                    button.setBackground(Color.LIGHT_GRAY);
                }
            }
        });

        // Click opens task creation window
        button.addActionListener(e -> openTaskCreation(date));

        return button;
    }

    // Formats date for display (e.g. "5 September 2025")
    private String formatDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix = "th";
        if (day % 10 == 1 && day != 11) suffix = "st";
        else if (day % 10 == 2 && day != 12) suffix = "nd";
        else if (day % 10 == 3 && day != 13) suffix = "rd";

        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return day + suffix + " " + monthName + " " + date.getYear();
    }

    // Opens task creation/view window
    private void openTaskCreation(LocalDate date) {
        JFrame taskFrame = new JFrame("Tasks for " + formatDate(date));
        taskFrame.setSize(400, 300);
        taskFrame.setLocationRelativeTo(null);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> taskList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(taskList);

        ArrayList<String[]> tasks = getTasksOnDate(date);
        for (String[] task : tasks) {
            listModel.addElement(task[2] + ": " + task[3]);
        }

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> {
            String taskType = JOptionPane.showInputDialog("Enter task type:");
            if (taskType != null && !taskType.trim().isEmpty()) {
                String taskInfo = JOptionPane.showInputDialog("Enter task info:");
                if (taskInfo != null && !taskInfo.trim().isEmpty()) {
                    addTask(userID, date, taskType, taskInfo);
                    listModel.addElement(taskType + ": " + taskInfo);
                    refreshAll();
                }
            }
        });

        JButton removeButton = new JButton("Remove Selected Task");
        removeButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedTask = listModel.getElementAt(selectedIndex);
                String[] parts = selectedTask.split(": ", 2);
                if (parts.length == 2) {
                    String taskType = parts[0];
                    String taskInfo = parts[1];
                    int taskIndex = findTaskIndex(userID, date, taskType, taskInfo);
                    if (taskIndex != -1) {
                        taskFile.removeRecord(taskIndex);
                        listModel.remove(selectedIndex);
                        refreshAll();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        taskFrame.add(scrollPane, BorderLayout.CENTER);
        taskFrame.add(buttonPanel, BorderLayout.SOUTH);
        taskFrame.setVisible(true);
    }

    // Adds task to file and sorts list
    public void addTask(int userId, LocalDate date, String taskType, String taskInfo) {
        String record = userId + "," + date.toString() + "," + taskType + "," + taskInfo;
        taskFile.addRecord(record);
        taskFile.sortRecords();
    }

    // Get all upcoming tasks within daysAhead
    public ArrayList<String[]> getUpcomingTasks(int daysAhead) {
        ArrayList<String[]> upcomingTasks = new ArrayList<>();
        ArrayList<String> allRecords = taskFile.readAllRecords();
        LocalDate limitDate = today.plusDays(daysAhead);

        for (String record : allRecords) {
            String[] parts = record.split(",");
            if (parts.length == 4) {
                int recordUserId = Integer.parseInt(parts[0]);
                LocalDate recordDate = LocalDate.parse(parts[1]);
                if (recordUserId == userID && !recordDate.isBefore(today) && !recordDate.isAfter(limitDate)) {
                    upcomingTasks.add(parts);
                }
            }
        }
        return upcomingTasks;
    }

    // Get all overdue tasks before today
    public ArrayList<String[]> getOverdueTasks() {
        ArrayList<String[]> overdueTasks = new ArrayList<>();
        ArrayList<String> allRecords = taskFile.readAllRecords();

        for (String record : allRecords) {
            String[] parts = record.split(",");
            if (parts.length == 4) {
                int recordUserId = Integer.parseInt(parts[0]);
                LocalDate recordDate = LocalDate.parse(parts[1]);
                if (recordUserId == userID && recordDate.isBefore(today)) {
                    overdueTasks.add(parts);
                }
            }
        }
        return overdueTasks;
    }

    // Finds index of a task record in the file
    private int findTaskIndex(int userId, LocalDate date, String taskType, String taskInfo) {
        ArrayList<String> allRecords = taskFile.readAllRecords();
        String targetRecord = userId + "," + date.toString() + "," + taskType + "," + taskInfo;
        for (int i = 0; i < allRecords.size(); i++) {
            if (allRecords.get(i).equals(targetRecord)) {
                return i;
            }
        }
        return -1;
    }
}
