package com.butler.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.butler.application.AccountUI;
import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;

class DataBaseDatTest {

	private AccountUI accountUI = new AccountUI();
	private DataBase db = new DataBase();
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	
	@BeforeEach
	void setUp() throws Exception {
		try {
    		// load test data
			db.preloadAccount();
			db.preloadTransaction();
		} catch (FileNotFoundException e) {
			Assertions.fail();
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		accountDao.accounts.clear();
		accountDao.accountsCounter = 0;
		transactionDao.transactions.clear();
		transactionDao.transactionsCounter = 0;
	}
	
	@Test
	void AccountDatTest() throws FileNotFoundException {
		
		System.out.println(String.format("| %-110s  |", "Here is my pre-load Account from txt file"));
		accountDao.printAll();
		System.out.println(String.format("| %-110s  |", "Save data to .dat file"));
		db.saveAccountToDat(); 
		System.out.println(String.format("| %-110s  |", "Here is my pre-load Account from .dat file"));
		  
		  accountDao.accounts.clear(); 
		  accountDao.accountsCounter = 0; 
		  db.preloadAccountFromDat();
		  accountDao.printAll();
		 
			// Only going to do one test with the first account
			Account acc0 = AccountDao.accounts.get(0);
			Assertions.assertTrue(acc0.getAmount()==1300, "Amount should be 1300");
			Assertions.assertTrue(acc0.getAmountInCurrency().equalsIgnoreCase("$1,300.00"), "Amount should be $1,300.00");
			Assertions.assertTrue(acc0.getCategory().equalsIgnoreCase("Checking Account"), "Category should be Checking Account");
			Assertions.assertTrue(acc0.getName().equalsIgnoreCase("Citi Bank Checking"), "Name should be Citi Bank Checking");
			Assertions.assertTrue(acc0.getOwner().equalsIgnoreCase("Hugh"), "Owner should be Hugh");
			Assertions.assertTrue(acc0.getType().equalsIgnoreCase("Income"), "Type should be Income");
			
			Assertions.assertTrue( AccountDao.accounts.size()==6, "size should be 6");
		  
			// clean up
			  accountDao.accounts.clear(); 
			  accountDao.accountsCounter = 0; 
		
		accountDao.getAll()
	   		.stream()
	   		.filter(a -> (a.getName().equals("Wallet")))
	   		.filter(a -> (a.getAmount() > 100))
	   		.forEach(System.out::print);
		
	}
	
	@Test
	void TransactionDatTest() throws FileNotFoundException {
		
		System.out.println(String.format("| %-110s  |", "Here is my pre-load Transaction from txt file"));
		transactionDao.printAll();
		System.out.println(String.format("| %-110s  |", "Save data to .dat file"));
		db.saveTransactionToDat();
		System.out.println(String.format("| %-110s  |", "Here is my pre-load Transaction from .dat file"));
		  
		  accountDao.accounts.clear(); 
		  accountDao.accountsCounter = 0; 
		transactionDao.transactions.clear(); 
		transactionDao.transactionsCounter = 0; 
		db.preloadAccount();
		  db.preloadTransactionFromDat();
		  transactionDao.printAll();
		 
			// Only going to do one test with the first transaction
			Transaction tran = TransactionDao.transactions.get(0);
			Assertions.assertTrue(tran.getAmount()==60.85, "Amount should be 60.85");
			Assertions.assertTrue(tran.getAmountInCurrency().equalsIgnoreCase("$60.85"), "Amount should be $60.85");
			Assertions.assertTrue(tran.getDate().toString().equalsIgnoreCase("Sat May 18 00:00:00 EDT 2019"), "Date should be Sat May 18 00:00:00 EDT 2019");
			Assertions.assertTrue(tran.toStringDate().equalsIgnoreCase("05/18/2019"), "Date should be 05/18/2019");
			Assertions.assertTrue(tran.getUser().equalsIgnoreCase("Hugh"), "User should be Hugh");
			Assertions.assertTrue(tran.getDepositTo().getName().equals(AccountDao.accounts.get(5).getName()), "WithdrawFrom should be user 5");
			Assertions.assertTrue(tran.getWithdrawFrom().getName().equals(AccountDao.accounts.get(2).getName()), "DepositTo should be user 2");
			
			Assertions.assertTrue( TransactionDao.transactions.size()==6, "size should be 6");		
			
			//clean up
			transactionDao.transactions.clear(); 
			transactionDao.transactionsCounter = 0; 
			accountDao.accounts.clear(); 
			accountDao.accountsCounter = 0; 
			
	}

}
