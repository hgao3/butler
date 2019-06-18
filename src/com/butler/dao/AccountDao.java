package com.butler.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.butler.config.DataBase;
import com.butler.config.EmbeddedDerbyDatabase;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.exception.ResourceNotFoundException;

public class AccountDao implements Dao<Account> {

	public static int accountsCounter = 0;

	@Override
	public Account get(Integer id) throws ResourceNotFoundException {
		Account acc = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = EmbeddedDerbyDatabase.conn.prepareStatement("SELECT * FROM accounts WHERE aid = ?");
			ps.setInt(1, id);

			// process the results
			rs = ps.executeQuery();

			if (rs.next()) {
				id = rs.getInt(1);
				String type = rs.getString(2);
				String name = rs.getString(3);
				String owner = rs.getString(4);
				Double amount = rs.getDouble(5);
				String category = rs.getString(6);

				if (type.equalsIgnoreCase("income")) {
					acc = new IncomeAccount(name, owner, amount, category);
					acc.setId(id);
				} else if (type.equalsIgnoreCase("expense")) {
					acc = new ExpenseAccount(name, owner, amount, category);
					acc.setId(id);
				}
			}

			if (acc == null) {
				throw new ResourceNotFoundException("Account", "id", id);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, ps, null);
		}
		return acc;
	}

	@Override
	public List<Account> getAll() {
		List<Account> accountList = new ArrayList<>();

		ResultSet rs = null;
		Statement s = null;

		try {
			// select data from tables accounts
			s = EmbeddedDerbyDatabase.conn.createStatement();
			rs = s.executeQuery("SELECT * FROM accounts ORDER BY aid");

			while (rs.next()) {
				int id = rs.getInt(1);
				String type = rs.getString(2);
				String name = rs.getString(3);
				String owner = rs.getString(4);
				Double amount = rs.getDouble(5);
				String category = rs.getString(6);

				Account acc = null;
				if (type.equalsIgnoreCase("income")) {
					acc = new IncomeAccount(name, owner, amount, category);
					acc.setId(id);
				} else if (type.equalsIgnoreCase("expense")) {
					acc = new ExpenseAccount(name, owner, amount, category);
					acc.setId(id);
				}
				accountList.add(acc);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, s, null);
		}
		accountsCounter = accountList.size();
		return accountList;
	}

	@Override
	public void printAll() {
		List<Account> accountList = getAll();
		for (Account account : accountList) {
			System.out.println(String.format("| ID: %-5d %s", account.getId(), account));
		}
	}

	public void accountTransfer(Account from, Account to, double amount) {

		this.withdraw(from, amount);
		this.deposit(to, amount);

		update(from.getId(), from);
		update(to.getId(), to);

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
	public int save(Account acc) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (acc != null) {
			try {
				ps = EmbeddedDerbyDatabase.conn.prepareStatement(
						"insert into accounts (type, name, owner, amount, category) values (?, ?, ?, ?, ?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

				ps.setString(1, acc.getType().toLowerCase());
				ps.setString(2, acc.getName());
				ps.setString(3, acc.getOwner());
				ps.setDouble(4, acc.getAmount());
				ps.setString(5, acc.getCategory());
				ps.executeUpdate();

				rs = ps.getGeneratedKeys(); // will return the ID in ID_COLUMN
				if (rs.next()) {
					int number = rs.getInt(1);
					return number;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// release all open resources to avoid unnecessary memory usage
				EmbeddedDerbyDatabase.close(rs, ps, null);
			}
		}
		return -1;
	}

	@Override
	public void update(Integer id, Account acc) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = EmbeddedDerbyDatabase.conn
					.prepareStatement("update accounts set type=?, name=?, owner=?, amount=?, category=? WHERE aid=?");

			ps.setString(1, acc.getType().toLowerCase());
			ps.setString(2, acc.getName());
			ps.setString(3, acc.getOwner());
			ps.setDouble(4, acc.getAmount());
			ps.setString(5, acc.getCategory());
			ps.setInt(6, id);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, ps, null);
		}
	}

	@Override
	public void delete(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = EmbeddedDerbyDatabase.conn.prepareStatement("DELETE FROM accounts WHERE aid=?");

			ps.setInt(1, id);
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, ps, null);
		}
	}
}
