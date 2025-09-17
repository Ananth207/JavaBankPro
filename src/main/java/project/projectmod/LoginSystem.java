package project.projectmod;
import com.ananth.BankLoginSystem.BankLoginSystemApplication;

import java.util.*;
import java.sql.*;

public class LoginSystem
{
    public String DB_URL ;
    public String DB_USER ;
    public String DB_PASSWORD ;
    public LoginSystem(String DB_URL, String DB_USER, String DB_PASSWORD)
    {
        this.DB_URL=DB_URL;
        this.DB_USER=DB_USER;
        this.DB_PASSWORD=DB_PASSWORD;
    }
    public String performLogin(Scanner scanner) {
        System.out.print("Enter Your Username: ");
        String username = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your Password: ");
        String password = scanner.nextLine();

        BankLoginSystemApplication emailer = new BankLoginSystemApplication();
        //int created_otp = emailer.return_otp();
        //int created_otp;



        emailer.send_email();
        System.out.println("Enter otp: ");
        int otp = scanner.nextInt();
        System.out.println(emailer.otp_created+"    "+otp);



        if (authenticateUser(username, password)&& otp == emailer.otp_created) {
            System.out.println("Login successful. Welcome to the online banking system!");
            // Add the logic for the banking operations here
            return username;
        } else {
            System.out.println("Invalid username or password. Login failed.");
            return null;
        }
    }

    public String performRegistration(Scanner scanner) {
        System.out.print("Create new username: ");
        String newUsername = scanner.next();
        scanner.nextLine();
        System.out.print("Create new password: ");
        String newPassword = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your First Name: ");
        String newFirstName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your Last Name: ");
        String newLastName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your Father Name: ");
        String newFatherName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your DOB (YYYY-MM-DD): ");
        String newDOB = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your phone number: ");
        long newPhoneno =scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter Your Email ID: ");
        String newEmailid = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your PAN number: ");
        String newPAN = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Your Address: ");
        String newAddress = scanner.next();

        AccountNumberGenerator ACC = new AccountNumberGenerator(DB_URL, DB_USER, DB_PASSWORD);
        String accountNumber = ACC.generateUniqueAccountNumber();
        long newacc_no = Long.parseLong(accountNumber);

        if (registerUser(newUsername, newPassword, newFirstName, newLastName, newFatherName, newDOB, newAddress, newPhoneno, newEmailid, newPAN,newacc_no)) {
            System.out.println("Registration successful. You can now log in.");
            return newUsername;
        } else {
            System.out.println("Registration failed. The username may already be taken.");
            return null;
        }
    }

    public boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String query = "SELECT * FROM bank_details WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If there is a match, the user is authenticated
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(String username, String password,String firstname,String lastname,String fathername,String dob,String address,long phoneno, String emailid,String pan,long acc_no) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String checkQuery = "SELECT * FROM bank_details WHERE username = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, username);

                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return false; // Username already exists
                    }
                }
            }

            String insertQuery = "INSERT INTO bank_details ( username,  password, firstname, lastname, fathername, dob, address, phone,  email, pan,acc_no) VALUES (?, ?,?,?,?,?, ?,?,?,?,?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, firstname);
                insertStatement.setString(4, lastname);
                insertStatement.setString(5, fathername);
                insertStatement.setString(6, dob);
                insertStatement.setString(7, address);
                insertStatement.setLong(8, phoneno);
                insertStatement.setString(9, emailid);
                insertStatement.setString(10, pan);
                insertStatement.setLong(11, acc_no);

                int rowsAffected = insertStatement.executeUpdate();
                return rowsAffected > 0; // Registration successful if rows affected > 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
}