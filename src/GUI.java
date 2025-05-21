import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JTextField textUsername;
    private JPasswordField textPassword;
    private JButton buttonOK;

    private final int WIDGET_HEIGHT = 30;
    private Login login;  // Reference to your login system

    public GUI() {
        login = new Login("users.txt");  // replace with your actual filename

        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 400);
        setLayout(null);
        setLocationRelativeTo(null);  // Center on screen

        // TITLE
        JLabel title = new JLabel("Study Planner");
        title.setBounds(250, 30, 300, WIDGET_HEIGHT);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title);

        // USERNAME LABEL AND TEXT FIELD
        JLabel labelUsername = new JLabel("Username:");
        labelUsername.setBounds(170, 90, 80, WIDGET_HEIGHT);
        add(labelUsername);

        textUsername = new JTextField();
        textUsername.setBounds(260, 90, 200, WIDGET_HEIGHT);
        add(textUsername);

        // PASSWORD LABEL AND TEXT FIELD
        JLabel labelPassword = new JLabel("Password:");
        labelPassword.setBounds(170, 140, 80, WIDGET_HEIGHT);
        add(labelPassword);

        textPassword = new JPasswordField();
        textPassword.setBounds(260, 140, 200, WIDGET_HEIGHT);
        textPassword.setEchoChar('*');  // turns letters into asterisk
        add(textPassword);

        // SHOW PASSWORD CHECKBOX
        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(260, 175, 150, 20);
        add(showPassword);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                textPassword.setEchoChar((char) 0);  // Show password
            } else {
                textPassword.setEchoChar('*');  // Hide password
            }
        });

        // Log in button
        buttonOK = new JButton("Log in");
        buttonOK.setBounds(300, 210, 100, WIDGET_HEIGHT);
        add(buttonOK);
        showPassword.setFocusable(false);


        buttonOK.addActionListener(e -> {
            // stores username and password
            String username = textUsername.getText().trim();
            String password = new String(textPassword.getPassword()).trim();

            // if user foundn in database then login
            if (login.findUser(username, password)) {
                int userID = login.getUserID();
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome, " +  username,
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                openMainMenu(userID);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void openMainMenu(int userID) {
        dispose();
        System.out.println("Logged in " + userID);

        new MainMenu(userID);
    }

    // Entry point for testing
    public static void main(String[] args) {
        new GUI();
    }
}
