import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame {
    private User user;
    private List<Account> accounts;
    private Account selectedAccount;
    private JLabel balanceLabel;
    private JTextField amountField;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton transferButton;
    private JButton logoutButton;
    private JButton manageAccountButton;
    private JButton manageCardsButton;
    private JButton manageLoansButton;
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    private JComboBox<String> accountSelector;
    
    public Dashboard(User user) {
        this.user = user;
        setTitle("Bank Management System - Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load user accounts
        accounts = DatabaseManager.getUserAccounts(user.getId());
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found for this user", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            logout();
            return;
        }
        
        // Set the first account as selected by default
        selectedAccount = accounts.get(0);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));
        
        // Top panel with user info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Logout button in top panel
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 20, 60)); // Crimson
        logoutButton.setForeground(Color.BLACK);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with account info and transactions
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        centerPanel.setBackground(new Color(240, 248, 255));
        
        // Left panel - Account operations
        JPanel accountPanel = new JPanel(new BorderLayout(0, 10));
        accountPanel.setBackground(new Color(240, 248, 255));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Account Operations"));
        
        // Account selector
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(new Color(240, 248, 255));
        selectorPanel.add(new JLabel("Select Account: "));
        
        accountSelector = new JComboBox<>();
        for (Account account : accounts) {
            accountSelector.addItem(account.getAccountNumber() + " (" + account.getAccountType() + ")");
        }
        accountSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = accountSelector.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                    selectedAccount = accounts.get(selectedIndex);
                    updateAccountInfo();
                    loadTransactions();
                }
            }
        });
        selectorPanel.add(accountSelector);
        
        // Account balance display
        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balancePanel.setBackground(new Color(240, 248, 255));
        
        balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balancePanel.add(balanceLabel);
        
        // Button panel for actions
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setOpaque(false);
        
        manageAccountButton = new JButton("Manage Account");
        manageAccountButton.addActionListener(e -> openAccountPage());
        
        manageCardsButton = new JButton("Manage Cards");
        manageCardsButton.addActionListener(e -> openCardsPage());
        
        manageLoansButton = new JButton("Manage Loans");
        manageLoansButton.addActionListener(e -> openLoansPage());
        
        buttonPanel.add(manageAccountButton);
        buttonPanel.add(manageCardsButton);
        buttonPanel.add(manageLoansButton);
        
        // Create a container for balance and buttons in vertical arrangement
        JPanel accountControlPanel = new JPanel(new BorderLayout(0, 10));
        accountControlPanel.setBackground(new Color(240, 248, 255));
        accountControlPanel.add(balancePanel, BorderLayout.NORTH);
        accountControlPanel.add(buttonPanel, BorderLayout.CENTER);
        
        accountPanel.add(selectorPanel, BorderLayout.NORTH);
        
        // Transaction panel with amount field and buttons
        JPanel transactionPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        transactionPanel.setBackground(new Color(240, 248, 255));
        
        // Amount field
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.setBackground(new Color(240, 248, 255));
        amountPanel.add(new JLabel("Amount: $"));
        amountField = new JTextField(10);
        amountPanel.add(amountField);
        transactionPanel.add(amountPanel);
        
        // Operation buttons
        depositButton = new JButton("Deposit");
        depositButton.setBackground(new Color(46, 139, 87)); // Sea Green
        depositButton.setForeground(Color.BLACK);
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });
        transactionPanel.add(depositButton);
        
        withdrawButton = new JButton("Withdraw");
        withdrawButton.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        withdrawButton.setForeground(Color.BLACK);
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });
        transactionPanel.add(withdrawButton);
        
        transferButton = new JButton("Transfer");
        transferButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        transferButton.setForeground(Color.BLACK);
        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transfer();
            }
        });
        transactionPanel.add(transferButton);
        
        // Create main account operations panel with everything combined
        JPanel mainAccountOperationsPanel = new JPanel(new BorderLayout(0, 15));
        mainAccountOperationsPanel.setBackground(new Color(240, 248, 255));
        mainAccountOperationsPanel.add(accountControlPanel, BorderLayout.NORTH);
        mainAccountOperationsPanel.add(transactionPanel, BorderLayout.CENTER);
        
        accountPanel.add(mainAccountOperationsPanel, BorderLayout.CENTER);
        
        // Right panel - Transaction history
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(new Color(240, 248, 255));
        historyPanel.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        
        // Transaction table
        String[] columnNames = {"Date", "Type", "Amount", "Description"};
        transactionTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        transactionTable = new JTable(transactionTableModel);
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Load transaction history
        loadTransactions();
        
        // Add panels to center panel
        centerPanel.add(accountPanel);
        centerPanel.add(historyPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void openAccountPage() {
        AccountPage accountPage = new AccountPage(user, selectedAccount);
        accountPage.setVisible(true);
        accountPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshAccountData(); // Refresh data when account page is closed
            }
        });
    }
    
    private void updateAccountInfo() {
        balanceLabel.setText("Current Balance: $" + String.format("%.2f", selectedAccount.getBalance()));
    }
    
    private void loadTransactions() {
        // Clear existing data
        transactionTableModel.setRowCount(0);
        
        // Load transactions from database
        List<Transaction> transactions = DatabaseManager.getTransactionHistory(selectedAccount.getAccountId());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Transaction transaction : transactions) {
            String formattedDate = dateFormat.format(transaction.getDate());
            String type = transaction.getType();
            String amount = String.format("$%.2f", transaction.getAmount());
            String description = transaction.getDescription();
            
            transactionTableModel.addRow(new Object[]{formattedDate, type, amount, description});
        }
        
        // Update the UI
        transactionTable.repaint();
    }
    
    private void refreshAccountData() {
        // Reload account data
        accounts = DatabaseManager.getUserAccounts(user.getId());
        
        // Find the currently selected account with updated balance
        for (Account account : accounts) {
            if (account.getAccountId() == selectedAccount.getAccountId()) {
                selectedAccount = account;
                break;
            }
        }
        
        // Update display
        updateAccountInfo();
        loadTransactions();
    }
    
    private void deposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DatabaseManager.deposit(selectedAccount.getAccountId(), amount)) {
                JOptionPane.showMessageDialog(this, "Successfully deposited $" + String.format("%.2f", amount), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                amountField.setText("");
                
                // Refresh data
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to deposit amount", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void withdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (DatabaseManager.withdraw(selectedAccount.getAccountId(), amount)) {
                JOptionPane.showMessageDialog(this, "Successfully withdrew $" + String.format("%.2f", amount), 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                amountField.setText("");
                
                // Refresh data
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to withdraw amount. Insufficient funds?", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void transfer() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Prompt for recipient account number
            String recipientAccountNumber = JOptionPane.showInputDialog(this, "Enter recipient's account number:");
            
            if (recipientAccountNumber == null || recipientAccountNumber.trim().isEmpty()) {
                return;
            }
            
            // Find recipient account
            int recipientAccountId = -1;
            
            // In a real application, you would query the database directly
            // For simplicity, we'll assume we have a method to get account by number
            Account recipientAccount = null;
            
            // This is a placeholder - in a real application, use a database query
            for (Account otherAccount : accounts) {
                if (otherAccount.getAccountNumber().equals(recipientAccountNumber)) {
                    recipientAccount = otherAccount;
                    recipientAccountId = otherAccount.getAccountId();
                    break;
                }
            }
            
            if (recipientAccount == null) {
                // If not found in user's accounts, search all accounts
                // This would be better handled with a direct database query
                recipientAccountId = findAccountIdByNumber(recipientAccountNumber);
            }
            
            if (recipientAccountId == -1) {
                JOptionPane.showMessageDialog(this, "Recipient account not found", 
                        "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Can't transfer to the same account
            if (recipientAccountId == selectedAccount.getAccountId()) {
                JOptionPane.showMessageDialog(this, "Cannot transfer to the same account", 
                        "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Process transfer
            if (DatabaseManager.transfer(selectedAccount.getAccountId(), recipientAccountId, amount)) {
                JOptionPane.showMessageDialog(this, "Successfully transferred $" + String.format("%.2f", amount) + 
                        " to account " + recipientAccountNumber, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                
                amountField.setText("");
                
                // Refresh data
                refreshAccountData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to complete transfer. Insufficient funds?", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", 
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int findAccountIdByNumber(String accountNumber) {
        // Use the new DatabaseManager method to find an account by number
        Account account = DatabaseManager.getAccountByNumber(accountNumber);
        if (account != null) {
            return account.getAccountId();
        }
        return -1;
    }
    
    private void logout() {
        LoginPage loginPage = new LoginPage();
        loginPage.setVisible(true);
        this.dispose();
    }
    
    private void openCardsPage() {
        Cards cardsPage = new Cards(user);
        cardsPage.setVisible(true);
        this.dispose();
    }
    
    private void openLoansPage() {
        Loan loanPage = new Loan(user);
        loanPage.setVisible(true);
        this.dispose();
    }
} 