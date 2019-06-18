package com.butler.domain;

public class ExpenseAccount extends Account {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5334502539871031867L;
	public ExpenseAccount() {
		super();
	}
	public ExpenseAccount(String name, String owner, double amount, String category) {
		super(name, owner, amount, category);
	}
	@Override
	public String getType() {
		return "expense";
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
	@Override
	public String toDataString() {
		return String.format("%s,%s,%.2f,%s", 
				this.getType(), this.getName(), this.getAmount(), this.getCategory()); 
	}
}
