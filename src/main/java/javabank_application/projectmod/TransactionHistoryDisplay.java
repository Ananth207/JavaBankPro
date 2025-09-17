package javabank_application.projectmod;

import java.sql.*;

public class TransactionHistoryDisplay {
    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public TransactionHistoryDisplay(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void displayTransactionHistory(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM transaction_history " +
                    "WHERE sender_username = ? OR receiver_username = ? " +
                    "ORDER BY transaction_time DESC " +
                    "LIMIT 3";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("***************************************");
                        System.out.println("Recent Transaction History for " + username + ":");
                        System.out.println("***************************************");
                        do {
                            displayTransactionDetails(resultSet);
                        } while (resultSet.next());
                    } else {
                        System.out.println("No transaction history found for " + username + ".");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayTransactionDetails(ResultSet resultSet) throws SQLException {
        System.out.println("Sender: " + resultSet.getString("sender_username"));
        System.out.println("Receiver: " + resultSet.getString("receiver_username"));
        System.out.println("Amount: $" + resultSet.getDouble("amount"));
        System.out.println("Transaction Time: " + resultSet.getTimestamp("transaction_time"));
        System.out.println("***************************************");
    }
}
