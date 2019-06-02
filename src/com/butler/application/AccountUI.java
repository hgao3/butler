package com.butler.application;

import java.util.Scanner;

import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;

public class AccountUI {
	
	private AccountDao accountDao = new AccountDao();
	private TransactionDao transactionDao = new TransactionDao();
	
	public void manageAccounts(Scanner scan) {
		System.out.println("===================================================================================================================");
		System.out.println("| Sorry, only view function is available in this version.                                                         |");
		System.out.println("| Here is your accounts view                                                                                      |");
		System.out.println("===================================================================================================================");
		accountDao.printAll();
		System.out.println("===================================================================================================================");
		System.out.println("| You can enter 0 to go back to main page.                                        |");
		System.out.println("===================================================================================");
		ButlerIOUtils.getInputWithIntRange(scan, 0, 0);
	}
	
	
}
