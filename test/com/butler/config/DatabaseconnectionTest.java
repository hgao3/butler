package com.butler.config;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.butler.application.AccountUI;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

class DatabaseconnectionTest {

	private EmbeddedDerbyDatabase conn = new EmbeddedDerbyDatabase();

	@BeforeEach
	void setUp() throws Exception {
		conn.createDatabaseConnection();
		conn.createTable();
		conn.preloadAccountData();
		conn.preloadTransactionsData();
	}

	@AfterEach
	void tearDown() throws Exception {
		conn.dropTable();
		conn.shutDownDatabaseConnection();
	}
	
	private AccountUI accountUI = new AccountUI();
	private DataBase db = new DataBase();
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();

	@Test
	void test() {

		try {			
			
			System.out.println("save account");
			Account acc = new IncomeAccount("Costco", "Hugh", 0, "Groceries");
			accountDao.save(acc);
			
			System.out.println("get by id 6");
			acc = accountDao.get(6);
			
			Assertions.assertTrue( acc.getId() == 6 , "id should be 6");
			
			
			System.out.println("get all");
			List<Account> accounts = accountDao.getAll();
			for (Account account : accounts) {
				System.out.println(account);
			}
			Assertions.assertTrue( accounts.size()==7, "size should be 7");

			System.out.println("transfer money");
			// transfer money
			accountDao.accountTransfer(accounts.get(0), accounts.get(2), 500);
			
			Assertions.assertTrue( accountDao.get(0).getAmount()==2500, "account 0's amount should be 2500");
			Assertions.assertTrue( accountDao.get(2).getAmount()==600, "account 2's amount should be 600");
		
			
			System.out.println("update account name");
			acc.setName("test");
			accountDao.update(6, acc);
			
			// get by id
			acc = accountDao.get(6);
			Assertions.assertTrue( acc.getName().equalsIgnoreCase("test"), "account 6's name should be 'test'");
			
			
			System.out.println("delete account by id 6");
			// delete 6
			accountDao.delete(6);
			
			accounts = accountDao.getAll();
			Assertions.assertTrue( accounts.size()==6, "size should be 6 now");
			
			System.out.println("print all account from db");
			accountDao.printAll();
			
			
			
			System.out.println("save transaction");
			String date = "05/25/2019";
            String myUser = "Hugh";
            Double amount = 20.0;
            Account withdrawFrom = accountDao.get(2);
            Account depositTo = accountDao.get(5);
			Transaction trans = new Transaction(date, myUser, withdrawFrom, depositTo, amount);
			transactionDao.save(trans);
			
			
			System.out.println("get by id 6");
			trans = transactionDao.get(6);
			Assertions.assertTrue( trans.getId() == 6 , "id should be 6");
			
			System.out.println("get all");
			List<Transaction> transactions = transactionDao.getAll();
			for (Transaction tr : transactions) {
				System.out.println(tr);
			}
			Assertions.assertTrue( transactions.size()==7, "size should be 7");
			
			
			System.out.println("update date");
			trans.setDate( "05/26/2019");
			transactionDao.update(6, trans);
			trans = transactionDao.get(6);
			Assertions.assertTrue( trans.toStringDate().equalsIgnoreCase("05/26/2019"), "date should be 05/26/2019");
			
			System.out.println("delete transaction");
			transactionDao.delete(6);
			transactions = transactionDao.getAll();
			Assertions.assertTrue( transactions.size()==6, "size should be 6 now");
			
			transactionDao.printAll();			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
