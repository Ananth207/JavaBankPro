package project.projectmod;
import java.util.*;
import java.sql.*;
import java.time.*;
public class LoanEligibilityChecker {

    //private static final String JDBC_URL = "jdbc:sqlite:loan_history.db";
    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public LoanEligibilityChecker(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }
    public void LoanCheckMain(Scanner scanner, String username)
    {

        if (!isUsernameExists(username)) {
            System.out.println("Sorry, the provided username does not exist.");
            return;
        }
        System.out.print("Enter the requested loan amount: ");
        double requestedLoanAmount = scanner.nextDouble();

        try {

            // Check eligibility based on loan history
            boolean isEligible = checkLoanEligibility( username, requestedLoanAmount);

            // Display the result
            if (isEligible) {
                System.out.println("Congratulations! You are eligible for the loan.");

                // Check for pending loans before proceeding with the loan request
                boolean hasPendingLoans =hasPendingLoans( username);
                if (hasPendingLoans) {
                    System.out.println("Sorry, your loan request is declined due to pending loans.");
                } else {
                    // Ask for confirmation to avail the loan
                    System.out.print("Do you want to avail the loan? (yes/no): ");
                    Scanner scanner1=new Scanner(System.in);
                    String confirmation = scanner1.nextLine().toLowerCase();

                    if ("yes".equals(confirmation)) {
                        // Add a new record to the LoanHistory table
                        addLoanHistoryRecord( username, requestedLoanAmount, true);

                        // Update the bank balance in the bank_details table
                        updateBankBalance( username, requestedLoanAmount);

                        System.out.println("Loan request successfully processed. Bank balance updated.");
                    } else {
                        System.out.println("Loan request declined.");
                    }
                }
            } else {
                System.out.println("Sorry, you are not eligible for the loan.");
            }

            // Close the database connection
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public boolean isUsernameExists(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM bank_details WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean checkLoanEligibility( String username, double requestedLoanAmount) throws SQLException {
        // Check the number of loan histories for the person
        int loanHistoryCount = getLoanHistoryCount(username);
        if(requestedLoanAmount>100000)
        {
            return false;
        }
        if (loanHistoryCount < 5) {
            return true; // Eligible if the number of loan histories is less than 5
        } else {
            // Check repayment percentage for those with more than 5 loan histories
            int nonRepaymentCount = getNonRepaymentCount(username);

            double nonRepaymentPercentage = (double) nonRepaymentCount / loanHistoryCount * 100;

            if (nonRepaymentPercentage >= 40) {
                return false; // Not eligible if non-repayment percentage is greater than or equal to 40%
            } else if (nonRepaymentPercentage >= 20 && requestedLoanAmount > 50000) {
                return false; // Not eligible for a loan greater than 50000 if non-repayment percentage is greater than or equal to 20%
            } else {
                return true; // Eligible in all other cases
            }
        }
    }

    public int getLoanHistoryCount( String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM LoanHistory WHERE username = ?";
            try (PreparedStatement preparedStatement =connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    public int getNonRepaymentCount(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM LoanHistory WHERE username = ? AND repaymentOnTime = 'No'";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean hasPendingLoans(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT COUNT(*) FROM LoanHistory WHERE username = ? AND repaymentOnTime IS NULL";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public void addLoanHistoryRecord(String username, double loanAmount, boolean repaymentOnTime) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO LoanHistory (username, loanAmount, loanAvailedDate, deadlineDate) VALUES ( ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setDouble(2, loanAmount);
                //preparedStatement.setBoolean(3, repaymentOnTime);
                preparedStatement.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Assuming loan availed today
                preparedStatement.setDate(4, calculateDeadlineDate(loanAmount)); // Calculate deadline date
                preparedStatement.executeUpdate();
            }
        }
    }

    public void updateBankBalance(String username, double loanAmount) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE bank_details SET balance = balance + ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDouble(1, loanAmount);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        }
    }

    public java.sql.Date calculateDeadlineDate(double loanAmount) {
        // Calculate deadline date based on loan amount
        LocalDate loanAvailedDate = LocalDate.now();
        if (loanAmount < 50000) {
            return java.sql.Date.valueOf(loanAvailedDate.plusDays(100));
        } else {
            return java.sql.Date.valueOf(loanAvailedDate.plusDays(150));
        }
    }
}
