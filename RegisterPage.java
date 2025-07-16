import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class RegisterPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;
    
    public RegisterPage() {
        setTitle("Bank Management System - Register");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create panel with a nice background color
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue color
        
        // Title label
        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(120, 20, 300, 30);
        panel.add(titleLabel);
        
        // Full Name field
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setBounds(50, 70, 100, 25);
        panel.add(fullNameLabel);
        
        fullNameField = new JTextField();
        fullNameField.setBounds(180, 70, 200, 25);
        panel.add(fullNameField);
        
        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 110, 100, 25);
        panel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setBounds(180, 110, 200, 25);
        panel.add(emailField);
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 150, 100, 25);
        panel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(180, 150, 200, 25);
        panel.add(usernameField);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 190, 100, 25);
        panel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(180, 190, 200, 25);
        panel.add(passwordField);
        
        // Confirm Password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 230, 120, 25);
        panel.add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(180, 230, 200, 25);
        panel.add(confirmPasswordField);
        
        // Register button
        registerButton = new JButton("Register");
        registerButton.setBounds(180, 280, 100, 30);
        registerButton.setBackground(Color.BLACK); // Sea Green
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        panel.add(registerButton);
        
        // Back button
        backButton = new JButton("Back to Login");
        backButton.setBounds(150, 320, 150, 30);
        backButton.setBackground(Color.BLACK); // Cornflower Blue
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                backToLogin();
            }
        });
        panel.add(backButton);
        
        // Add panel to frame
        add(panel);
    }
    
    private void register() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Basic validation
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Email validation (simple)
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username already exists using DatabaseManager
        if (DatabaseManager.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", 
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Register user in database
        if (DatabaseManager.registerUser(username, password, fullName, email)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Return to login page
            backToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void backToLogin() {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
        this.dispose();
    }
} 