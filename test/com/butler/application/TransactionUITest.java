package com.butler.application;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;

class TransactionUITest {
	private TransactionUI transactionUI = new TransactionUI();
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
		transactionDao.transactions.clear();
	}
	
	@Test
	void updateAmountTest() {
		transactionUI.manageTransactions();
	}
}
