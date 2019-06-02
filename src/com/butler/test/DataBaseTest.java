package com.butler.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;

class DataBaseTest {

	/*
	 * My input file context
	 * 
	income,Citi Bank Checking,Hugh,3000,Checking Account
	income,Citi Bank Saving,Hugh,3000,Saving Account
	income,Wallet,Hugh,100,Cash
	expense,Whole Foods Market,Hugh,0,Groceries
	expense,Rent,Hugh,0,Rent
	expense,Pizza Hut,Hugh,0,Dining Out
	
	05/18/2019,Hugh,60.85,2,5
	05/19/2019,Hugh,1500,0,4
	05/20/2019,Hugh,200,0,2
	05/21/2019,Hugh,105.99,2,3
	05/22/2019,Hugh,10.99,2,5
	05/23/2019,Hugh,20.57,2,5

	*/
	
	@Test
	void test() throws FileNotFoundException {
		DataBase db = new DataBase();
		
		// test preloadAccount
		db.preloadAccount();
		
		// Only going to do one test with the first account
		Account acc0 = AccountDao.accounts.get(0);
		Assertions.assertTrue(acc0.getAmount()==3000, "Amount should be 3000");
		Assertions.assertTrue(acc0.getAmountInCurrency().equalsIgnoreCase("$3,000.00"), "Amount should be $3,000.00");
		Assertions.assertTrue(acc0.getCategory().equalsIgnoreCase("Checking Account"), "Category should be Checking Account");
		Assertions.assertTrue(acc0.getName().equalsIgnoreCase("Citi Bank Checking"), "Name should be Citi Bank Checking");
		Assertions.assertTrue(acc0.getOwner().equalsIgnoreCase("Hugh"), "Owner should be Hugh");
		Assertions.assertTrue(acc0.getType().equalsIgnoreCase("Income"), "Type should be Income");
		
		Assertions.assertTrue( AccountDao.accounts.size()==6, "size should be 6");
		
		
		// test preloadTransaction
		db.preloadTransaction();
		
		// Only going to do one test with the first transaction
		Transaction tran = TransactionDao.transactions.get(0);
		Assertions.assertTrue(tran.getAmount()==60.85, "Amount should be 60.85");
		Assertions.assertTrue(tran.getAmountInCurrency().equalsIgnoreCase("$60.85"), "Amount should be $60.85");
		Assertions.assertTrue(tran.getDate().toString().equalsIgnoreCase("Sat May 18 00:00:00 EDT 2019"), "Date should be Sat May 18 00:00:00 EDT 2019");
		Assertions.assertTrue(tran.toStringDate().equalsIgnoreCase("05/18/2019"), "Date should be 05/18/2019");
		Assertions.assertTrue(tran.getUser().equalsIgnoreCase("Hugh"), "User should be Hugh");
		Assertions.assertTrue(tran.getDepositTo().equals(AccountDao.accounts.get(5)), "WithdrawFrom should be user 5");
		Assertions.assertTrue(tran.getWithdrawFrom().equals(AccountDao.accounts.get(2)), "DepositTo should be user 2");
		
		Assertions.assertTrue( TransactionDao.transactions.size()==6, "size should be 6");
		
		// test printReports
		db.printReports();
		
		// test print report by scanning it and compare line by line
		String line;
		Scanner scan = new Scanner(new File(DataBase.ACCOUNT_REPORT));

		try {
//          ===================================================================================================================
			line = scan.nextLine();
//			| Here is your Account Summary                                                                                    |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Here is your Account Summary"), "Contains Here is your Account Summary");
//			===================================================================================================================
			line = scan.nextLine();
//			| ID: 0     | Name: Citi Bank Checking   | Type: Income  | Amount:    $1,300.00 | Category : Checking Account     |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("| ID: 0     | Name: Citi Bank Checking   | Type: Income  | Amount:    $1,300.00 | Category : Checking Account     |"), "Contains | ID: 0     | Name: Citi Bank Checking   | Type: Income  | Amount:    $1,300.00 | Category : Checking Account     |");
//			| ID: 1     | Name: Citi Bank Saving     | Type: Income  | Amount:    $3,000.00 | Category : Saving Account       |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("ID: 1"), "Contains ID: 1");

//			| ID: 2     | Name: Wallet               | Type: Income  | Amount:      $101.60 | Category : Cash                 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Name: Wallet"), "Contains Name: Wallet");
//			| ID: 3     | Name: Whole Foods Market   | Type: Expense | Amount:    ($105.99) | Category : Groceries            |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Type: Expense"), "Contains Type: Expense");
//			| ID: 4     | Name: Rent                 | Type: Expense | Amount:  ($1,500.00) | Category : Rent                 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Amount:  ($1,500.00)"), "Contains Amount:  ($1,500.00)");
//			| ID: 5     | Name: Pizza Hut            | Type: Expense | Amount:     ($92.41) | Category : Dining Out           |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Category : Dining Out"), "Contains Category : Dining Out");
//			=================================================================================================================================================
			line = scan.nextLine();
//			| Here is your Transaction View                                                                                                                 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Here is your Transaction View  "), "Contains Here is your Transaction View  ");
//			=================================================================================================================================================
			line = scan.nextLine();
//			| ID: 0     | Date: 05/18/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $60.85 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("| ID: 0     | Date: 05/18/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $60.85 |"), "Contains | ID: 0     | Date: 05/18/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $60.85 |");
//			| ID: 1     | Date: 05/19/2019   | User: Hugh    | WithdrawFrom: Citi Bank Checking   | DepositTo : Rent                 | Amount:    $1,500.00 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("User: Hugh"), "Contains User: Hugh");
//			| ID: 2     | Date: 05/20/2019   | User: Hugh    | WithdrawFrom: Citi Bank Checking   | DepositTo : Wallet               | Amount:      $200.00 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Date: 05/20/2019"), "Contains Date: 05/20/2019");
//			| ID: 3     | Date: 05/21/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Whole Foods Market   | Amount:      $105.99 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("WithdrawFrom: Wallet"), "Contains WithdrawFrom: Wallet");
//			| ID: 4     | Date: 05/22/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $10.99 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("DepositTo : Pizza Hut"), "Contains DepositTo : Pizza Hut");
//			| ID: 5     | Date: 05/23/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $20.57 |
			line = scan.nextLine();
			Assertions.assertTrue(line.contains("Amount:       $20.57"), "Contains Amount:       $20.57");
//			=================================================================================================================================================
			line = scan.nextLine();
		} catch (Exception e) {
			System.out.println("Bad record or read in data.");
		}
	}
}
