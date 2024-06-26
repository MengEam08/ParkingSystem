package controller;

import main.ParkingSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class loginForm extends JDialog {
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JTextField tfEmail;
    private JButton btnLogIn;
    private JPanel loginPanel;
    private JPasswordField pfPassword;
    private JPasswordField pfConform;

    private static final String FIRST_NAME = "group5";
    private static final String LAST_NAME = "sme";
    private static final String EMAIL = "group5@gmail.com";
    private static final String PASSWORD = "123456";
    private static final int MAX_ATTEMPTS = 3;
    private int attemptCount = 0;

    public loginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(600, 500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = tfFirstName.getText();
                String lastName = tfLastName.getText();
                String email = tfEmail.getText();
                String password = new String(pfPassword.getPassword());

                if (firstName.equals(FIRST_NAME) && lastName.equals(LAST_NAME) && email.equals(EMAIL) && password.equals(PASSWORD)) {
                    // Dispose the login form
                    dispose();

                    // Call the ParkingSystem
                    ParkingSystem parkingSystem = new ParkingSystem();
                    parkingSystem.start(); // Assuming there is a start method to initiate the ParkingSystem
                } else {
                    attemptCount++;
                    if (attemptCount >= MAX_ATTEMPTS) {
                        JOptionPane.showMessageDialog(loginForm.this, "Maximum login attempts reached. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(loginForm.this, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        loginForm frame = new loginForm(null);
    }
}
