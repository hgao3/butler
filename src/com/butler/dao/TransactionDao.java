package com.butler.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.butler.config.DataBase;
import com.butler.config.EmbeddedDerbyDatabase;
import com.butler.domain.Account;
import com.butler.domain.ExpenseAccount;
import com.butler.domain.IncomeAccount;
import com.butler.domain.Transaction;
import com.butler.exception.ClassInstantiationException;
import com.butler.exception.ResourceNotFoundException;

public class TransactionDao implements Dao<Transaction> {

	public static int transactionsCounter = 0;
	public AccountDao accountDao = new AccountDao();

	@Override
	public Transaction get(Integer id) throws ResourceNotFoundException {
		Transaction trans = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = EmbeddedDerbyDatabase.conn.prepareStatement("SELECT * FROM transactions WHERE tid = ?");
			ps.setInt(1, id);

			// process the results
			rs = ps.executeQuery();

			if (rs.next()) {
				id = rs.getInt(1);
				String date = rs.getString(2);
				String myUser = rs.getString(3);
				Double amount = rs.getDouble(4);
				String withdrawFromId = rs.getString(5);
				String depositToId = rs.getString(6);

				try {
					Account withdrawFrom = accountDao.get(Integer.parseInt(withdrawFromId));
					Account depositTo = accountDao.get(Integer.parseInt(depositToId));

					trans = new Transaction(date, myUser, withdrawFrom, depositTo, amount);
					trans.setId(id);
				} catch (ClassInstantiationException e) {
					e.printStackTrace();
				}
			}

			if (trans == null) {
				throw new ResourceNotFoundException("Transaction", "id", id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, ps, null);
		}

		return trans;
	}

	@Override
	public List<Transaction> getAll() {
		List<Transaction> transactionList = new ArrayList<>();

		// PreparedStatement ps = null;
		ResultSet rs = null;
		Statement s = null;

		try {
			// select data from tables accounts
			s = EmbeddedDerbyDatabase.conn.createStatement();
			rs = s.executeQuery("SELECT * FROM transactions ORDER BY tid");

			while (rs.next()) {
				int id = rs.getInt(1);
				String date = rs.getString(2);
				String myUser = rs.getString(3);
				Double amount = rs.getDouble(4);
				String withdrawFromId = rs.getString(5);
				String depositToId = rs.getString(6);

				Transaction trans = null;
				try {
					Account withdrawFrom = accountDao.get(Integer.parseInt(withdrawFromId));
					Account depositTo = accountDao.get(Integer.parseInt(depositToId));

					trans = new Transaction(date, myUser, withdrawFrom, depositTo, amount);
					trans.setId(id);
				} catch (ClassInstantiationException e) {
					e.printStackTrace();
				}
				transactionList.add(trans);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, s, null);
		}

		transactionsCounter = transactionList.size();
		return transactionList;
	}

	public List<Transaction> getAllWithFilter(Account acc) {
		List<Transaction> transactionList = new ArrayList<>();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = EmbeddedDerbyDatabase.conn
					.prepareStatement("SELECT tid, date, myUser, transactions.amount, a.name, b.name FROM transactions"
							+ " INNER JOIN accounts as a ON a.aid = transactions.withdrawFromId"
							+ " INNER JOIN accounts as b ON b.aid = transactions.depositToId"
							+ " WHERE transactions.withdrawFromId = ? or transactions.depositToId = ? ORDER BY tid");

			ps.setInt(1, acc.getId());
			ps.setInt(2, acc.getId());
			// process the results
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt(1);
				String date = rs.getString(2);
				String myUser = rs.getString(3);
				Double amount = rs.getDouble(4);

				// get name instead of aid
				String withdrawFromId = rs.getString(5);
				String depositToId = rs.getString(6);

				Transaction trans = null;
				try {
					// only need to create a fake account with name
					Account withdrawFrom = new IncomeAccount();
					withdrawFrom.setName(withdrawFromId);
					Account depositTo = new IncomeAccount();
					depositTo.setName(depositToId);

					trans = new Transaction(date, myUser, withdrawFrom, depositTo, amount);
					trans.setId(id);
				} catch (ClassInstantiationException e) {
					e.printStackTrace();
				}
				transactionList.add(trans);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// release all open resources to avoid unnecessary memory usage
			EmbeddedDerbyDatabase.close(rs, ps, null);
		}

		transactionsCounter = transactionList.size();
		return transactionList;
	}

	@Override
	public void printAll() {
		List<Transaction> transactionList = getAll();
		for (Transaction trans : transactionList) {
			System.out.println(String.format("| ID: %-5d %s", trans.getId(), trans));
		}
	}

	@Override
	public int save(Transaction t) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (t != null) {
			try {
				ps = EmbeddedDerbyDatabase.conn.prepareStatement(
						"insert into transactions (date, myUser, amount, withdrawFromId, depositToId) "
								+ "values (?, ?, ?, ?, ?)",
						PreparedStatement.RETURN_GENERATED_KEYS);

				ps.setString(1, t.toStringDate());
				ps.setString(2, t.getUser());
				ps.setDouble(3, t.getAmount());
				ps.setInt(4, t.getWithdrawFrom().getId());
				ps.setInt(5, t.getDepositTo().getId());
				ps.executeUpdate();

				rs = ps.getGeneratedKeys(); // will return the ID in ID_COLUMN
				if (rs.next()) {
					int number = rs.getInt(1);
					return number;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// release all open resources to avoid unnecessary memory usage
				EmbeddedDerbyDatabase.close(rs, ps, null);
			}
		}
		return -1;
	}

	@Override
	public void update(Integer id, Transaction t) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = EmbeddedDerbyDatabase.conn.prepareStatement(
					"update transactions set date=?, myUser=?, amount=?, withdrawFromId=?, depositToId=? WHERE tid=?");

			ps.setString(1, t.toStringDate());
			ps.setString(2, t.getUser());
			ps.setDouble(3, t.getAmount());
			ps.setInt(4, t.getWithdrawFrom().getId());
			ps.setInt(5, t.getDepositTo().getId());
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
			ps = EmbeddedDerbyDatabase.conn.prepareStatement("DELETE FROM transactions WHERE tid=?");

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
