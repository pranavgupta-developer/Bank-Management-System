import java.sql.Timestamp;

public class Card {
    private int cardId;
    private int accountId;
    private String cardNumber;
    private String cardType;
    private String cvv;
    private Timestamp expiryDate;
    private Timestamp issuedDate;
    private boolean active;
    
    // Constructors
    public Card() {
    }
    
    public Card(int cardId, int accountId, String cardNumber, String cardType, 
                String cvv, Timestamp expiryDate, Timestamp issuedDate, boolean active) {
        this.cardId = cardId;
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.issuedDate = issuedDate;
        this.active = active;
    }
    
    // Getters and Setters
    public int getCardId() {
        return cardId;
    }
    
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public Timestamp getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Timestamp expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Timestamp getIssuedDate() {
        return issuedDate;
    }
    
    public void setIssuedDate(Timestamp issuedDate) {
        this.issuedDate = issuedDate;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Other helper methods
    // For security reasons, we should mask the card number for display
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 16) {
            return "Invalid card number";
        }
        
        return "**** **** **** " + cardNumber.substring(12);
    }
    
    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", accountId=" + accountId +
                ", cardNumber='" + getMaskedCardNumber() + '\'' +
                ", cardType='" + cardType + '\'' +
                ", expiryDate=" + expiryDate +
                ", issuedDate=" + issuedDate +
                ", active=" + active +
                '}';
    }
} 