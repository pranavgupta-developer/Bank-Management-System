import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.swing.text.NumberFormatter;

public class Loan extends JFrame {
    private User user;
    private List<Account> accounts;
    private Account selectedAccount;
    private JComboBox<String> accountSelector;
    private JTable loanTable;
    private DefaultTableModel loanTableModel;
    private JButton applyLoanButton;
    private JButton makePaymentButton;
    private JButton backButton;
    private JLabel balanceLabel;
    
    public Loan(User user) {
        this.user = user;
        setTitle("Bank Management System - Loan Management");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load user accounts
        accounts = DatabaseManager.getUserAccounts(user.getId());
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found for this user", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        // Set the first account as selected by default
        selectedAccount = accounts.get(0);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 248, 255));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with account selector and balance
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Loan Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Account selector panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectorPanel.setOpaque(false);
        
        selectorPanel.add(new JLabel("Account: "));
        accountSelector = new JComboBox<>();
        for (Account account : accounts) {
            accountSelector.addItem(account.getAccountNumber() + " (" + account.getAccountType() + ")");
        }
        accountSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = accountSelector.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < accounts.size()) {
                    selectedAccount = accounts.get(selectedIndex);
                    updateBalanceLabel();
                    refreshLoanTable();
                }
            }
        });
        selectorPanel.add(accountSelector);
        
        // Balance display
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(Color.WHITE);
        updateBalanceLabel();
        selectorPanel.add(new JLabel("   Balance: "));
        selectorPanel.add(balanceLabel);
        
        topPanel.add(selectorPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with loan table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(240, 248, 255));
        
        // Create loan table
        loanTableModel = new DefaultTableModel(
                new Object[]{"Loan ID", "Type", "Principal", "Interest Rate", "Term", "Monthly Payment", "Remaining", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        loanTable = new JTable(loanTableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanTable.setRowHeight(25);
        loanTable.getTableHeader().setReorderingAllowed(false);
        loanTable.getTableHeader().setBackground(new Color(70, 130, 180));
        loanTable.getTableHeader().setForeground(Color.WHITE);
        
        // Custom cell renderer for currency and percentage formats
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        loanTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Principal
        loanTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Interest Rate
        loanTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Monthly Payment
        loanTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // Remaining
        
        // Status cell renderer with colors
        loanTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String status = value.toString();
                    if (status.equals("ACTIVE")) {
                        c.setForeground(new Color(0, 128, 0)); // Dark Green
                    } else if (status.equals("PENDING")) {
                        c.setForeground(new Color(255, 140, 0)); // Dark Orange
                    } else if (status.equals("PAID")) {
                        c.setForeground(new Color(0, 0, 128)); // Navy Blue
                    } else if (status.equals("DEFAULTED")) {
                        c.setForeground(new Color(178, 34, 34)); // Firebrick Red
                    } else if (status.equals("APPROVED")) {
                        c.setForeground(new Color(0, 100, 0)); // Dark Green
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        applyLoanButton = new JButton("Apply for New Loan");
        applyLoanButton.setBackground(new Color(46, 139, 87)); // Sea Green
        applyLoanButton.setForeground(Color.BLACK);
        applyLoanButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyLoanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyForLoan();
            }
        });
        
        makePaymentButton = new JButton("Make Payment");
        makePaymentButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        makePaymentButton.setForeground(Color.BLACK);
        makePaymentButton.setFont(new Font("Arial", Font.BOLD, 14));
        makePaymentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                makePayment();
            }
        });
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(100, 149, 237)); // Cornflower Blue
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dashboard dashboard = new Dashboard(user);
                dashboard.setVisible(true);
                dispose();
            }
        });
        
        buttonPanel.add(applyLoanButton);
        buttonPanel.add(makePaymentButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Load loan table initially
        refreshLoanTable();
    }
    
    private void updateBalanceLabel() {
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        balanceLabel.setText(df.format(selectedAccount.getBalance()));
    }
    
    private void refreshLoanTable() {
        // Clear existing rows
        loanTableModel.setRowCount(0);
        
        // Get loans for the selected account
        List<DatabaseManager.Loan> loans = DatabaseManager.getLoansByAccountId(selectedAccount.getAccountId());
        
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
        DecimalFormat percentFormat = new DecimalFormat("#0.00%");
        
        for (DatabaseManager.Loan loan : loans) {
            loanTableModel.addRow(new Object[]{
                loan.getLoanId(),
                loan.getLoanType(),
                currencyFormat.format(loan.getPrincipalAmount()),
                loan.getInterestRate() + "%",
                loan.getTermMonths() + " months",
                currencyFormat.format(loan.getMonthlyPayment()),
                currencyFormat.format(loan.getRemainingAmount()),
                loan.getStatus()
            });
        }
        
        if (loans.isEmpty()) {
            makePaymentButton.setEnabled(false);
        } else {
            makePaymentButton.setEnabled(true);
        }
    }
    
    private void applyForLoan() {
        // Create loan application dialog
        JDialog dialog = new JDialog(this, "Apply for a New Loan", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Loan type selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Loan Type:"), gbc);
        
        String[] loanTypes = {"PERSONAL", "HOME", "AUTO", "EDUCATION", "BUSINESS"};
        JComboBox<String> loanTypeCombo = new JComboBox<>(loanTypes);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(loanTypeCombo, gbc);
        
        // Principal amount
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Principal Amount:"), gbc);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setMinimum(1000.0);
        currencyFormatter.setMaximum(1000000.0);
        currencyFormatter.setAllowsInvalid(false);
        
        JFormattedTextField principalField = new JFormattedTextField(currencyFormatter);
        principalField.setValue(10000.0);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(principalField, gbc);
        
        // Interest rate
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Interest Rate (%):"), gbc);
        
        NumberFormat percentFormat = NumberFormat.getNumberInstance();
        NumberFormatter percentFormatter = new NumberFormatter(percentFormat);
        percentFormatter.setMinimum(1.0);
        percentFormatter.setMaximum(20.0);
        percentFormatter.setAllowsInvalid(false);
        
        JFormattedTextField interestField = new JFormattedTextField(percentFormatter);
        interestField.setValue(5.75);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(interestField, gbc);
        
        // Loan term
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Term (months):"), gbc);
        
        String[] termOptions = {"12", "24", "36", "48", "60", "120", "180", "240", "360"};
        JComboBox<String> termCombo = new JComboBox<>(termOptions);
        termCombo.setSelectedItem("36");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(termCombo, gbc);
        
        // Monthly payment preview
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Monthly Payment:"), gbc);
        
        JLabel monthlyPaymentLabel = new JLabel("$0.00");
        monthlyPaymentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(monthlyPaymentLabel, gbc);
        
        // Calculate button to preview payment
        JButton calculateButton = new JButton("Calculate Payment");
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double principal = ((Number)principalField.getValue()).doubleValue();
                    double interestRate = ((Number)interestField.getValue()).doubleValue();
                    int term = Integer.parseInt((String)termCombo.getSelectedItem());
                    
                    // Calculate monthly payment
                    double monthlyInterestRate = interestRate / (12 * 100);
                    double monthlyPayment = (principal * monthlyInterestRate * 
                                           Math.pow(1 + monthlyInterestRate, term)) / 
                                           (Math.pow(1 + monthlyInterestRate, term) - 1);
                    
                    // Update the monthly payment label
                    DecimalFormat df = new DecimalFormat("$#,##0.00");
                    monthlyPaymentLabel.setText(df.format(monthlyPayment));
                } catch (Exception ex) {
                    monthlyPaymentLabel.setText("Invalid input");
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        formPanel.add(calculateButton, gbc);
        
        // Add a separator
        JSeparator separator = new JSeparator();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 15, 5);
        formPanel.add(separator, gbc);
        
        // Total amount with interest
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(new JLabel("Total Repayment:"), gbc);
        
        JLabel totalRepaymentLabel = new JLabel("$0.00");
        totalRepaymentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(totalRepaymentLabel, gbc);
        
        // Update total when calculate is pressed
        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double monthlyPayment = Double.parseDouble(monthlyPaymentLabel.getText().replaceAll("[^\\d.]", ""));
                    int term = Integer.parseInt((String)termCombo.getSelectedItem());
                    double totalRepayment = monthlyPayment * term;
                    
                    DecimalFormat df = new DecimalFormat("$#,##0.00");
                    totalRepaymentLabel.setText(df.format(totalRepayment));
                } catch (Exception ex) {
                    totalRepaymentLabel.setText("Invalid input");
                }
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("Submit Application");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double principal = ((Number)principalField.getValue()).doubleValue();
                    double interestRate = ((Number)interestField.getValue()).doubleValue();
                    int term = Integer.parseInt((String)termCombo.getSelectedItem());
                    String loanType = (String)loanTypeCombo.getSelectedItem();
                    
                    // Create the loan
                    boolean success = DatabaseManager.createLoan(
                            selectedAccount.getAccountId(),
                            loanType,
                            principal,
                            interestRate,
                            term);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Loan application submitted successfully!\n" +
                                "Your loan is pending approval.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        refreshLoanTable();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to submit loan application. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid input. Please check your entries.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void makePayment() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a loan to make a payment on.",
                    "No Loan Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int loanId = (int)loanTableModel.getValueAt(selectedRow, 0);
        String status = (String)loanTableModel.getValueAt(selectedRow, 7);
        String loanType = (String)loanTableModel.getValueAt(selectedRow, 1);
        String monthlyPaymentStr = (String)loanTableModel.getValueAt(selectedRow, 5);
        String remainingAmountStr = (String)loanTableModel.getValueAt(selectedRow, 6);
        
        // Check if loan status allows payments
        if (!status.equals("ACTIVE") && !status.equals("APPROVED")) {
            JOptionPane.showMessageDialog(this,
                    "Payments can only be made on active or approved loans.",
                    "Cannot Make Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Extract the numeric values from formatted strings
        double monthlyPayment = Double.parseDouble(monthlyPaymentStr.replaceAll("[^\\d.]", ""));
        double remainingAmount = Double.parseDouble(remainingAmountStr.replaceAll("[^\\d.]", ""));
        
        // Create payment dialog
        JDialog dialog = new JDialog(this, "Make Loan Payment", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Loan information
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Loan ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel("#" + loanId + " (" + loanType + ")"), gbc);
        
        // Monthly payment
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Monthly Payment:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel(monthlyPaymentStr), gbc);
        
        // Remaining amount
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Remaining Amount:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(new JLabel(remainingAmountStr), gbc);
        
        // Payment amount
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Payment Amount:"), gbc);
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setMinimum(0.01);
        currencyFormatter.setMaximum(remainingAmount);
        currencyFormatter.setAllowsInvalid(false);
        
        JFormattedTextField paymentField = new JFormattedTextField(currencyFormatter);
        paymentField.setValue(monthlyPayment);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(paymentField, gbc);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton payButton = new JButton("Make Payment");
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double paymentAmount = ((Number)paymentField.getValue()).doubleValue();
                    
                    // Ensure account has sufficient balance
                    if (selectedAccount.getBalance() < paymentAmount) {
                        JOptionPane.showMessageDialog(dialog,
                                "Insufficient funds in account to make this payment.",
                                "Insufficient Funds", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Make the payment
                    boolean success = DatabaseManager.makeLoanPayment(loanId, paymentAmount);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog,
                                "Payment made successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        // Refresh account data and loan table
                        selectedAccount = DatabaseManager.getAccountById(selectedAccount.getAccountId());
                        updateBalanceLabel();
                        refreshLoanTable();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "Failed to make payment. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Invalid input. Please check your entries.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(payButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        // For testing purposes
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Dummy user for testing
            User testUser = new User();
            testUser.setId(1);
            testUser.setUsername("testuser");
            testUser.setFullName("Test User");
            
            new Loan(testUser).setVisible(true);
        });
    }
} 