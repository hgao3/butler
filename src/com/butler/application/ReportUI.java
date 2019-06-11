package com.butler.application;

import java.util.Scanner;

import com.butler.config.DataBase;
import com.butler.dao.AccountDao;
import com.butler.dao.TransactionDao;
import com.butler.domain.Account;

public class ReportUI {
	private Scanner scan = null;
	private AccountDao accountDao = new AccountDao();
	private DataBase dataBase = new DataBase();
	private boolean isExitMenu = false;

	public void managePrintReports() {
		isExitMenu = false;
		scan = new Scanner(System.in);
		while (!isExitMenu) {
			System.out.println(
					"===================================================================================================================");
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |", "Here is your accounts view"));
			System.out.println(
					"===================================================================================================================");
			accountDao.printAll();
			System.out.println(
					"===================================================================================================================");
			System.out.println(String.format("| %-110s  |",
					"Please enter the ID number for the account that you are trying to filter."));
			System.out.println(
					String.format("| %-110s  |", "You can also enter -1 to go back to report main page or -2 to print all account without filter."));
			
			System.out.println(
					"===================================================================================================================");
			int id = ButlerIOUtils.getInputWithIntRange(scan, -2, accountDao.accountsCounter);
			if (id == -1) {
				isExitMenu = true;
			} else if (id == -2){
				dataBase.printReports();
			} else {
				Account acc = accountDao.get(id);
				dataBase.printReportsWithAccountFilter(acc);
			}
		}
	}
}
