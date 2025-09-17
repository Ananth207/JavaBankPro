package project.projectmod;
import java.util.*;
import java.sql.*;
public class AccountDetails {

    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public AccountDetails(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }

    public void displayAccountDetails(Scanner scanner,String loggeduser) {
        String username = loggeduser;

        if (validateUser(username)) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM bank_details WHERE username = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, username);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            displayUserDetails(resultSet);
                            editUserDetails(scanner, resultSet);
                        } else {
                            System.out.println("Error retrieving user details. Please try again.");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("User not found or invalid phone number. Unable to display account details.");
        }
    }

    private void displayUserDetails(ResultSet resultSet) throws SQLException {
        System.out.println("User Details:\n");
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
        System.out.println("Balance: $" + resultSet.getDouble("balance"));
        System.out.println();
    }

    private void editUserDetails(Scanner scanner, ResultSet resultSet) throws SQLException {
        System.out.println("Edit Options:");
        System.out.println("1. Edit First Name");
        System.out.println("2. Edit Last Name");
        System.out.println("3. Edit Father Name");
        System.out.println("4. Edit Date of Birth");
        System.out.println("5. Edit Address");
        System.out.println("6. Edit Email ID");
        System.out.println("7. Edit PAN Number");
        System.out.println("8. Exit");
        System.out.print("Choose an option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        switch (option) {
            case 1:
                editField("firstname", scanner, resultSet);
                break;
            case 2:
                editField("lastname", scanner, resultSet);
                break;
            case 3:
                editField("fathername", scanner, resultSet);
                break;
            case 4:
                editField("dob", scanner, resultSet);
                break;
            case 5:
                editField("address", scanner, resultSet);
                break;
            case 6:
                editField("email", scanner, resultSet);
                break;
            case 7:
                editField("pan", scanner, resultSet);
                break;
            case 8:
                // Exit editing
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private void editField(String fieldName, Scanner scanner, ResultSet resultSet) throws SQLException {
        System.out.print("Enter new " + fieldName + ": ");
        String newValue = scanner.nextLine();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE bank_details SET " + fieldName + " = ? WHERE phone = ?";
            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                updateStatement.setString(1, newValue);
                updateStatement.setInt(2, resultSet.getInt("phone"));

                int rowsAffected = updateStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println(fieldName + " updated successfully.");
                    // Update the ResultSet with the new value
                    //resultSet.updateString(fieldName, newValue);
                    //displayUserDetails(resultSet);
                    // Return to the editing options
                    editUserDetails(scanner, resultSet);
                } else {
                    System.out.println("Error updating " + fieldName + ". Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validateUser(String username) {
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