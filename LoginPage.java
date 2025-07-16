import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    public LoginPage() {
        setTitle("Bank Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create panel with a nice background color
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue color
        
        // Title label
        JLabel titleLabel = new JLabel("Bank Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(80, 20, 300, 30);
        panel.add(titleLabel);
        
        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 70, 100, 25);
        panel.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(150, 70, 200, 25);
        panel.add(usernameField);
        
        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 110, 100, 25);
        panel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 200, 25);
        panel.add(passwordField);
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.setBounds(150, 160, 100, 30);
        loginButton.setBackground(Color.BLACK); // Cornflower Blue
        loginButton.setForeground(Color.BLACK);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton);
        
        // Register button
        registerButton = new JButton("Register");
        registerButton.setBounds(150, 200, 100, 30);
        registerButton.setBackground(Color.BLACK); // Sea Green
        registerButton.setForeground(Color.BLACK);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegisterPage();
            }
        });
        panel.add(registerButton);
        
        // Add panel to frame
        add(panel);
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Use database manager for authentication
        if (DatabaseManager.authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            openDashboard(username);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", 
                    "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openRegisterPage() {
        RegisterPage registerPage = new RegisterPage();
        registerPage.setVisible(true);
        this.dispose();
    }
    
    private void openDashboard(String username) {
        User user = DatabaseManager.getUserDetails(username);
        if (user != null) {
            Dashboard dashboard = new Dashboard(user);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error loading user details", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 