package com.butler.domain;

import java.io.Serializable;
import java.text.NumberFormat;

public abstract class Account implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2241276831419900166L;
	private int id;
	private String name;
	private String owner;
	private double amount;
	private String category;
	
	protected Account() {
		this.name = "";
		this.owner = "";
		this.amount = 0;
		this.category = "";
	}
	protected Account(String name, String owner, double amount, String category) {
		this.name = name;
		this.owner = owner;
		this.amount = amount;
		this.category = category;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public double getAmount() {
		return amount;
	}
	public String getAmountInCurrency() {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		return formatter.format(amount);
	}
	// we do not want user to set amount other than constructor initializing
	//public void setAmount(double amount) {this.amount = amount;}

	// update the amount instead of reset the amount
	// positive amount will increase the amount
	// negative number will decrease the amount
	public void updateAmount(double amount) {
		this.amount = this.amount + amount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public abstract String getType();
	
	public abstract String toDataString();
}
