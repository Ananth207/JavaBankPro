package javabank_application;
import java.util.*;
import javabank_application.projectmod.*;


public class OnlineBankingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String loggedUser = null;
        int option;

        String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12665091";
        String DB_USER = "sql12665091";
        String DB_PASSWORD = "USCJhlW5K2";

        LoginSystem loginSystem = new LoginSystem(DB_URL,
                DB_USER, DB_PASSWORD);
        TransactionSystem transactionSystem = new TransactionSystem(DB_URL,
                DB_USER, DB_PASSWORD);
        BalanceDisplay balanceDisplay = new BalanceDisplay(DB_URL,
                DB_USER, DB_PASSWORD);
        AccountDetails accountDetails = new AccountDetails(DB_URL,
                DB_USER, DB_PASSWORD);
        TransactionHistoryDisplay transactionHistoryDisplay = new TransactionHistoryDisplay(DB_URL,
                DB_USER, DB_PASSWORD);
        LoanEligibilityChecker loanEligibilityChecker = new LoanEligibilityChecker(DB_URL,
                DB_USER, DB_PASSWORD);
        LoanRepayment loanRepayment = new LoanRepayment(DB_URL,
                DB_USER, DB_PASSWORD);

        boolean logger =true;

        while (logger) {
            printMainMenu();
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    loggedUser = loginSystem.performLogin(scanner);
                    break;
                case 2:
                    loggedUser = loginSystem.performRegistration(scanner);
                    break;
                case 3:
                    System.out.println("Exiting. Thank you for using our Online Banking System.");
                    System.exit(0);
                    logger = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1, 2, or 3.");
            }

            while (loggedUser != null && logger) {
                printUserMenu(loggedUser);
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1:
                        transactionSystem.performTransaction(scanner, loggedUser);
                        break;
                    case 2:
                        balanceDisplay.displayBalance(scanner, loggedUser);
                        break;
                    case 3:
                        accountDetails.displayAccountDetails(scanner, loggedUser);
                        break;
                    case 4:
                        transactionHistoryDisplay.displayTransactionHistory(loggedUser);
                        break;
                    case 5:
                        loanEligibilityChecker.LoanCheckMain(scanner, loggedUser);
                        break;
                    case 6:
                        loanRepayment.LoanRepayMain(scanner, loggedUser);
                        break;
                    case 7:
                        System.out.println("Logging out. Have a great day, " + loggedUser + "!");
                        loggedUser = null;
                        break;
                    case 8:
                        System.out.println("Exiting. Thank you for using our Online Banking System.");
                        System.exit(0);
                        logger = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose a number between 1 and 8.");
                }
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("**************************************************");
        System.out.println("*              Online Banking System              *");
        System.out.println("**************************************************");
        System.out.println("* 1. Login                                      *");
        System.out.println("* 2. Register                                   *");
        System.out.println("* 3. Exit                                       *");
        System.out.println("**************************************************");
        System.out.print("Choose an option: ");
    }

    private static void printUserMenu(String username) {
        System.out.println("**************************************************");
        System.out.println("*              Welcome, " + formatUsername(username) + "!               *");
        System.out.println("**************************************************");
        System.out.println("* 1. Transaction                                *");
        System.out.println("* 2. Balance                                    *");
        System.out.println("* 3. Account Details                            *");
        System.out.println("* 4. Transaction History                        *");
        System.out.println("* 5. Loan availing                              *");
        System.out.println("* 6. Loan Repayment                             *");
        System.out.println("* 7. Logout                                     *");
        System.out.println("* 8. Exit                                       *");
        System.out.println("**************************************************");
        System.out.print("Choose an option: ");
    }

    private static String formatUsername(String username) {
        return Character.toUpperCase(username.charAt(0)) + username.substring(1);
    }
}