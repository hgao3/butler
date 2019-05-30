package com.butler.application;

import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

public class Main {

	private boolean isExit = false;
	private boolean isExitSubMenu = false;
	private Scanner scan = null;
	private NumberFormat formatter = null;

	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	private DataBase database = new DataBase();
	private final static String LOGIN_USER = "Hugh";

	public static void main(String[] args) throws Exception {

		// do it this way so I don't need to make all method static
		Main app = new Main();
		app.runApp();
	}
	
	public void runApp() throws Exception {
		formatter = NumberFormat.getCurrencyInstance();
		scan = new Scanner(System.in);

		setUpData();
		printHeader();

		while (!isExit) {
			printMainPage();
			int choice = getInputWithIntRange(1, 6);
			performMenuAction(choice);
		}
	}

	private void setUpData() throws Exception {
		database.preloadAccount();
		database.preloadTransaction();
	}

	public void printHeader() {
		System.out.println("===================================================================================");
		System.out.println("| Welcome, your personal butler at your services                                  |");
	}

	public void printMainPage() {
		System.out.println("===================================================================================");
		System.out.println("| Will your like to:                                                              |");
		System.out.println("| 1.	Submit a transaction (Limit)                                              |");
		System.out.println("| 2.	Manage transactions (Limit)                                               |");
		System.out.println("| 3.	Manage accounts (Limit)                                                   |");
		System.out.println("| 4.	Manage account categories (N/A)                                           |");
		System.out.println("| 5.	Save report (Limit)                                                       |");
		System.out.println("| 6.	Exit                                                                      |");
		System.out.println("===================================================================================");
	}

	public void printMenuForSubmitTransaction() {
		System.out.println("===================================================================================");
		System.out.println("| Please enter:                                                                   |");
		System.out.println("|   The date, amount, withdraw from and deposit to accounts for this transaction. |");
		System.out.println("| Rule 1: You need to separate by comma, like: 05/18/2019,60.85,2,5               |");
		System.out.println("| You can also enter another transaction or enter 0 to go back to main page.      |");
		System.out.println("| Here is the available accounts for the transaction:                             |");
		System.out.println("===================================================================================================================");
		accountDao.printAll();
		System.out.println("===================================================================================================================");
	}
	
	public void getInputForSubmitTransaction() throws ClassInstantiationException {
		String line;
		String[] lineVector;
		Account accountFrom = null;
		Account accountTo = null;
		Transaction trans = null;
		String date = null;
		double amount = 0;
		int accountFromId = -1;
		int accountToId = -1;
		int choice;
		
		while (scan.hasNext()) {
			if (scan.hasNextLine()) {
				line = scan.nextLine();

				// consume the \n character from pre-enter
				if (line.isEmpty()) {
					line = scan.nextLine(); // read 05/18/2019,60.85,wallet,pizza hut
				}

				// separate all values by comma
				lineVector = line.split(",");

				// if user only enter 0 to go back to main page
				if (lineVector.length == 1) {
					try {
					choice = Integer.parseInt(lineVector[0]);
					} catch (NumberFormatException | NullPointerException e ) {
						System.out.println("We can't parse what you enter [" + line + "]. Please try again or enter 0 to go  back to main page. ");
						continue;
					}
					if (choice == 0) {
						isExitSubMenu = true;
						return;
					} else {
						System.out.println("You have to enter 0 or a transcation");
					}
					// if user enter a transaction with 3 comma, [string,double,string,string]
				} else if (lineVector.length == 4) {
					try {
					// parsing the values to string and double
					date = lineVector[0];
					amount = Double.parseDouble(lineVector[1]);
					accountFromId = Integer.parseInt(lineVector[2]);
					accountToId = Integer.parseInt(lineVector[3]);
					} catch (NumberFormatException | NullPointerException e ) {
						System.out.println("We can't parse what you enter [" + line + "]. Please try again or enter 0 to go  back to main page. ");
						continue;
					}
		
					accountFrom = accountDao.get(accountFromId);
					accountTo = accountDao.get(accountToId);
					
					if (accountFrom != null && accountTo != null) {
						accountDao.accountTransfer(accountFrom, accountTo, amount);
						
						trans = new Transaction(date, LOGIN_USER, accountFrom, accountTo, amount);
						transactionDao.save(trans);
	
						System.out.println("A transaction of " + formatter.format(amount) + " is withdraw from "
								+ accountFrom.getName() + " and deposit to " + accountTo.getName() + " at " + date
								+ ".");
						System.out.println( 
								"| You new balance for both account are:                                                               |");
						System.out.println(accountFrom);
						System.out.println(accountTo);
						System.out.println(
								"===================================================================================");
						System.out.println(
								"| You can enter another transaction or enter 0 to go back to main page.           |");
						System.out.println(
								"===================================================================================");
					} else {
						System.out.println(
								"====================================================================================");
						System.out.println(
								"| Invalid Entry, Please check if you mistype account name or not follows the rules.|");
						System.out.println(
								"====================================================================================");
					}
				}
			}
		}
	}

	public void submitTransaction() throws ClassInstantiationException {
		isExitSubMenu = false;
		while (!isExitSubMenu) {
			printMenuForSubmitTransaction();
			getInputForSubmitTransaction();
		}
	}

	public void manageTransactions() {
		System.out.println("=================================================================================================================================================");
		System.out.println("| Sorry, only view function is available in this version.                                                                                       |");
		System.out.println("| Here is your transactions view                                                                                                                |");
		System.out.println("=================================================================================================================================================");
		this.transactionDao.printAll();
		System.out.println("=================================================================================================================================================");
		System.out.println("| You can enter 0 to go back to main page.                                        |");
		System.out.println("===================================================================================");
		getInputWithIntRange(0, 0);
	}

	public void manageAccounts() {
		System.out.println("===================================================================================================================");
		System.out.println("| Sorry, only view function is available in this version.                                                         |");
		System.out.println("| Here is your accounts view                                                                                      |");
		System.out.println("===================================================================================================================");
		accountDao.printAll();
		System.out.println("===================================================================================================================");
		System.out.println("| You can enter 0 to go back to main page.                                        |");
		System.out.println("===================================================================================");
		getInputWithIntRange(0, 0);
	}

	public void manageAccountCategories() {
		sorry();
	}

	public void saveReport() {
		database.printReports();
		System.out.println("===================================================================================");
		System.out.println("| We are redirect you back to main page.                                          |");
		System.out.println("===================================================================================");
	}

	private int getInputWithIntRange(int min, int max) {
		while (scan.hasNext()) {
			if (scan.hasNextInt()) {
				int value = scan.nextInt();
				// get int input from mix to max
				if (value >= min && value <= max) {
					return value;
				} else {
					System.out.println("You have to enter within a range.");
				}
			} else {
				System.out.println("You have to enter a int.");
				scan.next();
			}
		}
		return -1;
	}

	private void performMenuAction(int choice) throws ClassInstantiationException {
		switch (choice) {
		case 1:
			this.submitTransaction();
			break;
		case 2:
			this.manageTransactions();
			break;
		case 3:
			this.manageAccounts();
			break;
		case 4:
			this.manageAccountCategories();
			break;
		case 5:
			this.saveReport();
			break;
		case 6:
			System.out.println("Good bye!");
			isExit = true;
			break;
		default:
			System.out.println("That wasn't a choice.");
			break;
		}
	}

	private void sorry() {
		System.out.println("===================================================================================");
		System.out.println("| Sorry, the function is not available in this version.                           |");
		System.out.println("===================================================================================");
	}
}
