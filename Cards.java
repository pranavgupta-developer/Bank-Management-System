import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Cards extends JFrame {
    private User user;
    private List<Account> accounts;
    private Account selectedAccount;
    private JComboBox<String> accountSelector;
    private JPanel cardDisplayPanel;
    private JButton generateCardButton;
    private JButton blockCardButton;
    private JButton backButton;
    
    public Cards(User user) {
        this.user = user;
        setTitle("Bank Management System - Card Management");
        setSize(800, 600);
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
        
        // Top panel with account selector
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180)); // Steel Blue
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Card Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add account selector
        JPanel selectorPanel = new JPanel();
        selectorPanel.setOpaque(false);
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
                    refreshCardDisplay();
                }
            }
        });
        selectorPanel.add(accountSelector);
        
        topPanel.add(selectorPanel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with card display
        cardDisplayPanel = new JPanel();
        cardDisplayPanel.setLayout(new BoxLayout(cardDisplayPanel, BoxLayout.Y_AXIS));
        cardDisplayPanel.setBackground(new Color(240, 248, 255));
        cardDisplayPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(cardDisplayPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        generateCardButton = new JButton("Generate New Card");
        generateCardButton.setBackground(new Color(46, 139, 87)); // Sea Green
        generateCardButton.setForeground(Color.BLACK);
        generateCardButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateNewCard();
            }
        });
        
        blockCardButton = new JButton("Block Selected Card");
        blockCardButton.setBackground(new Color(220, 20, 60)); // Crimson
        blockCardButton.setForeground(Color.BLACK);
        blockCardButton.setFont(new Font("Arial", Font.BOLD, 14));
        blockCardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blockSelectedCard();
            }
        });
        
        backButton = new JButton("Back to Dashboard");
        backButton.setBackground(new Color(70, 130, 180)); // Steel Blue
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Dashboard dashboard = new Dashboard(user);
                dashboard.setVisible(true);
                dispose();
            }
        });
        
        buttonPanel.add(generateCardButton);
        buttonPanel.add(blockCardButton);
        buttonPanel.add(backButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Load card display initially
        refreshCardDisplay();
    }
    
    private void refreshCardDisplay() {
        cardDisplayPanel.removeAll();
        
        // Get cards for the selected account
        List<Card> cards = DatabaseManager.getAccountCards(selectedAccount.getAccountId());
        
        if (cards.isEmpty()) {
            JLabel noCardsLabel = new JLabel("No cards available for this account. Generate a new card.");
            noCardsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noCardsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            cardDisplayPanel.add(Box.createVerticalStrut(50));
            cardDisplayPanel.add(noCardsLabel);
        } else {
            for (Card card : cards) {
                cardDisplayPanel.add(createCardPanel(card));
                cardDisplayPanel.add(Box.createVerticalStrut(20));
            }
        }
        
        cardDisplayPanel.revalidate();
        cardDisplayPanel.repaint();
    }
    
    private JPanel createCardPanel(Card card) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(card.isActive() ? new Color(70, 130, 180) : new Color(128, 128, 128)); // Blue if active, gray if blocked
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        cardPanel.setPreferredSize(new Dimension(380, 200));
        cardPanel.setMaximumSize(new Dimension(380, 200));
        
        // Bank name
        JLabel bankLabel = new JLabel("BANK MANAGEMENT SYSTEM");
        bankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bankLabel.setForeground(Color.WHITE);
        cardPanel.add(bankLabel, BorderLayout.NORTH);
        
        // Card number (formatted with spaces for readability)
        String formattedCardNumber = formatCardNumber(card.getCardNumber());
        JLabel cardNumberLabel = new JLabel(formattedCardNumber);
        cardNumberLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        cardNumberLabel.setForeground(Color.WHITE);
        
        // Card holder name and expiry in center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(cardNumberLabel, BorderLayout.NORTH);
        
        // Card holder
        JLabel cardHolderLabel = new JLabel("CARD HOLDER");
        cardHolderLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        cardHolderLabel.setForeground(new Color(220, 220, 220));
        
        JLabel nameLabel = new JLabel(user.getFullName().toUpperCase());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        
        JPanel holderPanel = new JPanel(new BorderLayout());
        holderPanel.setOpaque(false);
        holderPanel.add(cardHolderLabel, BorderLayout.NORTH);
        holderPanel.add(nameLabel, BorderLayout.CENTER);
        
        // Expiry date
        JLabel expiryLabel = new JLabel("VALID THRU");
        expiryLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        expiryLabel.setForeground(new Color(220, 220, 220));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yy");
        JLabel validThruLabel = new JLabel(dateFormat.format(card.getExpiryDate()));
        validThruLabel.setFont(new Font("Arial", Font.BOLD, 14));
        validThruLabel.setForeground(Color.WHITE);
        
        JPanel expiryPanel = new JPanel(new BorderLayout());
        expiryPanel.setOpaque(false);
        expiryPanel.add(expiryLabel, BorderLayout.NORTH);
        expiryPanel.add(validThruLabel, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new BorderLayout(15, 0));
        infoPanel.setOpaque(false);
        infoPanel.add(holderPanel, BorderLayout.WEST);
        infoPanel.add(expiryPanel, BorderLayout.EAST);
        
        centerPanel.add(infoPanel, BorderLayout.SOUTH);
        cardPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Status indicator at bottom
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel(card.isActive() ? "ACTIVE" : "BLOCKED");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(card.isActive() ? new Color(144, 238, 144) : new Color(255, 99, 71));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        // Card type (VISA, MASTERCARD, etc.)
        JLabel cardTypeLabel = new JLabel(card.getCardType().toUpperCase());
        cardTypeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cardTypeLabel.setForeground(Color.WHITE);
        
        statusPanel.add(cardTypeLabel, BorderLayout.EAST);
        
        cardPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Store the card ID as a client property for reference when blocking
        cardPanel.putClientProperty("cardId", card.getCardId());
        
        // Add mouse listener to make the card selectable
        cardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Deselect all cards
                for (Component comp : cardDisplayPanel.getComponents()) {
                    if (comp instanceof JPanel) {
                        ((JComponent) comp).setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(0, 0, 0), 1),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
                    }
                }
                
                // Select this card
                cardPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
            }
        });
        
        return cardPanel;
    }
    
    private String formatCardNumber(String cardNumber) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < cardNumber.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                formatted.append(" ");
            }
            formatted.append(cardNumber.charAt(i));
        }
        return formatted.toString();
    }
    
    private void generateNewCard() {
        // Ask for card type
        String[] cardTypes = {"VISA", "MASTERCARD"};
        String cardType = (String) JOptionPane.showInputDialog(
                this, "Select card type:", "Card Type", 
                JOptionPane.QUESTION_MESSAGE, null, cardTypes, cardTypes[0]);
        
        if (cardType == null) {
            return; // User canceled
        }
        
        // Generate a card number
        String cardNumber = generateCardNumber(cardType);
        
        // Generate CVV
        String cvv = generateCVV();
        
        // Set expiry date (4 years from now)
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 4);
        Timestamp expiryDate = new Timestamp(calendar.getTimeInMillis());
        
        // Create the card in the database
        boolean success = DatabaseManager.createCard(selectedAccount.getAccountId(), cardNumber, cardType, cvv, expiryDate);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "New card generated successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCardDisplay();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to generate new card. Please try again.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateCardNumber(String cardType) {
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        
        // First digits based on card type
        if (cardType.equals("VISA")) {
            cardNumber.append("4");
        } else if (cardType.equals("MASTERCARD")) {
            cardNumber.append("5");
        }
        
        // Generate the rest of the 16-digit card number
        while (cardNumber.length() < 16) {
            cardNumber.append(random.nextInt(10));
        }
        
        return cardNumber.toString();
    }
    
    private String generateCVV() {
        Random random = new Random();
        StringBuilder cvv = new StringBuilder();
        
        // Generate a 3-digit CVV
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        
        return cvv.toString();
    }
    
    private void blockSelectedCard() {
        // Find the selected card
        Integer selectedCardId = null;
        
        for (Component comp : cardDisplayPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel cardPanel = (JPanel) comp;
                if (cardPanel.getBorder().toString().contains("javax.swing.border.CompoundBorder")) {
                    selectedCardId = (Integer) cardPanel.getClientProperty("cardId");
                    break;
                }
            }
        }
        
        if (selectedCardId == null) {
            JOptionPane.showMessageDialog(this, "Please select a card to block", 
                    "No Card Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ask for confirmation
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to block this card? This action cannot be undone.", 
                "Confirm Block Card", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = DatabaseManager.blockCard(selectedCardId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Card has been blocked successfully", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshCardDisplay();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to block card. Please try again.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
            
            new Cards(testUser).setVisible(true);
        });
    }
} 