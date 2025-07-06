import java.time.*;
import java.util.*;
import javax.swing.*;
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
    Color SoftBlue = new Color(194, 214, 246);


    public TaskCalendar(int year, int month) {
        this.userID = userID;
        this.day = day;
        this.month = month;
        this.year = year;
        //this.taskFile = new Database(filename);
    }

    public JPanel getCalendarPanel() {
        ArrayList<ArrayList<Integer>> calendar = createCalendar();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 7)); // 7 columns for Mon–Sun

        String[] days = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
        for (String d : days) {
            panel.add(new JLabel(d, SwingConstants.CENTER));
        }

        for (ArrayList<Integer> week : calendar) {
            for (Integer day : week) {
                if (day == null) {
                    panel.add(new JLabel(""));
                } else {
                    JButton button = new JButton(day.toString());
                    if (day == currentDay && currentMonth == month && currentYear == year) {
                        button.setBackground(SoftBlue);
                    }
                    LocalDate selectedDate = LocalDate.of(year, month, day);
                    button.addActionListener(e -> {
                        System.out.println("Clicked day: " + day);
                        // Optional: open task view or add popup
                        openTaskCreationDialog(selectedDate);
                    });
                    panel.add(button);
                }
            }
        }

        return panel;
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

    public void openTaskCreationDialog(LocalDate date) {
        String taskInfo = JOptionPane.showInputDialog(null,
                "Add a task for " + date.toString() + ":",
                "Create Task", JOptionPane.PLAIN_MESSAGE);
        if (taskInfo != null && !taskInfo.trim().isEmpty()) {
            System.out.println("Task for " + date + ": " + taskInfo);
            // TODO: Save the task using your saveTask method or logic here
        } else {
            System.out.println("No task entered.");
        }
    }

    private void openTaskCreation(LocalDate date) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Create Task for " + date.toString());
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());

        // Panel for inputs
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel dateLabel = new JLabel("Date: " + date.toString());
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

        // Save button panel
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



    public LocalDate stringToLocalDate(String date) {
        try {
            String[] dateParts = date.split("/");

            int inputDay = Integer.parseInt(dateParts[0]);
            int inputMonth = Integer.parseInt(dateParts[1]);
            int inputYear = Integer.parseInt(dateParts[2]);

            // Optional: reject if parts are too long (e.g. "123/45/67")
            if (dateParts[0].length() > 2 || dateParts[1].length() > 2 || dateParts[2].length() > 2) {
                System.out.println(DARK_RED + "1Invalid format! Use DD/MM/YY format. " + RESET);
                return null;
            }

            if (inputMonth < 1 || inputMonth > 12) {
                System.out.println(DARK_RED + "Invalid month! Use DD/MM/YY format. " + RESET);
                return null;
            }

            if (inputYear < 0 || inputYear > 99) {
                System.out.println(DARK_RED + "Invalid year! Use DD/MM/YY format. " + RESET);
                return null;
            }

            inputYear += 2000;
            int daysInMonth = YearMonth.of(inputYear, inputMonth).lengthOfMonth();

            if (inputDay < 1 || inputDay > daysInMonth) {
                System.out.println(DARK_RED + "Invalid day for given month/year. " + RESET);
                return null;
            }

            return LocalDate.of(inputYear, inputMonth, inputDay);

        } catch (Exception e) {
            System.out.println(DARK_RED + "Invalid input! Use DD/MM/YY format with numbers. " + RESET);
            return null;
        }
    }



    // Add a task to a specific date
    public void addTask() {
        Scanner scanner = new Scanner(System.in);
        LocalDate taskDate = null;
        String taskType = "";
        String taskInfo = "";

        System.out.println(BOLD + YELLOW + "======== TASK CREATION ========" + RESET);
        while (taskDate == null) {
            System.out.print("Enter date (DD/MM/YY or D/M/YY) to add task: ");
            String input = scanner.nextLine().trim();
            taskDate = stringToLocalDate(input);
        }



        // prompt for task type and information
        System.out.println("1) Academic");
        System.out.println("2) Social & Personal");
        System.out.println("3) Health & Wellbeing");

        while (taskType.isEmpty()) {
            System.out.print("Enter task type: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> taskType = "Academic";
                case "2" -> taskType = "Social & Personal";
                case "3" -> taskType = "Health & Wellbeing";
                default -> System.out.println(DARK_RED + "Invalid choice!" + RESET);
            }
        }

        // Task description
        System.out.println("Enter task info: ");
        while (taskInfo.trim().isEmpty()) {
            taskInfo = scanner.nextLine().trim();
            if (taskInfo.isEmpty()) {
                System.out.print(DARK_RED + "Task info cannot be empty. Please enter some information: " + RESET);
            }
        }

        saveTask(userID, taskDate, taskType, taskInfo);
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