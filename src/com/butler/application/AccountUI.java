package com.butler.application;

import java.util.Scanner;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

public class AccountUI {
	private Scanner scan = null;
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	private DataBase dataBase = new DataBase();
	private boolean isExitMenu = false;
	private boolean isExitSubMenu = false;

	public void manageAccounts() {
		isExitMenu = false;
		isExitSubMenu = false;
		scan = new Scanner(System.in);

		while (!isExitMenu) {
			viewAllAccount();
			int choice = ButlerIOUtils.getInputWithIntRange(scan, 0, 3);
			switch (choice) {
			case 0:
				isExitMenu = true;
				break;
			case 1:
				createNewAccount();
				break;
			case 2:
				isExitSubMenu = false;
				editAccountById();
				break;
			case 3:
				isExitSubMenu = false;
				deleteAccountById();
				break;
			default:
				System.out.println("That wasn't a choice.");
				break;
			}
		}
	}

	public void viewAllAccount() {
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Will your like to:"));
		System.out.println(String.format("| %-110s  |", "0.   Go back to main page"));
		System.out.println(String.format("| %-110s  |", "1.   Create a new account"));
		System.out.println(String.format("| %-110s  |", "2.   Edit a existing account"));
		System.out.println(String.format("| %-110s  |", "3.   Delete a existing account"));
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Here is your accounts view"));
		System.out.println(
				"===================================================================================================================");
		accountDao.printAll();
		System.out.println(
				"===================================================================================================================");
	}

	public void createNewAccount() {
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Please enter:"));
		System.out.println(String.format("| %-110s  |",
				"  The account type (income or expense), name, amount, and category for this new account."));
		System.out.println(String.format("| %-110s  |",
				"Rule 1: You need to separate by comma, like: income,Citi Bank Checking,3000,Checking Account"));
		System.out.println(String.format("| %-110s  |",
				"You can also enter another transaction or enter 0 to go back to account main page."));
		System.out.println(
				"===================================================================================================================");

		// scan user input for account, and accountDao.save(acc)
		isExitSubMenu = dataBase.scanAccountWithIO(scan, true, -1);
	}

	public void editAccountById() {
		while (!isExitSubMenu) {
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |",
					"Please enter the ID number for the account that you are trying to edit."));
			System.out.println(
					String.format("| %-110s  |", "You can also enter -1 to go back to account main page."));
			System.out.println(
					"===================================================================================================================");
			int id = ButlerIOUtils.getInputWithIntRange(scan, -1, accountDao.accountsCounter);
			if (id == -1) {
				isExitSubMenu = true;
			} else {
				Account oldAcc = accountDao.get(id);
				System.out.println(
						"===================================================================================================================");
				System.out.println(String.format("| %-110s  |", oldAcc));
				System.out.println(String.format("| User friendly String | %-110s |", oldAcc.toDataString()));
				System.out.println(String.format("| %-110s  |", "Please enter the edited account: "));
				System.out.println(String.format("| %-110s  |",
						"  The account type (income or expense), name, amount, and category for this edit account."));
				System.out.println(String.format("| %-110s  |",
						"Rule 1: You need to separate by comma, like: income,Citi Bank Checking,3000,Checking Account"));
				System.out.println(
						"===================================================================================================================");

				// scan user input for account, and accountDao.save(acc)
				isExitSubMenu = dataBase.scanAccountWithIO(scan, true, id);
			}
		}
	}

	public void deleteAccountById() {
		while (!isExitSubMenu) {
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |",
					"Please enter the ID number for the account that you are trying to delete."));
			System.out.println(
					String.format("| %-110s  |", "You can also enter -1 to go back to account main page."));
			System.out.println(
					"===================================================================================================================");
			int id = ButlerIOUtils.getInputWithIntRange(scan, -1, accountDao.accountsCounter);
			if (id == -1) {
				isExitSubMenu = true;
			} else {
				accountDao.delete(id);
				System.out.println("===================================================================================================================");
				System.out.println(String.format("| %-110s  |", "Account is deleted"));
				System.out.println("===================================================================================================================");	
			
				isExitSubMenu = true;
			}
		}
	}
}
