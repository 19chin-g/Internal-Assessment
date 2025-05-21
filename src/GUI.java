import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame {
    private JTextField textUsername;
    private JTextField textPassword;
    private JButton buttonOK;

    private final int WIDGET_HEIGHT = 30; //use constants for easier rearranging


    public GUI() {
        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // quit the app when we close the window
        setSize(700, 400);
        setLayout(null);
        JLabel title  = new JLabel("Study Planner");
        title.setBounds(250,50, 200, WIDGET_HEIGHT);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        textUsername = new JTextField();
        textUsername.setBounds(250, 90, 200, WIDGET_HEIGHT);
        textPassword = new JTextField();
        textPassword.setBounds(250, 130, 200, WIDGET_HEIGHT);

        buttonOK = new JButton("Log in");
        buttonOK.setBounds(300, 170, 100, WIDGET_HEIGHT);



        add(title);
        add(textUsername);
        add(textPassword);

        add(buttonOK);
        setVisible(true);
        System.out.println("SEQUENCE: GUI_test created");

        buttonOK.addActionListener(e -> {
            String username = textUsername.getText();
            String password = textPassword.getText();

            System.out.println(username);
            System.out.println(password);
        });


    }
}
