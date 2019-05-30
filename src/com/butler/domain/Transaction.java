package com.butler.domain;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.butler.exception.ClassInstantiationException;

public class Transaction {
	private Date date;
	private String user;
	private Account withdrawFrom;
	private Account depositTo;
	private double amount;
	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

	public Transaction(String dateString, String user, Account withdrawFrom, Account depositTo, double amount)
			throws ClassInstantiationException {
		try {
			this.setDate(dateString);
			this.user = user;
			this.withdrawFrom = withdrawFrom;
			this.depositTo = depositTo;
			this.amount = amount;
		} catch (ParseException e) {
			throw new ClassInstantiationException(this.getClass().getName(), "date", dateString);
		}
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setDate(String date) throws ParseException {
		this.date = formatter.parse(date);
	}

	public String toStringDate() {
		return formatter.format(this.date);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Account getWithdrawFrom() {
		return withdrawFrom;
	}

	public void setWithdrawFrom(Account withdrawFrom) {
		this.withdrawFrom = withdrawFrom;
	}

	public Account getDepositTo() {
		return depositTo;
	}

	public void setDepositTo(Account depositTo) {
		this.depositTo = depositTo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAmountInCurrency() {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(this.amount);
	}

	@Override
	public String toString() {
		return String.format("| Date: %-12s | User: %-7s | WithdrawFrom: %-20s | DepositTo : %-20s | Amount: %12s |",
				this.toStringDate(), this.getUser(), this.getWithdrawFrom().getName(), this.getDepositTo().getName(),
				this.getAmountInCurrency());
	}
}
