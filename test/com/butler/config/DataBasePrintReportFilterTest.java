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

class DataBasePrintReportFilterTest {

	private AccountUI accountUI = new AccountUI();
	private DataBase db = new DataBase();
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	
	private EmbeddedDerbyDatabase conn = new EmbeddedDerbyDatabase();

	@BeforeEach
	void setUp() throws Exception {
		conn.createDatabaseConnection();
		conn.createTable();
		//conn.preloadAccountData();
		//conn.preloadTransactionsData();
	}

	@AfterEach
	void tearDown() throws Exception {
		conn.dropTable();
		conn.shutDownDatabaseConnection();
	}
	
	@Test
	void AccountDatTest() throws FileNotFoundException {
		
		db.preloadAccount();
		db.preloadTransaction();
		
		Account acc = accountDao.get(2);
		db.printReportsWithAccountFilter(acc);
		
						// test print report by scanning it and compare line by line
						String line;
						Scanner scan = new Scanner(new File(DataBase.ACCOUNT_REPORT));

						try {
							
//===================================================================================================================
							line = scan.nextLine();
//| Here is your Account Summary                                                                                    |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("Here is your Account Summary"), "Contains Here is your Account Summary");
//===================================================================================================================
							line = scan.nextLine();
//| ID: 2     | Name: Wallet               | Type: Income  | Amount:      $101.60 | Category : Cash                 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("ID: 2"), "Contains ID: 2");
//=================================================================================================================================================
							line = scan.nextLine();
//| Here is your Transaction View                                                                                                                 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("Here is your Transaction View  "), "Contains Here is your Transaction View  ");	
//=================================================================================================================================================
							line = scan.nextLine();
//| ID: 0     | Date: 05/18/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $60.85 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("Date: 05/18/2019"), "Contains Date: 05/18/2019");
//| ID: 2     | Date: 05/20/2019   | User: Hugh    | WithdrawFrom: Citi Bank Checking   | DepositTo : Wallet               | Amount:      $200.00 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("User: Hugh"), "Contains User: Hugh");
//| ID: 3     | Date: 05/21/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Whole Foods Market   | Amount:      $105.99 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("WithdrawFrom: Wallet"), "Contains WithdrawFrom: Wallet");
//| ID: 4     | Date: 05/22/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $10.99 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("DepositTo : Pizza Hut"), "Contains DepositTo : Pizza Hut");
//| ID: 5     | Date: 05/23/2019   | User: Hugh    | WithdrawFrom: Wallet               | DepositTo : Pizza Hut            | Amount:       $20.57 |
							line = scan.nextLine();
							Assertions.assertTrue(line.contains("Amount:       $20.57"), "Contains Amount:       $20.57");
//=================================================================================================================================================
							line = scan.nextLine();
						} catch (Exception e) {
							System.out.println("Bad record or read in data.");
						}
	}
}
