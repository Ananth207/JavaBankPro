package javabank_application.projectmod;

import java.sql.*;

public class TransactionHistory 
{
    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public TransactionHistory(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void recordTransaction(String senderUsername, String receiverUsername, double amount){
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://sql12.freesqldatabase.com:3306/sql12665091","sql12665091", "USCJhlW5K2")) {
            String query = "INSERT INTO transaction_history (sender_username, receiver_username, amount, transaction_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, senderUsername);
                preparedStatement.setString(2, receiverUsername);
                preparedStatement.setDouble(3, amount);
                preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Transaction recorded successfully.");
                } else {
                    System.out.println("Error recording transaction. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
