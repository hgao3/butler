package com.butler.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class EmbeddedDerbyDatabase {

	public static Connection conn;

	public void createDatabaseConnection() throws SQLException, ClassNotFoundException {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		Class.forName(driver);

		Properties props = new Properties();
		props.put("user", "hgao");
		props.put("password", "password");

		String url = "jdbc:derby:derbyDB;create=true";
		conn = DriverManager.getConnection(url, props);
		// conn.setAutoCommit(false);
	}

	public void dropTable() {
		Statement s = null;
		try {
			s = conn.createStatement();
			// delete the table
			s.execute("drop table transactions");
			s.execute("drop table accounts");

			// conn.commit();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			// release all open resources to avoid unnecessary memory usage
			close(null, s, null);
		}
		System.out.println("Committed the drop table");

	}

	public void shutDownDatabaseConnection() {
		try {
			// the shutdown=true attribute shuts down Derby
			DriverManager.getConnection("jdbc:derby:;shutdown=true");

			// To shut down a specific database only, but keep the
			// engine running (for example for connecting to other
			// databases), specify a database in the connection URL:
			// DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
		} catch (SQLException se) {
			if (((se.getErrorCode() == 50000) && ("XJ015".equals(se.getSQLState())))) {
				System.out.println("Derby database shut down normally");
			} else {
				System.err.println("Derby database did not shut down normally");
			}
		}
		close(null, null, conn);
	}

	public void createTable() {
		Statement s = null;
		ResultSet rs = null;
		try {
			/*
			 * Creating a statement object that we can use for running various SQL
			 * statements commands against the database.
			 */
			s = conn.createStatement();

			// Create tables accounts
			s.execute("CREATE TABLE accounts("
					+ "aid int not null GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), "
					+ "type VARCHAR(255) not null, " + "name VARCHAR(255) not null, " + "owner VARCHAR(255) not null, "
					+ "amount DOUBLE, " + "category VARCHAR(255) not null," + "primary key (aid))");

			// Create tables transactions
			s.execute("CREATE TABLE transactions("
					+ "tid int not null GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), "
					+ "date VARCHAR(255) not null, " + "myUser VARCHAR(255) not null, " + "amount DOUBLE, "
					+ "withdrawFromId int not null, " + "depositToId int not null, " + "primary key (tid), "
					+ "foreign key (withdrawFromId) references accounts(aid),"
					+ "foreign key (depositToId) references accounts(aid))");
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			// release all open resources to avoid unnecessary memory usage
			close(rs, s, null);
		}
	}

	public void preloadAccountData() {
		Statement s = null;
		ResultSet rs = null;
		try {
			/*
			 * Creating a statement object that we can use for running various SQL
			 * statements commands against the database.
			 */
			s = conn.createStatement();

			// Insert data to tables accounts
			s.execute("insert into accounts (type, name, owner, amount, category) values "
					+ "('income','Citi Bank Checking','Hugh',3000.00,'Checking Account'), "
					+ "('income','Citi Bank Saving','Hugh',3000.00,'Saving Account'), "
					+ "('income','Wallet','Hugh',100.00,'Cash'), "
					+ "('expense','Whole Foods Market','Hugh',0,'Groceries'), " + "('expense','Rent','Hugh',0,'Rent'), "
					+ "('expense','Pizza Hut','Hugh',0,'Dining Out')");
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			// release all open resources to avoid unnecessary memory usage
			close(rs, s, null);
		}
	}

	public void preloadTransactionsData() {
		Statement s = null;
		ResultSet rs = null;
		try {
			/*
			 * Creating a statement object that we can use for running various SQL
			 * statements commands against the database.
			 */
			s = conn.createStatement();

			// Insert data to tables transactions
			s.execute("insert into transactions (date, myUser,amount,withdrawFromId,depositToId) values "
					+ "('05/18/2019','Hugh',60.85,2,5), " + "('05/19/2019','Hugh',1500,0,4), "
					+ "('05/20/2019','Hugh',200,0,2), " + "('05/21/2019','Hugh',105.99,2,3), "
					+ "('05/22/2019','Hugh',10.99,2,5), " + "('05/23/2019','Hugh',20.57,2,5)");
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} finally {
			// release all open resources to avoid unnecessary memory usage
			close(rs, s, null);
		}
	}

	public static void close(ResultSet rs, Statement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();

			} catch (SQLException e) {
				System.out.println("The result set close() throw a exception: " + e.getMessage());
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				System.out.println("The statement close() throw a exception: " + e.getMessage());
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("The connection close() throw a exception: " + e.getMessage());
			}
		}

	}
}
