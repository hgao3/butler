package com.butler.domain;

public class ExpenseAccount extends Account {
	public ExpenseAccount() {
		super();
	}
	public ExpenseAccount(String name, String owner, double amount, String category) {
		super(name, owner, amount, category);
	}
	@Override
	public String getType() {
		return "Expense";
	}
	public void deposit(double amount) {
		this.updateAmount(-amount);
	}
	public void withdraw(double amount) {
		this.updateAmount(amount);
	}
	@Override
	public String toString() {
		return String.format("| Name: %-20s | Type: %-7s | Amount: %12s | Category : %-20s |", 
				this.getName(), this.getType(), this.getAmountInCurrency(), this.getCategory()); 
	}
}