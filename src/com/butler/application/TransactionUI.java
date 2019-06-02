package com.butler.application;

import java.text.NumberFormat;
import java.util.Scanner;

import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

public class TransactionUI {

	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	
	private NumberFormat formatter = NumberFormat.getCurrencyInstance();
	private boolean isExitSubMenu = false;
	
	public void manageTransactions(Scanner scan) {
		System.out.println("=================================================================================================================================================");
		System.out.println("| Sorry, only view function is available in this version.                                                                                       |");
		System.out.println("| Here is your transactions view                                                                                                                |");
		System.out.println("=================================================================================================================================================");
		this.transactionDao.printAll();
		System.out.println("=================================================================================================================================================");
		System.out.println("| You can enter 0 to go back to main page.                                        |");
		System.out.println("===================================================================================");
		ButlerIOUtils.getInputWithIntRange(scan, 0, 0);
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
	
	public void submitTransaction(Scanner scan) throws ClassInstantiationException {
		isExitSubMenu = false;
		while (!isExitSubMenu) {
			printMenuForSubmitTransaction();
			getInputForSubmitTransaction(scan);
		}
	}
	
	public void getInputForSubmitTransaction(Scanner scan) throws ClassInstantiationException {
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
						
						trans = new Transaction(date, Main.LOGIN_USER, accountFrom, accountTo, amount);
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
}
