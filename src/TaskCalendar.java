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



    LocalDate today = LocalDate.now();
    int currentDay = today.getDayOfMonth();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    JPanel calendarPanel = new JPanel();

    // Store references to day buttons for resizing fonts
    java.util.List<JButton> dayButtons = new ArrayList<>();
    java.util.List<JLabel> dayLabels = new ArrayList<>();

    public TaskCalendar(int userID, int month, int year, String filename) {
        this.userID = userID;
        this.month = month;
        this.year = year;
        this.taskFile = new Database(filename);
    }

    public void setUpcomingTextArea(JTextArea upcomingTasksArea) {
        this.upcomingTasksArea = upcomingTasksArea;
    }

    public int getSelectedYear() {
        return year;
    }

    public int getSelectedMonth() {
        return month;
    }

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
                    weekRow.add(null);
                } else if (day <= daysInMonth) {
                    weekRow.add(day++);
                } else {
                    weekRow.add(null);
                }
            }
            calendar.add(weekRow);
        }

        return calendar;
    }

    public void goToPreviousMonth() {
        month--;
        if (month < 1) {
            month = 12;
            year--;
        }
    }

    public void goToNextMonth() {
        month++;
        if (month > 12) {
            month = 1;
            year++;
        }
    }


    public JPanel getCalendarPanel() {
        calendarPanel.setLayout(new GridLayout(7, 7));
        calendarPanel.setBackground(new Color(34, 34, 34)); // dark background

        dayButtons.clear();
        dayLabels.clear();

        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setForeground(Color.LIGHT_GRAY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            calendarPanel.add(label);
            dayLabels.add(label);
        }

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

        // Add resize listener to adjust fonts dynamically
        calendarPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeFonts();
            }
        });

        return calendarPanel;
    }

    private void resizeFonts() {
        // Use panel height to estimate a good font size
        int height = calendarPanel.getHeight();
        if (height == 0) return; // Not yet visible

        // Header labels: bigger font
        int headerFontSize = Math.max(12, height / 25);
        Font headerFont = new Font("Segoe UI", Font.BOLD, headerFontSize);
        for (JLabel label : dayLabels) {
            label.setFont(headerFont);
        }

        // Day buttons: slightly smaller font
        int buttonFontSize = Math.max(10, height / 35);
        Font buttonFont = new Font("Segoe UI", Font.PLAIN, buttonFontSize);
        for (JButton button : dayButtons) {
            button.setFont(buttonFont);
        }
    }

    public void refreshCalendar() {
        calendarPanel.removeAll();
        calendarPanel.setLayout(new GridLayout(7, 7));
        calendarPanel.setBackground(new Color(34, 34, 34)); // dark background

        dayButtons.clear();
        dayLabels.clear();

        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            JLabel label = new JLabel(d, SwingConstants.CENTER);
            label.setForeground(Color.LIGHT_GRAY);
            label.setFont(new Font("Segoe UI", Font.BOLD, 16));
            calendarPanel.add(label);
            dayLabels.add(label);
        }

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

                String type = task[2];
                String info = task[3];

                sb.append(" • ").append(formattedDate)
                        .append(" - ").append(type)
                        .append(": ").append(info).append("\n");
            }
        }

        textArea.setText(sb.toString());
    }




    public void refreshAll() {
        refreshCalendar();
        if (upcomingTasksArea != null) {
            refreshSidePanel(upcomingTasksArea, 14); //
        }
    }



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

    private JButton getButton(Integer day) {
        Color defaultDayColor = new Color(50, 50, 50);
        Color futureTaskColor = new Color(70, 110, 80);    // Greenish
        Color pastTaskColor = new Color(110, 70, 70);      // Reddish
        Color todayBorderColor = Color.WHITE;

        LocalDate thisDay = LocalDate.of(year, month, day);
        boolean isToday = thisDay.equals(LocalDate.now());
        boolean hasTask = hasTask(thisDay);
        boolean isPast = thisDay.isBefore(LocalDate.now());

// Create button
        JButton button = new JButton(String.valueOf(day));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setFocusable(false);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(true);

// Background color logic
        if (hasTask) {
            if (isPast) {
                button.setBackground(pastTaskColor);
            } else {
                button.setBackground(futureTaskColor);
            }
        } else {
            button.setBackground(defaultDayColor);
        }

        Border defaultBorder;
        if (isToday) {
            defaultBorder = BorderFactory.createLineBorder(todayBorderColor, 3);
        } else {
            defaultBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
        }

        button.setBorder(defaultBorder);

        // Hover effects
        Color originalColor = button.getBackground();
        Color hoverColor = originalColor.brighter();
        Border hoverBorder = BorderFactory.createLineBorder(Color.GRAY, 2);
        Border finalDefaultBorder = defaultBorder;

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

        LocalDate selectedDate = LocalDate.of(year, month, day);
        button.addActionListener(e -> openTaskCreation(selectedDate));

        return button;
    }


    private String formatDate(LocalDate date) {
        int day = date.getDayOfMonth();
        String suffix;

        if (day >= 11 && day <= 13) {
            suffix = "th";
        } else {
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

    private void openTaskCreation(LocalDate date) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Task Creation");
        dialog.setModal(true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(null);

        // Use BorderLayout with padding around main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(34, 34, 34)); // dark background

        // Header panel for title
        JLabel titleLabel = new JLabel("Task Creation - " + formatDate(date));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center panel with all inputs stacked vertically
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(34, 34, 34));


        // Existing tasks
        ArrayList<String[]> loggedTasks = getTasksOnDate(date);
        if (!loggedTasks.isEmpty()) {
            JLabel existingLabel = new JLabel("Existing Tasks (tick to complete):");
            existingLabel.setForeground(Color.LIGHT_GRAY);
            existingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            existingLabel.setFocusable(false);
            inputPanel.add(existingLabel);
            inputPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            JPanel tasksCheckboxPanel = new JPanel();
            tasksCheckboxPanel.setLayout(new BoxLayout(tasksCheckboxPanel, BoxLayout.Y_AXIS));
            tasksCheckboxPanel.setBackground(new Color(34, 34, 34));
            tasksCheckboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (int i = 0; i < loggedTasks.size(); i++) {
                String[] task = loggedTasks.get(i);
                System.out.println(task[2]);
                String taskType = task[2];
                String taskInfo = task[3];
                JCheckBox taskCheckBox = new JCheckBox(taskType + ": " + taskInfo);
                taskCheckBox.setForeground(Color.WHITE);
                taskCheckBox.setBackground(new Color(34, 34, 34));
                taskCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                taskCheckBox.setFocusable(false);
                tasksCheckboxPanel.add(taskCheckBox);

                int recordIndex = findTaskIndex(userID, date, taskType, taskInfo);
                taskCheckBox.addActionListener(e -> {
                    if (taskCheckBox.isSelected() && recordIndex != -1) {
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
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Task type label + dropdown
        JLabel taskTypeLabel = new JLabel("Task Type:");
        taskTypeLabel.setForeground(Color.LIGHT_GRAY);
        taskTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskTypeLabel);

        String[] taskTypes = { "Work & Productivity", "Health & Wellbeing", "Social & Leisure" };
        JComboBox<String> taskTypeCombo = new JComboBox<>(taskTypes);
        taskTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskTypeCombo);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Task info label + textarea
        JLabel taskInfoLabel = new JLabel("Task Information:");
        taskInfoLabel.setForeground(Color.LIGHT_GRAY);
        taskInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(taskInfoLabel);

        JTextArea taskInfoArea = new JTextArea(4, 20);
        taskInfoArea.setLineWrap(true);
        taskInfoArea.setWrapStyleWord(true);
        taskInfoArea.setBackground(new Color(50, 50, 50));
        taskInfoArea.setForeground(Color.WHITE);
        taskInfoArea.setCaretColor(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(taskInfoArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        inputPanel.add(scrollPane);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Save button
        JButton saveButton = new JButton("Save Task");
        saveButton.setBackground(new Color(60, 120, 60));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setPreferredSize(new Dimension(100, 30));

        saveButton.addActionListener(e -> {
            String taskType = (String) taskTypeCombo.getSelectedItem();
            String taskInfo = taskInfoArea.getText().trim();

            if (taskInfo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter task information.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            addTask(userID,  date,  taskType, taskInfo);
            dialog.dispose();
            refreshAll();
        });

        inputPanel.add(saveButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    public void addTask(int userId, LocalDate date, String taskType, String taskInfo) {

        String taskRecord = userId + " ; " + date + " ; " + taskType + " ; " + taskInfo;
        taskFile.addRecord(taskRecord);
        // Add to your records list and then sort it
        taskFile.sortRecords();
    }



    public ArrayList<String[]> getUpcomingTasks(int daysAhead) {
        ArrayList<String[]> upcomingTasks = new ArrayList<>();
        LocalDate startDate = LocalDate.now();  // today
        LocalDate endDate = startDate.plusDays(daysAhead);  // today + daysAhead

        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] parts = record.split(" ; ");
                if (parts.length >= 4 && parts[0].equals(String.valueOf(userID))) {
                    LocalDate taskDate;
                    try {
                        taskDate = LocalDate.parse(parts[1]);
                    } catch (Exception e) {
                        continue; // skip malformed dates
                    }
                    // If taskDate is within startDate and endDate inclusive
                    if (!taskDate.isBefore(startDate) && !taskDate.isAfter(endDate)) {
                        upcomingTasks.add(parts);
                    }
                }
            }
        }
        return upcomingTasks;
    }





    private int findTaskIndex(int userId, LocalDate date, String taskType, String taskInfo) {
        String target = userId + " ; " + date + " ; " + taskType + " ; " + taskInfo;
        return taskFile.searchRecord(target);
    }





}
