package com.butler.domain;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;

class AccountTest {

	@Test
	void updateAmountTest() {
		Account[] accountArray = {new IncomeAccount("Bank of America Checking", "Hugh", 1000, "Checking Account"),
				new IncomeAccount("Bank of America Saving", "Hugh", 1000, "Saving Account"),
				new ExpenseAccount("Whole Foods Market", "Hugh", 1000, "Groceries")};
		
		for (Account acc: accountArray) {
			Assertions.assertTrue(acc.getAmount()==1000, "Amount should be all 1000");
		}
		
		for (Account acc: accountArray) {
			if (acc != null) {
				if (acc instanceof IncomeAccount) {
					// downcasting
					// increase amount for 100 if its a income account
					((IncomeAccount) acc).deposit(100);
				}
				
				if (acc instanceof ExpenseAccount) {
					// downcasting.
					// decrease amount for 100 if its a expense account
					((ExpenseAccount) acc).deposit(100);
				}
			}
		}
		
		for (Account acc: accountArray) {
			if (acc != null) {
				if (acc instanceof IncomeAccount) {
					Assertions.assertTrue(acc.getAmount()==1100, "Amount should be all 1100");
				}
				
				if (acc instanceof ExpenseAccount) {
					Assertions.assertTrue(acc.getAmount()==900, "Amount should be all 900");
				}
			}
		}
		
		for (Account acc: accountArray) {
			if (acc != null) {
				if (acc instanceof IncomeAccount) {
					// downcasting
					// decrease amount for 100 if its a income account
					((IncomeAccount) acc).withdraw(100);
				}
				
				if (acc instanceof ExpenseAccount) {
					// downcasting.
					// increase amount for 100 if its a expense account
					((ExpenseAccount) acc).withdraw(100);
				}
			}
		}
		
		for (Account acc: accountArray) {
			if (acc != null) {
				if (acc instanceof IncomeAccount) {
					Assertions.assertTrue(acc.getAmount()==1000, "Amount should be all 1000");
				}
				
				if (acc instanceof ExpenseAccount) {
					Assertions.assertTrue(acc.getAmount()==1000, "Amount should be all 1000");
				}
			}
		}
	}
	
	@Test
	void basicSetterGetterTest() {
		Account[] accountArray = {new IncomeAccount("Bank of America Checking", "Hugh", 1000, "Checking Account"),
				new IncomeAccount("Bank of America Saving", "Hugh", 1000, "Saving Account"),
				new ExpenseAccount("Whole Foods Market", "Hugh", 1000, "Groceries")};
		
		// default initializing
		Account acc = new IncomeAccount();
		Assertions.assertTrue(acc.getAmount()==0, "Amount should be 0");
		Assertions.assertTrue(acc.getName().equalsIgnoreCase(""), "name should be empty");
		Assertions.assertTrue(acc.getOwner().equalsIgnoreCase(""), "owner should be empty");
		Assertions.assertTrue(acc.getCategory().equalsIgnoreCase(""), "category should be empty");
		Assertions.assertTrue(acc.getType().equalsIgnoreCase("income"));
		
		// use setter to set values
		acc.setName("Bank of America Checking");
		acc.setOwner("Hugh");
		acc.setCategory("Checking Account");
		((IncomeAccount) acc).deposit(100);
		
		Assertions.assertTrue(acc.getAmount()==100, "Amount should be 100");
		Assertions.assertTrue(acc.getName().equalsIgnoreCase("Bank of America Checking"), "name should be Bank of America Checking");
		Assertions.assertTrue(acc.getOwner().equalsIgnoreCase("Hugh"), "owner should be Hugh");
		Assertions.assertTrue(acc.getCategory().equalsIgnoreCase("Checking Account"), "category should be Checking Account");
		Assertions.assertTrue(acc.getAmountInCurrency().equalsIgnoreCase("$100.00"), "Amount should be $100.00");
		
		// check toString
		Assertions.assertTrue(acc.toString().equalsIgnoreCase("| Name: Bank of America Checking | Type: Income  | Amount:      $100.00 | Category : Checking Account     |"), "toString is not match");

		// default initializing
		acc = new ExpenseAccount();
		Assertions.assertTrue(acc.getType().equalsIgnoreCase("expense"));
		
		// check toString for ExpenseAccount 
		acc = new ExpenseAccount("Whole Foods Market", "Hugh", 1000, "Groceries");
		// check toString
		System.out.println(acc);
		Assertions.assertTrue(acc.toString().equalsIgnoreCase("| Name: Whole Foods Market   | Type: Expense | Amount:    $1,000.00 | Category : Groceries            |"), "toString is not match");

	}

}
