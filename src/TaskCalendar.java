import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

public class TaskCalendar {
    int userID;
    int day;
    int month;
    int year;
    Database taskFile;

    LocalDate today = LocalDate.now();
    int currentDay = today.getDayOfMonth();
    int currentMonth = today.getMonthValue();
    int currentYear = today.getYear();

    // COLOURS
    String RESET = "\u001B[0m";
    String DARK_RED = "\033[0;31m";
    String GREEN = "\u001B[32m";
    String YELLOW = "\u001B[33m";
    String CYAN = "\033[0;36m";
    String LIGHT_GRAY = "\033[0;37m"; // Light Gray (Bright White)
    String BOLD = "\033[1m";




    public TaskCalendar(int userID, int day, int month, int year, String filename) {
        this.userID = userID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.taskFile = new Database(filename);
    }

    public ArrayList<ArrayList<Integer>> createCalendar() {
        ArrayList<ArrayList<Integer>> calendar = new ArrayList<>();

        LocalDate firstDay = LocalDate.of(year, month, 1);
        int daysInMonth = firstDay.lengthOfMonth();
        int startDay = firstDay.getDayOfWeek().getValue(); // 1 (Mon) to 7 (Sun)

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

    public JPanel getCalendarPanel() {
        ArrayList<ArrayList<Integer>> calendar = createCalendar();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 7)); // 7 columns for Mon–Sun
        panel.setBackground(Color.white);

        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            panel.add(new JLabel(d, SwingConstants.CENTER));
        }

        for (ArrayList<Integer> week : calendar) {
            for (Integer day : week) {
                if (day == null) {
                    panel.add(new JLabel(""));
                } else {
                    JButton button = getButton(day);
                    panel.add(button);
                }
            }
        }

        return panel;
    }

    private boolean hasTask(LocalDate taskDate) {
        ArrayList<String> records = taskFile.readAllRecords();
        if (records != null) {
            for (String record : records) {
                String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                if (taskDetails[0].equals(String.valueOf(userID)) && taskDetails[1].equals(taskDate.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private JButton getButton(Integer day) {
        // Define your default day button color
        Color defaultDayColor = new Color(224, 230, 255); // Light grey default
        Color currentDayColor = new Color(185, 207, 248); // Soft blue for today
        Color taskDayColor = new Color(180, 240, 200);    // Soft green for task days

         // Create button for the day
        JButton button = new JButton(day.toString());

        button.setHorizontalAlignment(SwingConstants.LEFT);   // Top-left alignment
        button.setVerticalAlignment(SwingConstants.TOP);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(defaultDayColor); // Default color
        button.setFocusable(false);

       // Example condition: current day
        if (day == currentDay && currentMonth == month && currentYear == year) {
            button.setBackground(currentDayColor);

        } else if (hasTask(LocalDate.of(currentYear, currentMonth, day))) {
            button.setBackground(taskDayColor);


        }

        // Store original background
        Color originalColor = button.getBackground();
        Color hoverColor = originalColor.darker(); // Darker tint for hover
        Border hoverBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
        Border defaultBorder = BorderFactory.createEmptyBorder();

        // Add hover effect (color + border)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(hoverBorder);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
                button.setBorder(defaultBorder);
            }
        });


        LocalDate selectedDate = LocalDate.of(year, month, day);
        button.addActionListener(e -> {
            System.out.println("Clicked day: " + day);
            openTaskCreation(selectedDate);
        });
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

        String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = date.getYear();
        return day + suffix + " " + month + " " + year;
    }


    private void openTaskCreation(LocalDate date) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Create Task for " + formatDate(date));
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        // Panel for inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dateLabel = new JLabel("Date: " + formatDate(date));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(dateLabel);

        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel typeLabel = new JLabel("Task Type:");
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(typeLabel);

        String[] taskTypes = { "Academic", "Social & Personal", "Health & Wellbeing" };
        JComboBox<String> typeComboBox = new JComboBox<>(taskTypes);
        typeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(typeComboBox);

        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel infoLabel = new JLabel("Task Info:");
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(infoLabel);

        JTextArea infoTextArea = new JTextArea(5, 30);
        JScrollPane scrollPane = new JScrollPane(infoTextArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(scrollPane);

        dialog.add(inputPanel, BorderLayout.CENTER);

        // SAVE TASK BUTTON
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Task");
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            String taskType = (String) typeComboBox.getSelectedItem();
            String taskInfo = infoTextArea.getText().trim();

            if (taskInfo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Task info cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveTask(userID, date, taskType, taskInfo);
            JOptionPane.showMessageDialog(dialog, "Task saved successfully!");
            dialog.dispose();
        });

        dialog.setVisible(true);
    }



    public void viewTask() {
        ArrayList<String> records = taskFile.readAllRecords();
        ArrayList<String> tasks = new ArrayList<>();
        if (records != null) {
            for (String record : records) {
                String[] taskDetails = record.split(" ; "); // userID ; date ; type ; info
                if (taskDetails[0].equals(String.valueOf(userID))) {
                    tasks.add(record);
                    System.out.print(GREEN + "Task date: " + RESET + taskDetails[1] + " ");
                    System.out.print(GREEN + "Task type: " + RESET + taskDetails[2] + " ");
                    System.out.println(GREEN + "Task info: " + RESET + taskDetails[3] + " ");

                }

            }
            if (tasks.isEmpty() ) {
                System.out.println(DARK_RED + "No tasks found." + RESET);
            }
        }
    }





    // Save the task to the file
    public void saveTask(int userID, LocalDate taskDate, String type, String info) {
        String taskData = userID + " ; " + taskDate.toString() + " ; " + type + " ; " + info; // format it
        taskFile.addRecord(taskData);  // store in the text file
        System.out.println(GREEN + "Task saved successfully!" + RESET);
    }




    // Navigate to the previous month
    public void prevMonth() {
        if (month == 1) {
            month = 12;
            year--;
        } else {
            month--;
        }
    }

    // Navigate to the next month
    public void nextMonth() {
        if (month == 12) {
            month = 1;
            year++;
        } else {
            month++;
        }
    }

}