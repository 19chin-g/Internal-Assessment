import javax.swing.*;
import java.awt.*;


public class GUI extends JFrame {
    private JLabel labelOutput;
    private JTextField textUsername;
    private JButton buttonOK;

    private final int WIDGET_HEIGHT = 30; //use constants for easier rearranging


    public GUI() {
        setTitle("Study Planner IA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // quit the app when we close the window
        setSize(600, 400);
        setLayout(null);
        labelOutput = new JLabel("Date");
        labelOutput.setBounds(250,50, 100, WIDGET_HEIGHT);


        textUsername = new JTextField();
        textUsername.setBounds(250, 90, 200, WIDGET_HEIGHT);

        buttonOK = new JButton("");
        buttonOK.setBounds(150, 130, 100, WIDGET_HEIGHT);

        //buttonOK.addActionListener(buttonOK);




        add(labelOutput);
        add(textUsername);
        add(buttonOK);
        setVisible(true);
        System.out.println("SEQUENCE: GUI_test created");


    }
}
