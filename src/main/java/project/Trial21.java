package project;

import java.util.*;

import project.projectmod.LoginSystem;
import project.projectmod.*;

public class Trial21
{
    public static void main(String[] args)
    {

        String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12665091";
        String DB_USER = "sql12665091";
        String DB_PASSWORD = "USCJhlW5K2";

        Scanner scanner = new Scanner(System.in);
        String loggeduser =null;
        int option=0;

        LoginSystem L = new LoginSystem(DB_URL, DB_USER, DB_PASSWORD);
        TransactionSystem T = new TransactionSystem(DB_URL, DB_USER, DB_PASSWORD);
        BalanceDisplay B = new BalanceDisplay(DB_URL, DB_USER, DB_PASSWORD);
        AccountDetails A = new AccountDetails(DB_URL, DB_USER, DB_PASSWORD);
        TransactionHistoryDisplay TH = new TransactionHistoryDisplay(DB_URL, DB_USER, DB_PASSWORD);
        LoanEligibilityChecker LC=new LoanEligibilityChecker(DB_URL, DB_USER, DB_PASSWORD);
        LoanRepayment LR=new LoanRepayment(DB_URL, DB_USER, DB_PASSWORD);

        while (true)
        {
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.print("Choose an option: ");
            option = scanner.nextInt();
            scanner.nextLine();
            if (option == 1)
            {

                loggeduser=L.performLogin(scanner);
                break;
            } else if (option == 2)
            {
                loggeduser=L.performRegistration(scanner);
                break;
            }
            else
            {
                System.out.println("Invalid option. Exiting.");
            }
        }
        while (true)
        {
            if (loggeduser != null)
            {
                System.out.println("1. Transaction");
                System.out.println("2. Balance");
                System.out.println("3. Account Details");
                System.out.println("4. Transaction History");
                System.out.println("5. Loan availing");
                System.out.println("6. Loan Repayment");
                System.out.print("Choose an option: ");
                option = scanner.nextInt();
                if(option ==1)
                {
                    T.performTransaction(scanner,loggeduser);
                }
                else if(option==2)
                {
                    B.displayBalance(scanner,loggeduser);
                }
                else if(option ==3)
                {
                    A.displayAccountDetails(scanner,loggeduser);
                }
                else if (option == 4)
                {
                    TH.displayTransactionHistory(loggeduser);
                }
                else if(option==5)
                {
                    LC.LoanCheckMain(scanner,loggeduser);

                }
                else if(option==6)
                {
                    LR.LoanRepayMain(scanner,loggeduser);
                }
                else
                {
                    System.out.println("Invalid option. Exiting.");
                    break;
                }
            }

        }
    }

}


