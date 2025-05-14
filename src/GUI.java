import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame {
    private JLabel labelOutput;
    private JTextField textUsername;
    private JButton buttonOK;


    // private final int WIDGET_HEIGHT = 30; use constants for easier rearranging


    public GUI() {
        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // quit the app when we close the window
        setSize(600, 400);
        setLayout(null);
        labelOutput = new JLabel("Study Planner");
        labelOutput.setBounds(250,50, 150, 150);


        textUsername = new JTextField();
        textUsername.setBounds(50, 90, 200, 30);


        buttonOK = new JButton("OK");
        buttonOK.setBounds(50, 130, 100, 30);


        add(labelOutput);
        add(textUsername);
        add(buttonOK);
        setVisible(true);
        System.out.println("SEQUENCE: GUI_test created");
    }
}
