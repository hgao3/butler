package com.butler.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.butler.config.DataBase;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.exception.ResourceNotFoundException;

public class AccountDao implements Dao<Account> {

	public static int accountsCounter = 0;
	public static HashMap<Integer, Account> accounts = new HashMap<Integer, Account>();
	
	@Override
	public Account get(Integer id) throws ResourceNotFoundException {
		Account acc = AccountDao.accounts.get(id);
		if (acc == null) {
			throw new ResourceNotFoundException("Account", "id", id);
		} 
		return acc;
	}

	@Override
	public List<Account> getAll() {
		// non-Java 8
		//List<Account> result = new ArrayList(DataBase.accounts.keySet());
		
		// Java 8
		List<Account> result = AccountDao.accounts.values().stream().collect(Collectors.toList());
		return result;
	}

	@Override
	public void printAll() {
		for (Map.Entry<Integer, Account> entry : AccountDao.accounts.entrySet()) {		
			System.out.println(String.format("| ID: %-5d %s", entry.getKey(), entry.getValue()));
		}
	}
	
	public void accountTransfer(Account from, Account to, double amount) {
		this.withdraw(from, amount);
		this.deposit(to, amount);
	}
	
	public void withdraw(Account acc, double amount) {
		if (acc instanceof IncomeAccount) {
			// downcasting
			// increase amount if its a income account
			((IncomeAccount) acc).withdraw(amount);
		} else if (acc instanceof ExpenseAccount) {
			// downcasting.
			// decrease amount if its a expense account
			((ExpenseAccount) acc).withdraw(amount);
		}
	}
	
	public void deposit(Account acc, double amount) {
		if (acc instanceof IncomeAccount) {
			// downcasting
			// increase amount if its a income account
			((IncomeAccount) acc).deposit(amount);
		} else if (acc instanceof ExpenseAccount) {
			// downcasting.
			// decrease amount if its a expense account
			((ExpenseAccount) acc).deposit(amount);
		}
	}

	@Override
	public void save(Account acc) {
		if (acc != null) {
			acc.setId(AccountDao.accountsCounter);
			AccountDao.accounts.put(AccountDao.accountsCounter, acc);
			AccountDao.accountsCounter++;
		}		
	}

	@Override
	public void update(Integer id, Account a) {
		AccountDao.accounts.remove(id);
		AccountDao.accounts.put(id, a);		
	}

	@Override
	public void delete(Integer id) {
		AccountDao.accounts.remove(id);		
	}
}
