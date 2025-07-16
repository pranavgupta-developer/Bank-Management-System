-- Database creation
CREATE DATABASE IF NOT EXISTS bank_management_system;
USE bank_management_system;

-- Users table to store user authentication details
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,  
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Accounts table to store user account information
CREATE TABLE IF NOT EXISTS accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    account_type ENUM('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT') DEFAULT 'SAVINGS',
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Transactions table to track all account transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER_OUT', 'TRANSFER_IN') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    recipient_account_id INT,  
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(255),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_account_id) REFERENCES accounts(account_id) ON DELETE SET NULL
);

-- Cards table to store bank cards information
CREATE TABLE IF NOT EXISTS cards (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    card_type ENUM('VISA', 'MASTERCARD', 'DISCOVER', 'AMEX') NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Loans table to store loan information
CREATE TABLE IF NOT EXISTS loans (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    loan_type ENUM('PERSONAL', 'HOME', 'AUTO', 'EDUCATION', 'BUSINESS') NOT NULL,
    principal_amount DECIMAL(15, 2) NOT NULL,
    interest_rate DECIMAL(5, 2) NOT NULL,  -- Annual interest rate in percentage
    term_months INT NOT NULL,  -- Loan term in months
    monthly_payment DECIMAL(15, 2) NOT NULL,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP NOT NULL,
    remaining_amount DECIMAL(15, 2) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'ACTIVE', 'PAID', 'DEFAULTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_cards_account_id ON cards(account_id);
CREATE INDEX idx_cards_card_number ON cards(card_number);
CREATE INDEX idx_loans_account_id ON loans(account_id);

-- Insert sample data insertion (optional)
-- Insert sample users
INSERT INTO users (username, password, full_name, email) VALUES
('johndoe', 'password123', 'John Doe', 'john.doe@example.com'),
('janedoe', 'password456', 'Jane Doe', 'jane.doe@example.com');

-- Create accounts for sample users (user_id 1 and 2)
INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES
(1, '1000001', 'SAVINGS', 5000.00),
(1, '1000002', 'CHECKING', 2500.00),
(2, '2000001', 'SAVINGS', 7500.00);

-- Insert sample transactions
INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES
(1, 'DEPOSIT', 5000.00, 'Initial deposit'),
(2, 'DEPOSIT', 2500.00, 'Initial deposit'),
(3, 'DEPOSIT', 7500.00, 'Initial deposit');

-- Sample transfer transaction
INSERT INTO transactions (account_id, transaction_type, amount, recipient_account_id, description) VALUES
(1, 'TRANSFER_OUT', 500.00, 3, 'Transfer to Jane Doe');

INSERT INTO transactions (account_id, transaction_type, amount, recipient_account_id, description) VALUES
(3, 'TRANSFER_IN', 500.00, 1, 'Transfer from John Doe');

-- Insert sample card data
INSERT INTO cards (account_id, card_number, card_type, cvv, expiry_date, issued_date, is_active) VALUES
(1, '4111111111111111', 'VISA', '123', DATE_ADD(CURRENT_DATE, INTERVAL 3 YEAR), CURRENT_TIMESTAMP, true),
(2, '5555555555554444', 'MASTERCARD', '321', DATE_ADD(CURRENT_DATE, INTERVAL 2 YEAR), CURRENT_TIMESTAMP, true),
(3, '4222222222222222', 'VISA', '456', DATE_ADD(CURRENT_DATE, INTERVAL 4 YEAR), CURRENT_TIMESTAMP, true);

-- Insert sample loan data
INSERT INTO loans (account_id, loan_type, principal_amount, interest_rate, term_months, monthly_payment, start_date, end_date, remaining_amount, status) VALUES
(1, 'PERSONAL', 10000.00, 5.75, 24, 441.12, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_DATE, INTERVAL 24 MONTH), 10000.00, 'APPROVED'),
(2, 'AUTO', 25000.00, 4.25, 60, 462.81, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_DATE, INTERVAL 60 MONTH), 25000.00, 'ACTIVE'),
(3, 'HOME', 200000.00, 3.5, 360, 898.09, CURRENT_TIMESTAMP, DATE_ADD(CURRENT_DATE, INTERVAL 360 MONTH), 200000.00, 'ACTIVE');

-- Create stored procedure for transfers (optional)
DELIMITER //
CREATE PROCEDURE transfer_money(
    IN sender_account_id INT,
    IN recipient_account_id INT,
    IN transfer_amount DECIMAL(15, 2),
    IN transfer_description VARCHAR(255)
)
BEGIN
    DECLARE current_balance DECIMAL(15, 2);
    
    -- Start transaction
    START TRANSACTION;
    
    -- Get sender's current balance
    SELECT balance INTO current_balance FROM accounts WHERE account_id = sender_account_id FOR UPDATE;
    
    -- Check if sender has sufficient balance
    IF current_balance >= transfer_amount THEN
        -- Update sender's account
        UPDATE accounts SET balance = balance - transfer_amount WHERE account_id = sender_account_id;
        
        -- Update recipient's account
        UPDATE accounts SET balance = balance + transfer_amount WHERE account_id = recipient_account_id;
        
        -- Record outgoing transaction for sender
        INSERT INTO transactions (account_id, transaction_type, amount, recipient_account_id, description)
        VALUES (sender_account_id, 'TRANSFER_OUT', transfer_amount, recipient_account_id, transfer_description);
        
        -- Record incoming transaction for recipient
        INSERT INTO transactions (account_id, transaction_type, amount, recipient_account_id, description)
        VALUES (recipient_account_id, 'TRANSFER_IN', transfer_amount, sender_account_id, transfer_description);
        
        -- Commit transaction
        COMMIT;
    ELSE
        -- Insufficient funds, rollback
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient funds for transfer';
    END IF;
END //
DELIMITER ;

-- Index creation for better performance
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_transaction_date ON transactions(transaction_date);
CREATE INDEX idx_accounts_user_id ON accounts(user_id); 

ALTER TABLE loans
MODIFY COLUMN end_date DATETIME NOT NULL;

ALTER TABLE loans
MODIFY COLUMN start_date DATETIME DEFAULT CURRENT_TIMESTAMP;
