package ym.dbRSync.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager for JDBC connections.
 * This program create different type of a vendor Database connection for JDBC base on url.
 * 
 * @author	Yoon Moon
 * @version	0.1
 *
 */
public class JdbcConnManager {

	Connection conn;
	String url;

	public JdbcConnManager(String url) {
		this.url = url;
	}

	public Connection getConnection() throws Exception {
		Connection conn = null;
		
		// MS SQL server JDBC driver
		if (url.contains("sqlserver")) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException cnfe) {
				try {
					throw new SQLException("Can't find class for driver: ");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				conn = DriverManager.getConnection(url);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				conn.setReadOnly(true);

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else if (url.contains("jdbc:db2")) {
			String[] db2url = url.split(" ");
			
			try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				conn = DriverManager.getConnection(db2url[0],db2url[1],db2url[2]);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				//conn.setReadOnly(true);
			} catch (ClassNotFoundException cnfe) {
				try {
					throw new SQLException("Can't find class for driver: ");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else if (url.contains("jdbc:sap")) {
			String[] sapurl = url.split(" ");
			try {
				Class.forName("com.sap.db.jdbc.Driver");
				conn = DriverManager.getConnection(sapurl[0],sapurl[1],sapurl[2]);
				conn.setReadOnly(true);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			throw new ClassNotFoundException("Supported JDBC Class Not Found!");
		}
		return conn;
	}
}
