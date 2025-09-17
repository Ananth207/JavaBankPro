package javabank_application.projectmod;

import java.util.*;
import java.sql.*;

public class BalanceDisplay {

    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public BalanceDisplay(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void displayBalance(Scanner scanner, String loggedUser) {
        String username = loggedUser;

        if (validateUser(username)) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM bank_details WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            displayUserDetails(resultSet);
                            double balance = resultSet.getDouble("balance");
                            System.out.println("****************************************************");
                            System.out.println("*                  Account Balance                  *");
                            System.out.println("****************************************************");
                            System.out.println("Username: " + resultSet.getString("username"));
                            System.out.println("Current Balance: $" + balance);
                            System.out.println("****************************************************");
                        } else {
                            System.out.println("Error retrieving balance. Please try again.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User not found or invalid phone number. Unable to display balance.");
        }
    }

    private void displayUserDetails(ResultSet resultSet) throws SQLException {
        System.out.println("****************************************************");
        System.out.println("*                User Account Details              *");
        System.out.println("****************************************************");
        System.out.println("Username: " + resultSet.getString("username"));
        System.out.println("First Name: " + resultSet.getString("firstname"));
        System.out.println("Last Name: " + resultSet.getString("lastname"));
        System.out.println("Father Name: " + resultSet.getString("fathername"));
        System.out.println("Date of Birth: " + resultSet.getString("dob"));
        System.out.println("Address: " + resultSet.getString("address"));
        System.out.println("Phone Number: " + resultSet.getLong("phone"));
        System.out.println("Email ID: " + resultSet.getString("email"));
        System.out.println("PAN Number: " + resultSet.getString("pan"));
        System.out.println("Account Number: " + resultSet.getLong("acc_no"));
        System.out.println("****************************************************");
    }

    public boolean validateUser(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM bank_details WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If there is a match, the user is valid
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
