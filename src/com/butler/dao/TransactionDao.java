package com.butler.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.butler.config.DataBase;
import com.butler.domain.Account;
import com.butler.domain.Transaction;
import com.butler.exception.ResourceNotFoundException;

public class TransactionDao implements Dao<Transaction> {

	public static int transactionsCounter = 0;
	public static HashMap<Integer, Transaction> transactions = new HashMap<Integer, Transaction>();
	
	@Override
	public Transaction get(Integer id) {
		Transaction trans = TransactionDao.transactions.get(id);
		if (trans == null) {
			throw new ResourceNotFoundException("Account", "id", id);
		} 
		return trans;
	}

	@Override
	public List<Transaction> getAll() {
		// non-Java 8
		//List<Transaction> result = new ArrayList(DataBase.transactions.keySet());
		
		// Java 8
		List<Transaction> result = TransactionDao.transactions.values().stream().collect(Collectors.toList());
		return result;
	}

	@Override
	public void printAll() {
		for (Map.Entry<Integer, Transaction> entry : TransactionDao.transactions.entrySet()) {		
			System.out.println(String.format("| ID: %-5d %s", entry.getKey(), entry.getValue()));
		}
	}

	@Override
	public void save(Transaction t) {
		if (t != null) {
			TransactionDao.transactions.put(TransactionDao.transactionsCounter, t);
			TransactionDao.transactionsCounter++;
		}	
	}

}
