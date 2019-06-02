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

	private Scanner scan = null;
	


	private DataBase database = new DataBase();
	private AccountUI accountUI = new AccountUI();
	private TransactionUI transactionUI = new TransactionUI();
	public final static String LOGIN_USER = "Hugh";

	public static void main(String[] args) throws Exception {

		// do it this way so I don't need to make all method static
		Main app = new Main();
		app.runApp();
	}
	
	public void runApp() throws Exception {

		scan = new Scanner(System.in);

		setUpData();
		printHeader();

		while (!isExit) {
			printMainPage();
			int choice = ButlerIOUtils.getInputWithIntRange(scan, 1, 5);
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
		System.out.println("| 4.	Save report (Limit)                                                       |");
		System.out.println("| 5.	Exit                                                                      |");
		System.out.println("===================================================================================");
	}

	public void saveReport() {
		database.printReports();
		System.out.println("===================================================================================");
		System.out.println("| We are redirect you back to main page.                                          |");
		System.out.println("===================================================================================");
	}

	private void performMenuAction(int choice) throws ClassInstantiationException {
		switch (choice) {
		case 1:
			this.transactionUI.submitTransaction(scan);
			break;
		case 2:
			this.transactionUI.manageTransactions(scan);
			break;
		case 3:
			this.accountUI.manageAccounts(scan);
			break;
		case 4:
			this.saveReport();
			break;
		case 5:
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
