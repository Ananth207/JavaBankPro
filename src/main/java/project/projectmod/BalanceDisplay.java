package project.projectmod;
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

    public double displayBalance(Scanner scanner,String loggeduser)
    {
        String username = loggeduser;
    double balance = 0;
        if (validateUser(username)) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT balance FROM bank_details WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            balance = resultSet.getDouble("balance");
                            System.out.println("Your current balance: $" + balance);
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
        return balance;
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
