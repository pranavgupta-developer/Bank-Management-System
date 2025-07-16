# Bank Management System

A comprehensive Java-based Banking System with a graphical user interface that provides essential banking functionalities for both customers and bank staff.

## Features

- **User Authentication**
  - Secure login system
  - New user registration
  - Password protection

- **Account Management**
  - View account details
  - Check account balance
  - Transaction history
  - Account statements

- **Card Services**
  - Credit/Debit card management
  - Card activation/deactivation
  - View card details
  - Card transaction history

- **Loan Management**
  - Apply for different types of loans
  - Loan status tracking
  - EMI calculation
  - Loan repayment schedules

- **Dashboard**
  - User-friendly interface
  - Quick access to all banking features
  - Real-time account updates

## Technical Details

- **Programming Language**: Java
- **Database**: MySQL (using `bank_management_system.sql`)
- **Architecture**: Object-Oriented Design
- **User Interface**: Java Swing/AWT

## Project Structure

```
Bank-Management-System/
├── Source Files
│   ├── BankManagementSystem.java (Main application)
│   ├── LoginPage.java (Authentication)
│   ├── RegisterPage.java (User registration)
│   ├── Dashboard.java (Main interface)
│   ├── AccountPage.java (Account operations)
│   ├── Cards.java (Card management)
│   ├── Loan.java (Loan processing)
│   └── DatabaseManager.java (Database operations)
├── Database
│   └── bank_management_system.sql
└── Compiled Classes
    └── Class/ (Contains all .class files)
```

## Setup Instructions

1. **Prerequisites**
   - Java Development Kit (JDK)
   - MySQL Server
   - MySQL Connector/J (JDBC driver)

2. **Database Setup**
   - Create a new MySQL database
   - Import the `bank_management_system.sql` file

3. **Running the Application**
   - Compile all Java files
   - Run `BankManagementSystem.java`

## Security Features

- Password encryption
- Secure database connections
- Session management
- Input validation

## Contributing

Feel free to contribute to this project by:
1. Forking the repository
2. Creating your feature branch
3. Committing your changes
4. Pushing to the branch
5. Creating a new Pull Request

## Author

- [pranavgupta-developer](https://github.com/pranavgupta-developer)

## License

This project is licensed under the MIT License - see the LICENSE file for details.
