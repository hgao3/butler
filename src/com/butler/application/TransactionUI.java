package com.butler.application;

import java.text.NumberFormat;
import java.util.Scanner;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

public class TransactionUI {

	private Scanner scan = null;
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	private DataBase dataBase = new DataBase();
	private boolean isExitMenu = false;
	private boolean isExitSubMenu = false;

	public void manageTransactions() {
		isExitMenu = false;
		isExitSubMenu = false;
		scan = new Scanner(System.in);

		while (!isExitMenu) {
			viewAllTransaction();
			int choice = ButlerIOUtils.getInputWithIntRange(scan, 0, 3);
			switch (choice) {
			case 0:
				isExitMenu = true;
				break;
			case 1:
				createNewTransaction();
				break;
			case 2:
				isExitSubMenu = false;
				editTransactionById();
				break;
			case 3:
				isExitSubMenu = false;
				deleteTransactionById();
				break;
			default:
				System.out.println("That wasn't a choice.");
				break;
			}
		}
	}

	public void viewAllTransaction() {
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Will your like to:"));
		System.out.println(String.format("| %-110s  |", "0.   Go back to main page"));
		System.out.println(String.format("| %-110s  |", "1.   Create a new transaction"));
		System.out.println(String.format("| %-110s  |", "2.   Edit a existing transaction"));
		System.out.println(String.format("| %-110s  |", "3.   Delete a existing transaction"));
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Here is your transaction view"));
		System.out.println(
				"===================================================================================================================");
		transactionDao.printAll();
		System.out.println(
				"===================================================================================================================");
	}
	
	public void createNewTransaction() {
		System.out.println(
				"===================================================================================================================");
		System.out.println(String.format("| %-110s  |", "Please enter:"));
		System.out.println(String.format("| %-110s  |",
				"  The date, amount, withdraw from and deposit to accounts for this transaction."));
		System.out.println(String.format("| %-110s  |",
				"Rule 1: You need to separate by comma, like: 05/18/2019,60.85,2,5"));
		System.out.println(String.format("| %-110s  |",
				"You can also enter another transaction or enter 0 to go back to account main page."));
		System.out.println("| Here is the available accounts for the transaction:                             |");
		System.out.println(
				"===================================================================================================================");
		accountDao.printAll();
		System.out.println(
				"===================================================================================================================");

		// scan user input for account, and accountDao.save(acc)
		isExitSubMenu = dataBase.scanTransactionWithIO(scan, true, -1);
	}

	public void editTransactionById() {
		while (!isExitSubMenu) {
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |",
					"Please enter the ID number for the transaction that you are trying to edit."));
			System.out.println(
					String.format("| %-110s  |", "You can also enter -1 to go back to transaction main page."));
			System.out.println(
					"===================================================================================================================");
			int id = ButlerIOUtils.getInputWithIntRange(scan, -1, transactionDao.transactionsCounter);
			if (id == -1) {
				isExitSubMenu = true;
			} else {
				Transaction oldTrans = transactionDao.get(id);
				System.out.println(
						"===================================================================================================================");
				System.out.println(String.format("| %-110s  |", oldTrans));
				System.out.println(String.format("| User friendly String | %-110s |", oldTrans.toDataString()));
				System.out.println(String.format("| %-110s  |", "Please enter the edited transaction: "));
				System.out.println(String.format("| %-110s  |",
						"  The date, amount, withdraw from and deposit to accounts for this edit transaction."));
				System.out.println(String.format("| %-110s  |",
						"Rule 1: You need to separate by comma, like: 05/18/2019,60.85,2,5"));
				System.out.println(
						"===================================================================================================================");

				// scan user input for account, and accountDao.save(acc)
				isExitSubMenu = dataBase.scanTransactionWithIO(scan, true, id);
			}
		}
	}

	public void deleteTransactionById() {
		while (!isExitSubMenu) {
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |",
					"Please enter the ID number for the transaction that you are trying to delete."));
			System.out.println(
					String.format("| %-110s  |", "You can also enter -1 to go back to transaction main page."));
			System.out.println(
					"===================================================================================================================");
			int id = ButlerIOUtils.getInputWithIntRange(scan, -1, transactionDao.transactionsCounter);
			if (id == -1) {
				isExitSubMenu = true;
			} else {
				
				// return the money
				Transaction oldTrans = transactionDao.get(id);
				accountDao.accountTransfer(oldTrans.getDepositTo(), oldTrans.getWithdrawFrom(), oldTrans.getAmount());
				
				transactionDao.delete(id);
				System.out.println("===================================================================================================================");
				System.out.println(String.format("| %-110s  |", "Transaction is deleted"));
				System.out.println("===================================================================================================================");	
			
				isExitSubMenu = true;
			}
		}
		

	}
}
