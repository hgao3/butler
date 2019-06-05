package com.butler.application;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;

class AccountUITest {
	private AccountUI accountUI = new AccountUI();
	private DataBase db = new DataBase();
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	
	@BeforeEach
	void setUp() throws Exception {
		try {
    		// load test data
			db.preloadAccount();
		} catch (FileNotFoundException e) {
			Assertions.fail();
		}
	}

	@AfterEach
	void tearDown() throws Exception {
		accountDao.accounts.clear();
	}
	
	@Test
	void updateAmountTest() {
		accountUI.manageAccounts();
	}
}
