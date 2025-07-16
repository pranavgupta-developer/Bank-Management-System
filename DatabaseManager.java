import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_management_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // Add your database password here
    
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    // Initialize database - can be called when the application starts
    public static void initializeDatabase() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            System.out.println("Database connection established successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load JDBC driver: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }
    
    // User authentication
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If result exists, authentication successful
            }
            
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }
    
    // User registration
    public static boolean registerUser(String username, String password, String fullName, String email) {
        String query = "INSERT INTO users (username, password, full_name, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, email);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            // Get generated user_id to create default account
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    createDefaultAccount(userId, conn);
                    return true;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }
    
    // Create default account for new user
    private static void createDefaultAccount(int userId, Connection conn) throws SQLException {
        String query = "INSERT INTO accounts (user_id, account_number, account_type) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Generate simple account number (real implementation would be more robust)
            String accountNumber = "ACC" + userId + System.currentTimeMillis() % 10000;
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, accountNumber);
            pstmt.setString(3, "SAVINGS");
            
            pstmt.executeUpdate();
        }
    }
    
    // Check if username exists
    public static boolean usernameExists(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            System.err.println("Username check error: " + e.getMessage());
            return false;
        }
    }
    
    // Get user details
    public static User getUserDetails(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user details: " + e.getMessage());
        }
        
        return null;
    }
    
    // Get user accounts
    public static List<Account> getUserAccounts(int userId) {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    accounts.add(account);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting accounts: " + e.getMessage());
        }
        
        return accounts;
    }
    
    // Get account by ID
    public static Account getAccountById(int accountId) {
        String query = "SELECT * FROM accounts WHERE account_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    return account;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting account: " + e.getMessage());
        }
        
        return null;
    }
    
    // Get account by account number
    public static Account getAccountByNumber(String accountNumber) {
        String query = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccountId(rs.getInt("account_id"));
                    account.setUserId(rs.getInt("user_id"));
                    account.setAccountNumber(rs.getString("account_number"));
                    account.setAccountType(rs.getString("account_type"));
                    account.setBalance(rs.getDouble("balance"));
                    return account;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting account by number: " + e.getMessage());
        }
        
        return null;
    }
    
    // Deposit money
    public static boolean deposit(int accountId, double amount) {
        String query = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Record the transaction
                addTransaction(accountId, "DEPOSIT", amount, null, "Deposit to account", conn);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Deposit error: " + e.getMessage());
        }
        
        return false;
    }
    
    // Withdraw money
    public static boolean withdraw(int accountId, double amount) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // First check balance
            String balanceQuery = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
            try (PreparedStatement balancePstmt = conn.prepareStatement(balanceQuery)) {
                balancePstmt.setInt(1, accountId);
                
                try (ResultSet rs = balancePstmt.executeQuery()) {
                    if (rs.next()) {
                        double balance = rs.getDouble("balance");
                        
                        if (balance < amount) {
                            conn.rollback();
                            return false; // Insufficient funds
                        }
                        
                        // Proceed with withdrawal
                        String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                        try (PreparedStatement withdrawPstmt = conn.prepareStatement(withdrawQuery)) {
                            withdrawPstmt.setDouble(1, amount);
                            withdrawPstmt.setInt(2, accountId);
                            
                            int rowsAffected = withdrawPstmt.executeUpdate();
                            
                            if (rowsAffected > 0) {
                                // Record the transaction
                                addTransaction(accountId, "WITHDRAW", amount, null, "Withdrawal from account", conn);
                                conn.commit();
                                return true;
                            } else {
                                conn.rollback();
                                return false;
                            }
                        }
                    }
                }
            }
            
            conn.rollback();
            
        } catch (SQLException e) {
            System.err.println("Withdrawal error: " + e.getMessage());
        }
        
        return false;
    }
    
    // Transfer money
    public static boolean transfer(int fromAccountId, int toAccountId, double amount) {
        try (Connection conn = getConnection()) {
            // Use the stored procedure for transfer
            String query = "{CALL transfer_money(?, ?, ?, ?)}";
            
            try (CallableStatement cstmt = conn.prepareCall(query)) {
                cstmt.setInt(1, fromAccountId);
                cstmt.setInt(2, toAccountId);
                cstmt.setDouble(3, amount);
                cstmt.setString(4, "Transfer between accounts");
                
                cstmt.execute();
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Transfer error: " + e.getMessage());
            return false;
        }
    }
    
    // Add transaction record
    private static void addTransaction(int accountId, String type, double amount, 
                                     Integer recipientAccountId, String description,
                                     Connection conn) throws SQLException {
        
        String query = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                       "recipient_account_id, description) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, accountId);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            
            if (recipientAccountId != null) {
                pstmt.setInt(4, recipientAccountId);
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            
            pstmt.setString(5, description);
            
            pstmt.executeUpdate();
        }
    }
    
    // Get transaction history for an account
    public static List<Transaction> getTransactionHistory(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(rs.getInt("transaction_id"));
                    transaction.setAccountId(rs.getInt("account_id"));
                    transaction.setType(rs.getString("transaction_type"));
                    transaction.setAmount(rs.getDouble("amount"));
                    
                    // Get recipient account if it exists
                    if (rs.getObject("recipient_account_id") != null) {
                        transaction.setRecipientAccountId(rs.getInt("recipient_account_id"));
                    }
                    
                    transaction.setDate(rs.getTimestamp("transaction_date"));
                    transaction.setDescription(rs.getString("description"));
                    
                    transactions.add(transaction);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting transaction history: " + e.getMessage());
        }
        
        return transactions;
    }
    
    // Get cards for an account
    public static List<Card> getAccountCards(int accountId) {
        List<Card> cards = new ArrayList<>();
        String query = "SELECT * FROM cards WHERE account_id = ? ORDER BY issued_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Card card = new Card();
                    card.setCardId(rs.getInt("card_id"));
                    card.setAccountId(rs.getInt("account_id"));
                    card.setCardNumber(rs.getString("card_number"));
                    card.setCardType(rs.getString("card_type"));
                    card.setCvv(rs.getString("cvv"));
                    card.setExpiryDate(rs.getTimestamp("expiry_date"));
                    card.setIssuedDate(rs.getTimestamp("issued_date"));
                    card.setActive(rs.getBoolean("is_active"));
                    
                    cards.add(card);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting account cards: " + e.getMessage());
        }
        
        return cards;
    }
    
    // Create a new card
    public static boolean createCard(int accountId, String cardNumber, String cardType, 
                                    String cvv, Timestamp expiryDate) {
        String query = "INSERT INTO cards (account_id, card_number, card_type, cvv, expiry_date, issued_date, is_active) " +
                      "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, true)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setString(2, cardNumber);
            pstmt.setString(3, cardType);
            pstmt.setString(4, cvv);
            pstmt.setTimestamp(5, expiryDate);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating card: " + e.getMessage());
            return false;
        }
    }
    
    // Block a card
    public static boolean blockCard(int cardId) {
        String query = "UPDATE cards SET is_active = false WHERE card_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, cardId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error blocking card: " + e.getMessage());
            return false;
        }
    }
    
    // Create a new loan
    public static boolean createLoan(int accountId, String loanType, double principalAmount, 
                                    double interestRate, int termMonths) {
        // Calculate monthly payment using the formula: P = (P0 * r * (1 + r)^n) / ((1 + r)^n - 1)
        // where P0 is the principal, r is the monthly interest rate, and n is the term in months
        double monthlyInterestRate = interestRate / (12 * 100); // Convert from annual percentage to monthly decimal
        double monthlyPayment = (principalAmount * monthlyInterestRate * 
                               Math.pow(1 + monthlyInterestRate, termMonths)) / 
                               (Math.pow(1 + monthlyInterestRate, termMonths) - 1);
        
        // Round to 2 decimal places
        monthlyPayment = Math.round(monthlyPayment * 100.0) / 100.0;
        
        // Calculate end date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, termMonths);
        Timestamp endDate = new Timestamp(calendar.getTimeInMillis());
        
        String query = "INSERT INTO loans (account_id, loan_type, principal_amount, interest_rate, " +
                       "term_months, monthly_payment, end_date, remaining_amount, status) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            pstmt.setString(2, loanType);
            pstmt.setDouble(3, principalAmount);
            pstmt.setDouble(4, interestRate);
            pstmt.setInt(5, termMonths);
            pstmt.setDouble(6, monthlyPayment);
            pstmt.setTimestamp(7, endDate);
            pstmt.setDouble(8, principalAmount);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating loan: " + e.getMessage());
            return false;
        }
    }
    
    // Get all loans for an account
    public static List<Loan> getLoansByAccountId(int accountId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE account_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, accountId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setAccountId(rs.getInt("account_id"));
                    loan.setLoanType(rs.getString("loan_type"));
                    loan.setPrincipalAmount(rs.getDouble("principal_amount"));
                    loan.setInterestRate(rs.getDouble("interest_rate"));
                    loan.setTermMonths(rs.getInt("term_months"));
                    loan.setMonthlyPayment(rs.getDouble("monthly_payment"));
                    loan.setStartDate(rs.getTimestamp("start_date"));
                    loan.setEndDate(rs.getTimestamp("end_date"));
                    loan.setRemainingAmount(rs.getDouble("remaining_amount"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCreatedAt(rs.getTimestamp("created_at"));
                    loan.setUpdatedAt(rs.getTimestamp("updated_at"));
                    
                    loans.add(loan);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    // Make a loan payment
    public static boolean makeLoanPayment(int loanId, double paymentAmount) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Get loan details
            String loanQuery = "SELECT account_id, monthly_payment, remaining_amount, status FROM loans WHERE loan_id = ? FOR UPDATE";
            try (PreparedStatement loanStmt = conn.prepareStatement(loanQuery)) {
                loanStmt.setInt(1, loanId);
                
                try (ResultSet rs = loanStmt.executeQuery()) {
                    if (rs.next()) {
                        int accountId = rs.getInt("account_id");
                        double monthlyPayment = rs.getDouble("monthly_payment");
                        double remainingAmount = rs.getDouble("remaining_amount");
                        String status = rs.getString("status");
                        
                        // Check if loan is active and payment can be made
                        if (!status.equals("ACTIVE") && !status.equals("APPROVED")) {
                            conn.rollback();
                            return false;
                        }
                        
                        // Check if account has sufficient balance
                        String balanceQuery = "SELECT balance FROM accounts WHERE account_id = ? FOR UPDATE";
                        try (PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery)) {
                            balanceStmt.setInt(1, accountId);
                            
                            try (ResultSet balanceRs = balanceStmt.executeQuery()) {
                                if (balanceRs.next()) {
                                    double balance = balanceRs.getDouble("balance");
                                    
                                    if (balance < paymentAmount) {
                                        conn.rollback();
                                        return false; // Insufficient funds
                                    }
                                    
                                    // Update account balance
                                    String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                                    try (PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery)) {
                                        updateBalanceStmt.setDouble(1, paymentAmount);
                                        updateBalanceStmt.setInt(2, accountId);
                                        updateBalanceStmt.executeUpdate();
                                    }
                                    
                                    // Calculate new remaining amount
                                    double newRemainingAmount = remainingAmount - paymentAmount;
                                    if (newRemainingAmount < 0) {
                                        newRemainingAmount = 0;
                                    }
                                    
                                    // Update loan status if paid off
                                    String newStatus = status;
                                    if (newRemainingAmount == 0) {
                                        newStatus = "PAID";
                                    }
                                    
                                    // Update loan
                                    String updateLoanQuery = "UPDATE loans SET remaining_amount = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE loan_id = ?";
                                    try (PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanQuery)) {
                                        updateLoanStmt.setDouble(1, newRemainingAmount);
                                        updateLoanStmt.setString(2, newStatus);
                                        updateLoanStmt.setInt(3, loanId);
                                        updateLoanStmt.executeUpdate();
                                    }
                                    
                                    // Record transaction
                                    addTransaction(accountId, "WITHDRAW", paymentAmount, null, "Loan payment for loan #" + loanId, conn);
                                    
                                    conn.commit();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            
            conn.rollback();
            
        } catch (SQLException e) {
            System.err.println("Error making loan payment: " + e.getMessage());
        }
        
        return false;
    }
    
    // Approve loan and deposit the amount to the account
    public static boolean approveLoan(int loanId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Get loan details
            String loanQuery = "SELECT account_id, principal_amount, status FROM loans WHERE loan_id = ? FOR UPDATE";
            try (PreparedStatement loanStmt = conn.prepareStatement(loanQuery)) {
                loanStmt.setInt(1, loanId);
                
                try (ResultSet rs = loanStmt.executeQuery()) {
                    if (rs.next()) {
                        int accountId = rs.getInt("account_id");
                        double principalAmount = rs.getDouble("principal_amount");
                        String status = rs.getString("status");
                        
                        // Check if loan is pending
                        if (!status.equals("PENDING")) {
                            conn.rollback();
                            return false;
                        }
                        
                        // Update account balance
                        String updateBalanceQuery = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
                        try (PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery)) {
                            updateBalanceStmt.setDouble(1, principalAmount);
                            updateBalanceStmt.setInt(2, accountId);
                            updateBalanceStmt.executeUpdate();
                        }
                        
                        // Update loan status
                        String updateLoanQuery = "UPDATE loans SET status = 'ACTIVE', start_date = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE loan_id = ?";
                        try (PreparedStatement updateLoanStmt = conn.prepareStatement(updateLoanQuery)) {
                            updateLoanStmt.setInt(1, loanId);
                            updateLoanStmt.executeUpdate();
                        }
                        
                        // Record transaction
                        addTransaction(accountId, "DEPOSIT", principalAmount, null, "Loan disbursement for loan #" + loanId, conn);
                        
                        conn.commit();
                        return true;
                    }
                }
            }
            
            conn.rollback();
            
        } catch (SQLException e) {
            System.err.println("Error approving loan: " + e.getMessage());
        }
        
        return false;
    }
    
    // Get card by card number (for verification)
    public static Card getCardByNumber(String cardNumber) {
        String query = "SELECT * FROM cards WHERE card_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, cardNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Card card = new Card();
                    card.setCardId(rs.getInt("card_id"));
                    card.setAccountId(rs.getInt("account_id"));
                    card.setCardNumber(rs.getString("card_number"));
                    card.setCardType(rs.getString("card_type"));
                    card.setCvv(rs.getString("cvv"));
                    card.setExpiryDate(rs.getTimestamp("expiry_date"));
                    card.setIssuedDate(rs.getTimestamp("issued_date"));
                    card.setActive(rs.getBoolean("is_active"));
                    return card;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting card by number: " + e.getMessage());
        }
        
        return null;
    }

    static class Loan {
        private int loanId;
        private int accountId;
        private String loanType;
        private double principalAmount;
        private double interestRate;
        private int termMonths;
        private double monthlyPayment;
        private Timestamp startDate;
        private Timestamp endDate;
        private double remainingAmount;
        private String status;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        // Getters and setters
        public int getLoanId() { return loanId; }
        public void setLoanId(int loanId) { this.loanId = loanId; }
        
        public int getAccountId() { return accountId; }
        public void setAccountId(int accountId) { this.accountId = accountId; }
        
        public String getLoanType() { return loanType; }
        public void setLoanType(String loanType) { this.loanType = loanType; }
        
        public double getPrincipalAmount() { return principalAmount; }
        public void setPrincipalAmount(double principalAmount) { this.principalAmount = principalAmount; }
        
        public double getInterestRate() { return interestRate; }
        public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
        
        public int getTermMonths() { return termMonths; }
        public void setTermMonths(int termMonths) { this.termMonths = termMonths; }
        
        public double getMonthlyPayment() { return monthlyPayment; }
        public void setMonthlyPayment(double monthlyPayment) { this.monthlyPayment = monthlyPayment; }
        
        public Timestamp getStartDate() { return startDate; }
        public void setStartDate(Timestamp startDate) { this.startDate = startDate; }
        
        public Timestamp getEndDate() { return endDate; }
        public void setEndDate(Timestamp endDate) { this.endDate = endDate; }
        
        public double getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
        
        public Timestamp getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    }
}

// Model classes
class User {
    private int id;
    private String username;
    private String fullName;
    private String email;
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class Account {
    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType;
    private double balance;
    
    // Getters and setters
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}

class Transaction {
    private int transactionId;
    private int accountId;
    private String type;
    private double amount;
    private Integer recipientAccountId;
    private Timestamp date;
    private String description;
    
    // Getters and setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public Integer getRecipientAccountId() { return recipientAccountId; }
    public void setRecipientAccountId(Integer recipientAccountId) { this.recipientAccountId = recipientAccountId; }
    
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}