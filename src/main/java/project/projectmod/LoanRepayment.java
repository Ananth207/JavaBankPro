package project.projectmod;
import java.util.*;
import java.sql.*;
import java.time.*;
import java.util.Date;
import java.time.LocalDate;
public class LoanRepayment {

    public String DB_URL;
    public String DB_USER;
    public String DB_PASSWORD;

    public LoanRepayment(String DB_URL, String DB_USER, String DB_PASSWORD) {
        this.DB_URL = DB_URL;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }


    public void LoanRepayMain(Scanner scanner, String username){
        //Scanner scanner = new Scanner(System.in);

        if (!isUsernameExists(username)) {
            System.out.println("Sorry, the provided username does not exist.");
            return;
        }

        try {
            // Establish database connection
            //Connection connection = DriverManager.getConnection(JDBC_URL);

            // Check if the user is eligible to repay the loan
            if (isEligibleToRepay(username)) {
                // Get the loan details for the user
                LoanDetails loanDetails = getLoanDetails(username);

                // Display the loan details
                System.out.println("Loan Details:");
                System.out.println("Loan Amount: " + loanDetails.getLoanAmount());
                System.out.println("Repayment Status: " + loanDetails.getRepaymentStatus());
                System.out.println("Deadline Date: " + loanDetails.getDeadlineDate());

                // Get the current date
                LocalDate currentDate = LocalDate.now();
                Date deadlineDate = loanDetails.getDeadlineDate();
                LocalDate localDeadlineDate = deadlineDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                boolean isOverdue = currentDate.isAfter(localDeadlineDate);


               /*  Date deadlineDate = loanDetails.getDeadlineDate();
                //Instant instant = deadlineDate.toInstant();
                //LocalDate localDeadlineDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                //LocalDate currentDate = LocalDate.now();
                //boolean isOverdue = currentDate.isAfter(localDeadlineDate);*/

                // Ask the user to enter the repayment amount
                System.out.print("Enter the repayment amount: ");
                double repaymentAmount = scanner.nextDouble();

                // Check if the user's balance is sufficient for partial repayment
                if (isBalanceSufficient(username, repaymentAmount)) {
                    // Perform partial repayment
                    performPartialRepayment(username, repaymentAmount);

                    // Update the loan amount pending
                    updateLoanAmountPending(username);

                    // Display the updated loan details
                    LoanDetails updatedLoanDetails = getLoanDetails(username);
                    System.out.println("Updated Loan Details:");
                    System.out.println("Loan Amount Pending: " + updatedLoanDetails.getLoanAmount());
                    System.out.println("Repayment Status: " + updatedLoanDetails.getRepaymentStatus());

                    // Update the repayment status based on the deadline
                    updateRepaymentStatus(username, isOverdue);

                    System.out.println("Loan repayment process completed.");
                } else {
                    System.out.println("Insufficient balance. Unable to repay the specified amount.");
                }
            } else {
                System.out.println("You are not eligible to repay the loan at this time.");
            }

            // Close the database connection
            //connection.close();
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

    private boolean isEligibleToRepay(String username) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the user is eligible to repay the loan
            String query = "SELECT COUNT(*) FROM LoanHistory WHERE username = ? AND repaymentOnTime IS NULL ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) >= 0;
                }
            }
        }
        return false;
    }

    public LoanDetails getLoanDetails(String username) throws SQLException {
        try (Connection connections = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get loan details for the user
            String query = "SELECT loanAmount, repaymentOnTime, deadlineDate FROM LoanHistory WHERE username = ?";
            try (PreparedStatement preparedStatement = connections.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double loanAmount = resultSet.getDouble("loanAmount");
                    boolean repaymentOnTime = resultSet.getBoolean("repaymentOnTime");
                    java.sql.Date deadlineDate = resultSet.getDate("deadlineDate");
                    return new LoanDetails(loanAmount, repaymentOnTime, deadlineDate);
                }
            }
        }
        throw new SQLException("Loan details not found for the user.");
    }

    public boolean isBalanceSufficient(String username, double requiredBalance) throws SQLException {
        try (Connection connections = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the user's balance is sufficient for repayment
            String query = "SELECT balance FROM bank_details WHERE username = ?";
            try (PreparedStatement preparedStatement = connections.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    return balance >= requiredBalance;
                }
            }
        }
        throw new SQLException("Balance information not found for the user.");
    }

    public void performPartialRepayment(String username, double repaymentAmount) throws SQLException {
        try (Connection connections = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Subtract the repayment amount from the user's balance
            String updateBalanceQuery = "UPDATE bank_details SET balance = balance - ? WHERE username = ?";
            try (PreparedStatement updateBalanceStatement = connections.prepareStatement(updateBalanceQuery)) {
                updateBalanceStatement.setDouble(1, repaymentAmount);
                updateBalanceStatement.setString(2, username);
                updateBalanceStatement.executeUpdate();
            }

            // Subtract the repayment amount from the loan amount pending
            String updateLoanAmountQuery = "UPDATE LoanHistory SET loanAmount = loanAmount - ? WHERE username = ?";
            try (PreparedStatement updateLoanAmountStatement = connections.prepareStatement(updateLoanAmountQuery)) {
                updateLoanAmountStatement.setDouble(1, repaymentAmount);
                updateLoanAmountStatement.setString(2, username);
                updateLoanAmountStatement.executeUpdate();
            }
        }
    }

    public void updateLoanAmountPending(String username) throws SQLException {
        try (Connection connections = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Update the loan amount pending to 0 if the total repayment is completed
            String updateLoanAmountQuery = "UPDATE LoanHistory SET loanAmount = 0 WHERE username = ?";
            try (PreparedStatement updateLoanAmountStatement = connections.prepareStatement(updateLoanAmountQuery)) {
                updateLoanAmountStatement.setString(1, username);
                updateLoanAmountStatement.executeUpdate();
            }
        }

    }

    public void updateRepaymentStatus(String username, boolean isOverdue) throws SQLException {
        try (Connection connections = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Update the repayment status based on the deadline
            String updateQuery = "UPDATE LoanHistory SET repaymentOnTime = ? WHERE username = ?";
            try (PreparedStatement updateStatement = connections.prepareStatement(updateQuery)) {
                updateStatement.setString(1, isOverdue ? "No" : "Yes");
                updateStatement.setString(2, username);
                updateStatement.executeUpdate();
            }
        }
    }

    private static class LoanDetails {
        private final double loanAmount;
        private final boolean repaymentStatus;
        private final java.sql.Date deadlineDate;

        public LoanDetails(double loanAmount, boolean repaymentStatus, java.sql.Date deadlineDate) {
            this.loanAmount = loanAmount;
            this.repaymentStatus = repaymentStatus;
            this.deadlineDate = deadlineDate;
        }

        public double getLoanAmount() {
            return loanAmount;
        }

        public boolean getRepaymentStatus() {
            return repaymentStatus;
        }

        public Date getDeadlineDate() {
            return deadlineDate;
        }
    }
}

