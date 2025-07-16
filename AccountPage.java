import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.NumberFormatter;

public class AccountPage extends JFrame {
    private User user;
    private Account currentAccount;
    private JLabel accountNumberLabel;
    private JLabel accountTypeLabel;
    private JLabel balanceLabel;
    private JButton backButton;
    private JButton refreshButton;
    
    // Deposit panel components
    private JFormattedTextField depositAmountField;
    private JButton depositButton;
    
    // Withdraw panel components
    private JFormattedTextField withdrawAmountField;
    private JButton withdrawButton;
    
    // Transfer panel components
    private JFormattedTextField transferAmountField;
    private JTextField recipientAccountField;
    private JButton transferButton;
    
    public AccountPage(User user, Account account) {
        this.user = user;
        this.currentAccount = account;
        
        setTitle("Account Details - " + account.getAccountNumber());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with account info
        JPanel accountInfoPanel = createAccountInfoPanel();
        mainPanel.add(accountInfoPanel, BorderLayout.NORTH);
        
        // Center panel with operations
        JPanel operationsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        operationsPanel.setOpaque(false);
        
        // Create panels for operations
        JPanel depositPanel = createDepositPanel();
        JPanel withdrawPanel = createWithdrawPanel();
        JPanel transferPanel = createTransferPanel();
        
        operationsPanel.add(depositPanel);
        operationsPanel.add(withdrawPanel);
        operationsPanel.add(transferPanel);
        
        mainPanel.add(operationsPanel, BorderLayout.CENTER);
        
        // Bottom panel with navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(100, 149, 237));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.addActionListener(e -> refreshAccountData());
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.BLACK);
        backButton.addActionListener(e -> {
            Dashboard dashboard = new Dashboard(user);
            dashboard.setVisible(true);
            dispose();
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private JPanel createAccountInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Account Information",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        accountNumberLabel = new JLabel("Account Number: " + currentAccount.getAccountNumber());
        accountNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        accountTypeLabel = new JLabel("Account Type: " + currentAccount.getAccountType());
        accountTypeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        panel.add(accountNumberLabel);
        panel.add(accountTypeLabel);
        panel.add(balanceLabel);
        
        return panel;
    }
    
    private JPanel createDepositPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Deposit",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        
        // Create formatted text field for amount
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        
        depositAmountField = new JFormattedTextField(formatter);
        depositAmountField.setValue(0.0);
        depositAmountField.setColumns(10);
        
        JLabel amountLabel = new JLabel("Amount to Deposit ($):");
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setOpaque(false);
        inputPanel.add(amountLabel);
        inputPanel.add(depositAmountField);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        // Deposit button
        depositButton = new JButton("Deposit Funds");
        depositButton.setBackground(new Color(46, 139, 87));
        depositButton.setForeground(Color.BLACK);
        depositButton.setFont(new Font("Arial", Font.BOLD, 14));
        depositButton.addActionListener(e -> performDeposit());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(depositButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add an information text
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>Deposit funds to your account.<br>Funds will be available immediately.</div></html>");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createWithdrawPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Withdraw",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        
        // Create formatted text field for amount
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        
        withdrawAmountField = new JFormattedTextField(formatter);
        withdrawAmountField.setValue(0.0);
        withdrawAmountField.setColumns(10);
        
        JLabel amountLabel = new JLabel("Amount to Withdraw ($):");
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setOpaque(false);
        inputPanel.add(amountLabel);
        inputPanel.add(withdrawAmountField);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        // Withdraw button
        withdrawButton = new JButton("Withdraw Funds");
        withdrawButton.setBackground(new Color(100, 149, 237));
        withdrawButton.setForeground(Color.BLACK);
        withdrawButton.setFont(new Font("Arial", Font.BOLD, 14));
        withdrawButton.addActionListener(e -> performWithdrawal());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(withdrawButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add information text
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>Withdraw funds from your account.<br>Subject to available balance.</div></html>");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Transfer",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14)),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        
        // Create center panel with inputs
        JPanel inputsPanel = new JPanel(new GridLayout(4, 1, 5, 10));
        inputsPanel.setOpaque(false);
        
        // Amount input
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.setOpaque(false);
        JLabel amountLabel = new JLabel("Amount ($):");
        
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);
        
        transferAmountField = new JFormattedTextField(formatter);
        transferAmountField.setValue(0.0);
        transferAmountField.setColumns(10);
        
        amountPanel.add(amountLabel);
        amountPanel.add(transferAmountField);
        
        // Recipient account input
        JPanel recipientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recipientPanel.setOpaque(false);
        JLabel recipientLabel = new JLabel("Recipient Account Number:");
        
        recipientAccountField = new JTextField(15);
        
        recipientPanel.add(recipientLabel);
        recipientPanel.add(recipientAccountField);
        
        // Add to inputs panel
        inputsPanel.add(new JLabel()); // Spacer
        inputsPanel.add(amountPanel);
        inputsPanel.add(recipientPanel);
        inputsPanel.add(new JLabel()); // Spacer
        
        panel.add(inputsPanel, BorderLayout.CENTER);
        
        // Transfer button
        transferButton = new JButton("Transfer Funds");
        transferButton.setBackground(new Color(70, 130, 180));
        transferButton.setForeground(Color.BLACK);
        transferButton.setFont(new Font("Arial", Font.BOLD, 14));
        transferButton.addActionListener(e -> performTransfer());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(transferButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add an information text
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>Transfer funds to another account.<br>Recipient must have a valid account.</div></html>");
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        infoLabel.setForeground(new Color(100, 100, 100));
        
        panel.add(infoLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private void refreshAccountData() {
        // Reload the account data from the database
        Account refreshedAccount = DatabaseManager.getAccountById(currentAccount.getAccountId());
        if (refreshedAccount != null) {
            currentAccount = refreshedAccount;
            balanceLabel.setText("Current Balance: $" + String.format("%.2f", currentAccount.getBalance()));
        } else {
            JOptionPane.showMessageDialog(this, "Failed to refresh account data", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performDeposit() {
        try {
            double amount = ((Number) depositAmountField.getValue()).doubleValue();
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DatabaseManager.deposit(currentAccount.getAccountId(), amount)) {
                JOptionPane.showMessageDialog(this, 
                        "Successfully deposited $" + String.format("%.2f", amount), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset the input field
                depositAmountField.setValue(0.0);
                
                // Refresh the account balance
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deposit funds", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performWithdrawal() {
        try {
            double amount = ((Number) withdrawAmountField.getValue()).doubleValue();
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds", 
                        "Withdrawal Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DatabaseManager.withdraw(currentAccount.getAccountId(), amount)) {
                JOptionPane.showMessageDialog(this, 
                        "Successfully withdrew $" + String.format("%.2f", amount), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset the input field
                withdrawAmountField.setValue(0.0);
                
                // Refresh the account balance
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to withdraw funds", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void performTransfer() {
        try {
            double amount = ((Number) transferAmountField.getValue()).doubleValue();
            String recipientAccountNumber = recipientAccountField.getText().trim();
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (recipientAccountNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a recipient account number", 
                        "Missing Information", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (amount > currentAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds for transfer", 
                        "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Find recipient account - in a real app, we would search the database
            int recipientAccountId = findAccountIdByNumber(recipientAccountNumber);
            
            if (recipientAccountId == -1) {
                JOptionPane.showMessageDialog(this, "Recipient account not found", 
                        "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Can't transfer to the same account
            if (recipientAccountId == currentAccount.getAccountId()) {
                JOptionPane.showMessageDialog(this, "Cannot transfer to the same account", 
                        "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DatabaseManager.transfer(currentAccount.getAccountId(), recipientAccountId, amount)) {
                JOptionPane.showMessageDialog(this, 
                        "Successfully transferred $" + String.format("%.2f", amount) + 
                        " to account " + recipientAccountNumber, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Reset the input fields
                transferAmountField.setValue(0.0);
                recipientAccountField.setText("");
                
                // Refresh the account balance
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to complete transfer", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid information for the transfer", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int findAccountIdByNumber(String accountNumber) {
        // Use the DatabaseManager method to find an account by number
        Account account = DatabaseManager.getAccountByNumber(accountNumber);
        if (account != null) {
            return account.getAccountId();
        }
        return -1;
    }
    
    public static void main(String[] args) {
        // This is for testing purposes only
        // In a real application, this would be opened from the Dashboard
        
        // Dummy user and account for testing
        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        
        Account testAccount = new Account();
        testAccount.setAccountId(1);
        testAccount.setUserId(1);
        testAccount.setAccountNumber("1000001");
        testAccount.setAccountType("SAVINGS");
        testAccount.setBalance(5000.00);
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new AccountPage(testUser, testAccount).setVisible(true);
        });
    }
} 