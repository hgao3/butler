package com.butler.config;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.butler.application.Main;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;

public class DataBase {
	public final static String PRELOAD_ACCOUNT_DATA_TXT = "accountData.txt";
	public final static String PRELOAD_TRANSACTION_DATA_TXT = "transactionData.txt";
	public final static String ACCOUNT_REPORT = "accountReport.txt";

	public final static String PRELOAD_ACCOUNT_DATA_DAT = "accountData.dat";
	public final static String PRELOAD_TRANSACTION_DATA_DAT = "transactionData.dat";

	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	private NumberFormat formatter = NumberFormat.getCurrencyInstance();

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

	public void printReportsWithAccountFilter(Account acc) {
		System.out.println(ACCOUNT_REPORT + "...being built from scratch.");
		PrintWriter writer;
		try {
			writer = new PrintWriter(new File(ACCOUNT_REPORT));
			printLine(writer, 115);
			writer.println(String.format("| %-110s  |", "Here is your Account Summary"));
			printLine(writer, 115);
			// print filtered account
			writer.println(String.format("| ID: %-5d %s", acc.getId(), acc));
			printLine(writer, 145);
			writer.println(String.format("| %-140s  |", "Here is your Transaction View"));
			printLine(writer, 145);

			// print filtered transactions
			transactionDao.getAll().stream()
					.filter(t -> (t.getWithdrawFrom().getId()==acc.getId() || t.getDepositTo().getId()==acc.getId()))
					.forEach(t -> writer.println(String.format("| ID: %-5d %s", t.getId(), t)));

			printLine(writer, 145);
			writer.close();
			System.out.println(
					"All Account Summary and Transactions related to this account are saved to " + ACCOUNT_REPORT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void preloadAccount() throws FileNotFoundException {
		System.out.println("Loading account data from " + PRELOAD_ACCOUNT_DATA_TXT + "...");
		scan = new Scanner(new File(PRELOAD_ACCOUNT_DATA_TXT));
		scanAccountWithIO(scan, false, -1);
	}

	/**
	 * This method had used by two class. callFromUI: true, calling from UI class.
	 * It will print the scan account and return true when it scan "0" callFromUI:
	 * false, calling from DataBase. It will keep scan accounts from text file.
	 * updateId: -1 mean no update, >0 mean the account id that we want to update.
	 * 
	 * @param scan
	 * @param callFromUI
	 * @param updateId
	 * @return
	 */
	public boolean scanAccountWithIO(Scanner scan, boolean callFromUI, int updateId) {
		String line;
		String[] lineVector;
		Account acc = null;
		int choice;
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

					if (callFromUI) {
						// if user only enter 0 to go back to main page
						if (lineVector.length == 1) {
							try {
								choice = Integer.parseInt(lineVector[0]);
							} catch (NumberFormatException | NullPointerException e) {
								System.out.println("We can't parse what you enter [" + line
										+ "]. Please try again or enter 0 to go  back to main page. ");
								continue;
							}
							if (updateId > -1) {
								if (choice == -1) {
									// return true to flag exit.
									return true;
								}
								System.out.println("You have to enter -1 or an account");
							} else {
								if (choice == 0) {
									// return true to flag exit.
									return true;
								}
								System.out.println("You have to enter 0 or an account");
							}

						} else if (lineVector.length == 4) { // if user enter a transaction with 3 comma,
																// [string,string,double,string]
							// parsing the values to string and double
							String accountType = lineVector[0];
							String name = lineVector[1];
							double amount = Double.parseDouble(lineVector[2]);
							String category = lineVector[3];

							if (accountType.equalsIgnoreCase("income")) {
								acc = new IncomeAccount(name, Main.LOGIN_USER, amount, category);
							} else if (accountType.equalsIgnoreCase("expense")) {
								acc = new ExpenseAccount(name, Main.LOGIN_USER, amount, category);
							}
							if (updateId > -1) {
								// save account
								accountDao.update(updateId, acc);
							} else {
								// save account
								accountDao.save(acc);
							}

							System.out.println(
									"===================================================================================================================");
							System.out.println(String.format("| %-110s  |", acc));
							System.out.println(
									"===================================================================================================================");

						}
					} else if (lineVector.length == 5) { // if user enter a transaction with 4 comma,
															// [string,string,string,double,string]
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
		// default to false
		return false;
	}

	public void preloadTransaction() throws FileNotFoundException {
		System.out.println("Loading transaction data from " + PRELOAD_TRANSACTION_DATA_TXT + "...");
		scan = new Scanner(new File(PRELOAD_TRANSACTION_DATA_TXT));
		scanTransactionWithIO(scan, false, -1);

	}

	/**
	 * This method had used by two class. callFromUI: true, calling from UI class.
	 * It will print the scan transaction and return true when it scan "0"
	 * callFromUI: false, calling from DataBase. It will keep scan transaction from
	 * text file. updateId: -1 mean no update, >0 mean the transaction id that we
	 * want to update.
	 * 
	 * @param scan
	 * @param callFromUI
	 * @param updateId
	 * @return
	 */
	public boolean scanTransactionWithIO(Scanner scan, boolean callFromUI, int updateId) {
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
				try {
					line = scan.nextLine();
					// consume the \n character from empty line
					if (line.isEmpty()) {
						line = scan.nextLine(); // read 05/18/2019,Hugh,60.85,2,5
					}
					// separate all values by comma
					lineVector = line.split(",");

					if (callFromUI) {
						// if user only enter 0 to go back to main page
						if (lineVector.length == 1) {
							try {
								choice = Integer.parseInt(lineVector[0]);
							} catch (NumberFormatException | NullPointerException e) {
								System.out.println("We can't parse what you enter [" + line
										+ "]. Please try again or enter 0 to go  back to main page. ");
								continue;
							}
							if (updateId > -1) {
								if (choice == -1) {
									// return true to flag exit.
									return true;
								}
								System.out.println("You have to enter -1 or an transcation");
							} else {
								if (choice == 0) {
									// return true to flag exit.
									return true;
								}
								System.out.println("You have to enter 0 or an transcation");
							}
						} else if (lineVector.length == 4) {
							try {
								// parsing the values to string and double
								date = lineVector[0];
								amount = Double.parseDouble(lineVector[1]);
								accountFromId = Integer.parseInt(lineVector[2]);
								accountToId = Integer.parseInt(lineVector[3]);
							} catch (NumberFormatException | NullPointerException e) {
								System.out.println("We can't parse what you enter [" + line
										+ "]. Please try again or enter 0 to go  back to main page. ");
								continue;
							}

							accountFrom = accountDao.get(accountFromId);
							accountTo = accountDao.get(accountToId);

							if (accountFrom != null && accountTo != null) {

								if (updateId > -1) {
									// update Transaction

									Transaction oldTrans = transactionDao.get(updateId);
									// return the money
									accountDao.accountTransfer(accountTo, accountFrom, oldTrans.getAmount());

									// create a new Transaction
									accountDao.accountTransfer(accountFrom, accountTo, amount);
									trans = new Transaction(date, Main.LOGIN_USER, accountFrom, accountTo, amount);
									transactionDao.update(updateId, trans);
								} else {
									// save Transaction
									accountDao.accountTransfer(accountFrom, accountTo, amount);

									trans = new Transaction(date, Main.LOGIN_USER, accountFrom, accountTo, amount);
									transactionDao.save(trans);
								}

								System.out.println("A transaction of " + formatter.format(amount) + " is withdraw from "
										+ accountFrom.getName() + " and deposit to " + accountTo.getName() + " at "
										+ date + ".");
								System.out.println(
										"| You new balance for both account are:                                                               |");
								System.out.println(accountFrom);
								System.out.println(accountTo);
								System.out.println(
										"===================================================================================");
								if (updateId > -1) {
									// update Transaction
									System.out.println(
											"| You can enter another transaction or enter -1 to go back to main page.           |");
								} else {
									System.out.println(
											"| You can enter another transaction or enter 0 to go back to main page.           |");
								}

								System.out.println(
										"===================================================================================");
							} else {
								System.out.println(
										"====================================================================================");
								System.out.println(
										"| Invalid Entry, Please check if you mistype account id or not follows the rules.|");
								System.out.println(
										"====================================================================================");
							}
						}

					} else if (lineVector.length == 5) {
						// parsing the values to string and double
						String dateString = lineVector[0];
						String name = lineVector[1];
						amount = Double.parseDouble(lineVector[2]);
						accountFromId = Integer.parseInt(lineVector[3]);
						accountToId = Integer.parseInt(lineVector[4]);
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
		// default to false
		return false;
	}

	public void printLine(PrintWriter writer, int size) {
		for (int i = 0; i < size; i++) {
			writer.print("=");
		}
		writer.println();
	}

	public void saveAccountToDat() {
		try (ObjectOutputStream accountFile = new ObjectOutputStream(new FileOutputStream(PRELOAD_ACCOUNT_DATA_DAT));) {
			List<Account> accounts = accountDao.getAll();

			// in order to aviod that EOFException when I readObject, I have to either
			// 1. write a null to tell its the end
			// 2. send the size.
			// I prefer not use null :)
			accountFile.writeInt(accounts.size());
			// accountFile.write(accounts.size());
			for (Account account : accounts) {
				accountFile.writeObject(account);
			}
		} catch (Exception e) {
			System.out.println("Unable to write data accounts to .dat file " + PRELOAD_ACCOUNT_DATA_DAT);
		}
	}

	public void preloadAccountFromDat() {
		try {
			try (ObjectInputStream accountFile = new ObjectInputStream(
					new FileInputStream(PRELOAD_ACCOUNT_DATA_DAT));) {
				// first object is total size in int, following with the object.
				int size = accountFile.readInt();
				while (size > 0) {
					Account acc = (Account) (accountFile.readObject());
					accountDao.save(acc);
					size--;
				}
			}
		} catch (Exception e) {
			System.out.println("Error during reading Account object from .dat file " + PRELOAD_ACCOUNT_DATA_DAT);
		}
	}

	public void saveTransactionToDat() {
		try (ObjectOutputStream transactionFile = new ObjectOutputStream(
				new FileOutputStream(PRELOAD_TRANSACTION_DATA_DAT));) {
			List<Transaction> transactions = transactionDao.getAll();

			// in order to aviod that EOFException when I readObject, I have to either
			// 1. write a null to tell its the end
			// 2. send the size.
			// I prefer not use null :)
			transactionFile.writeInt(transactions.size());
			// accountFile.write(accounts.size());
			for (Transaction transaction : transactions) {
				transactionFile.writeObject(transaction);
			}
		} catch (Exception e) {
			System.out.println("Unable to write transactions data to .dat file " + PRELOAD_TRANSACTION_DATA_DAT);
		}
	}

	public void preloadTransactionFromDat() {
		try {
			try (ObjectInputStream transactionFile = new ObjectInputStream(
					new FileInputStream(PRELOAD_TRANSACTION_DATA_DAT));) {
				// first object is total size in int, following with the object.
				int size = transactionFile.readInt();
				while (size > 0) {
					Transaction transaction = (Transaction) (transactionFile.readObject());
					transactionDao.save(transaction);
					size--;
				}
			}
		} catch (Exception e) {
			System.out
					.println("Error during reading Transaction object from .dat file " + PRELOAD_TRANSACTION_DATA_DAT);
		}
	}
}
