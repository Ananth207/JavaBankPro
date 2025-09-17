package javabank_application.projectmod;
import com.ananth.BankLoginSystem.BankLoginSystemApplication;

import java.util.*;
import java.sql.*;

public class TransactionSystem
{

    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;
    

    public TransactionSystem(String DB_URL, String DB_USER, String DB_PASSWORD) 
    {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
        //transactionHistory = new TransactionHistory(DB_URL, DB_USER, DB_PASSWORD);
    }
    public TransactionHistory transactionHistory = new TransactionHistory(DB_URL, DB_USER, DB_PASSWORD);

    public void performTransaction(Scanner scanner, String loggeduser) 
    {
        String senderUsername = loggeduser;
        System.out.print("Enter Receiver's phone number: ");
        long receiverPhoneNumber = scanner.nextLong();
        String receiverUsername = null;

        if (validateReceiver(receiverPhoneNumber)) 
        {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) 
            {
                String query = "SELECT * FROM bank_details WHERE phone = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) 
                {
                    preparedStatement.setLong(1, receiverPhoneNumber);
                    //preparedStatement.setInt(2, receiverPhoneNumber);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) 
                    {
                        if (resultSet.next()) 
                        {
                            receiverUsername = resultSet.getString("username");

                            double depositAmount;
                            System.out.print("Enter amount to send: ");
                            depositAmount = scanner.nextDouble();
                            BankLoginSystemApplication emailer = new BankLoginSystemApplication();

                            emailer.send_email();
                            System.out.print("Enter otp: ");
                            int otp = scanner.nextInt();

                            if (checkBalance(senderUsername, depositAmount) && otp == emailer.otp_created)
                            {
                                try (Connection connection1 = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) 
                                {
                                    // Deduct amount from sender's account
                                    String deductQuery = "UPDATE bank_details SET balance = balance - ? WHERE username = ?";
                                    try (PreparedStatement deductStatement = connection1.prepareStatement(deductQuery)) 
                                    {
                                        deductStatement.setDouble(1, depositAmount);
                                        deductStatement.setString(2, senderUsername);

                                        int deductRowsAffected = deductStatement.executeUpdate();
                                        if (deductRowsAffected > 0) 
                                        {
                                            // Add amount to receiver's account
                                            String addQuery = "UPDATE bank_details SET balance = balance + ? WHERE phone = ?";
                                            try (PreparedStatement addStatement = connection.prepareStatement(addQuery)) 
                                            {
                                                addStatement.setDouble(1, depositAmount);
                                                addStatement.setLong(2, receiverPhoneNumber);

                                                int addRowsAffected = addStatement.executeUpdate();
                                                if (addRowsAffected > 0) 
                                                {
                                                    System.out.println("Money transfer successful.");
                                                    
                                                    // Record the transaction in the transaction history
                                                    transactionHistory.recordTransaction(senderUsername, receiverUsername, depositAmount);
                                                } 
                                                else {
                                                    System.out.println("Error processing transfer to receiver. Please try again.");
                                                }
                                            }
                                        } 
                                        else 
                                        {
                                            System.out.println("Error processing transfer from sender. Please try again.");
                                        }
                                    }
                                } 
                                catch (SQLException e) 
                                {
                                    e.printStackTrace();
                                }
                            } 
                            else 
                            {
                            System.out.println("Insufficient funds or error processing transfer. Transaction aborted.");
                            }
                        }
                        else 
                        {
                        System.out.println("Error retrieving receiver's username . Please try again.");
                        //return null;
                        } 
                    }
                }
            }
            catch (SQLException e) 
            {
                e.printStackTrace();
            }
        
        } 
        else 
        {
            System.out.println("Receiver not found or invalid phone number. Transaction aborted.");
        }
    }

    

    private boolean validateReceiver(long receiverPhoneNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM bank_details WHERE phone = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setLong(1, receiverPhoneNumber);
                //preparedStatement.setInt(2, receiverPhoneNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If there is a match, the receiver is valid
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkBalance(String senderUsername, double transactionAmount) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT balance FROM bank_details WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, senderUsername);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        double balance = resultSet.getDouble("balance");
                        // Check if sender's balance is sufficient after deducting the transaction amount
                        return balance >= transactionAmount;
                    } else {
                        System.out.println("Error retrieving sender's balance. Please try again.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

