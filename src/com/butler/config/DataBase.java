package com.butler.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;

public class DataBase {
	public final static String PRELOAD_ACCOUNT_DATA = "accountData.txt";
	public final static String PRELOAD_TRANSACTION_DATA = "transactionData.txt";
	public final static String ACCOUNT_REPORT = "accountReport.txt";

	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();

	private Scanner scan;

	public void printReports() {
		System.out.println(ACCOUNT_REPORT + "...being built from scratch.");
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(ACCOUNT_REPORT));
			printLine(writer, 115);
			writer.println(String.format("| %-110s  |", "Here is your Account Summary"));
			printLine(writer, 115);
			for (Map.Entry<Integer, Account> entry : AccountDao.accounts.entrySet()) {
				writer.println(String.format("| ID: %-5d %s", entry.getKey(), entry.getValue()));
			}
			printLine(writer, 145);
			writer.println(String.format("| %-140s  |", "Here is your Transaction View"));
			printLine(writer, 145);

			for (Map.Entry<Integer, Transaction> entry : TransactionDao.transactions.entrySet()) {
				writer.println(String.format("| ID: %-5d %s", entry.getKey(), entry.getValue()));
			}
			printLine(writer, 145);
			writer.close();
			System.out.println("All Account Summary and Transactions are saved to " + ACCOUNT_REPORT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void preloadAccount() throws FileNotFoundException {
		System.out.println("Loading account data from " + PRELOAD_ACCOUNT_DATA + "...");
		String line;
		String[] lineVector;
		Account acc = null;
		scan = new Scanner(new File(PRELOAD_ACCOUNT_DATA));
		while (scan.hasNext()) {
			if (scan.hasNextLine()) {
				try {
					line = scan.nextLine();
					// consume the \n character from empty line
					if (line.isEmpty()) {
						line = scan.nextLine(); // read income,Wallet,Hugh,100,Cash
					}
					// separate all values by comma
					lineVector = line.split(",");
					if (lineVector.length == 5) {
						// parsing the values to string and double
						String accountType = lineVector[0];
						String name = lineVector[1];
						String owner = lineVector[2];
						double amount = Double.parseDouble(lineVector[3]);
						String category = lineVector[4];

						if (accountType.equalsIgnoreCase("income")) {
							acc = new IncomeAccount(name, owner, amount, category);
						} else if (accountType.equalsIgnoreCase("expense")) {
							acc = new ExpenseAccount(name, owner, amount, category);
						}
						// save account
						accountDao.save(acc);
					}
				} catch (Exception e) {
					System.out.println("Bad record or read in data.");
				}
			}
		}
	}

	public void preloadTransaction() throws FileNotFoundException {
		System.out.println("Loading transaction data from " + PRELOAD_TRANSACTION_DATA + "...");
		String line;
		String[] lineVector;
		Transaction trans = null;
		scan = new Scanner(new File(PRELOAD_TRANSACTION_DATA));
		while (scan.hasNext()) {
			if (scan.hasNextLine()) {
				try {
					line = scan.nextLine();
					// consume the \n character from empty line
					if (line.isEmpty()) {
						line = scan.nextLine(); // read 05/18/2019,Hugh,60.85,2,5
					}
					// separate all values by comma
					lineVector = line.split(",");
					if (lineVector.length == 5) {
						// parsing the values to string and double
						String dateString = lineVector[0];
						String name = lineVector[1];
						double amount = Double.parseDouble(lineVector[2]);
						int accountFromId = Integer.parseInt(lineVector[3]);
						int accountToId = Integer.parseInt(lineVector[4]);
						// get account by id
						Account withdrawFrom = accountDao.get(accountFromId);
						Account depositTo = accountDao.get(accountToId);
						// transfer money between accounts
						accountDao.accountTransfer(withdrawFrom, depositTo, amount);
						// save transaction
						trans = new Transaction(dateString, name, withdrawFrom, depositTo, amount);
						transactionDao.save(trans);
					}
				} catch (Exception e) {
					System.out.println("Bad record or read in data.");
				}
			}
		}
	}

	public void printLine(PrintWriter writer, int size) {
		for (int i = 0; i < size; i++) {
			writer.print("=");
		}
		writer.println();
	}
}
