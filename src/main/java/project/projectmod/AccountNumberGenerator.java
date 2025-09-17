package project.projectmod;
import java.util.*;
import java.sql.*;
public class AccountNumberGenerator {

    public String DB_URL ;
    public String DB_USER ;
    public String DB_PASSWORD ;
    public AccountNumberGenerator(String DB_URL, String DB_USER, String DB_PASSWORD)
    {
        this.DB_URL=DB_URL;
        this.DB_USER=DB_USER;
        this.DB_PASSWORD=DB_PASSWORD;
    }

    public String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;

        do {
            // Generate a candidate account number
            StringBuilder sb = new StringBuilder();
            sb.append(random.nextInt(8) + 1); // Generates a random digit between 1 and 9 for the first digit
            for (int i = 1; i < 10; i++) {
                sb.append(random.nextInt(10)); // Generates a random digit between 0 and 9 for the remaining digits
            }
            accountNumber = sb.toString();
        } while (accountNumberExists(accountNumber)); // Retry if the generated account number already exists

        return accountNumber;
    }

    public boolean accountNumberExists(String accountNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM account_table WHERE account_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, accountNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if the account number already exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
