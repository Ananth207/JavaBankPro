
# JavaBankPro

JavaBank Pro is a sophisticated financial management system designed to provide an all and encompassing solution for modern banking operations.
<br><br>This project addresses the critical 
need for a secure, efficient, and user-friendly platform that seamlessly integrates bank account management, loan processing, credit and debit transactions, and 
robust OTP messaging for enhanced security by leveraging the power of Java's Object Oriented Programming (OOP) principles coupled with user-friendly UI and cross-platform capabilities, JavaBank Pro offers a stable and scalable framework to handle a wide array of financial services.




## Installation and Execution

This project uses SpringBoot application for execution of OTP Module, based on JDK 21. Hence it is necessary to install and set the current dependency of the project as JDK 21 and the project must also contain Maven (helping to download dependencies, which refers to the libraries or JAR files). 

This could be done easily by directly opening the project zip file in a robust Java IDE such as IntelliJ.

Since the project involves using an online database connected via internet, this application requires online connection.

Thus, jdbc driver file must be downloaded and installed in the external libraries. This file can be found in the location              
..\BankLoginSystem\src\main\java\mysql-connector-j-8.2.0.jar 

The main segment to be executed is ..\BankLoginSystem\src\main\java\javabank_application\OnlineBankingSystem.java

..\BankLoginSystem\src\main\resources\application.properties can be used to change the sending email to other emails during otp verification. 



## About Code

The following Credentials could be used for login system:<br> **Username**: *Ananth*<br>
**Password**: *1234*

The following are the modules present in the project

AccountDetails,<br>AccountNumberGenerator,<br>
BalanceDisplay,<br>
LoanEligibilityChecker,<br>
LoanRepayment,<br>
LoginSystem,<br>
TransanctionHistory,<br>
TransanctionHistoryDisplay,<br>
TransanctionSystem

Once the main segment is executed, you would be asked to Login/Register, where both invloves OTP Checking using your email address (If the email isnt found in the inbox, please check through your spam emails). <br>
<br>After Logging/Registering, various functions such as Transanctions, Loan application can be selected through a menu driven application. Since the database is connected online, multiple systems can run the code at the same time and all the changes apply seamlessly.
<br>

Happy Banking !!!

## Developers

Abbhinav Elliah <br>
Ananth Narayanan P<br>
Bhuvan S<br>
_(SSN College of Engineering)_
