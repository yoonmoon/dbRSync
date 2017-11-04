package ym.dbRSync.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

/**
 * Database Connection Manager for JDBC connections.
 * This program create different type of a vendor Database connection for JDBC base on url.
 * 
 * @author	Yoon Moon
 * @version	0.1
 *
 */
public class JdbcConnManager {

	static String URL_DELIMITER = " ";
	static int DEFAULT_ISOLATION = Connection.TRANSACTION_READ_UNCOMMITTED; 

	public static Connection getConnection(String url, String user, String passwd) {
		Connection conn = null;
		
		// MS SQL server JDBC driver
		if (url.contains("sqlserver")) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			} catch (ClassNotFoundException cnfe) {
				try {
					throw new SQLException("Can't find class for driver: com.microsoft.sqlserver.jdbc.SQLServerDriver");
				} catch (SQLException e) {e.printStackTrace();}
			}

			try {
				conn = DriverManager.getConnection(url, user, passwd);
				conn.setTransactionIsolation(DEFAULT_ISOLATION);
				conn.setReadOnly(true);
			} catch (SQLException e) {
				System.out.println((new Date()).toString()+" - Connection error = sqlUrl[0]");
				e.printStackTrace();
			}

		} else if (url.contains("jdbc:db2")) {			
			try {
				Class.forName("com.ibm.db2.jcc.DB2Driver");
				conn = DriverManager.getConnection(url, user, passwd);
				conn.setTransactionIsolation(DEFAULT_ISOLATION);
				//conn.setReadOnly(true);
			} catch (ClassNotFoundException cnfe) {
				try {
					throw new SQLException("Can't find class for driver: com.ibm.db2.jcc.DB2Driver");
				} catch (SQLException e) {e.printStackTrace();}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else if (url.contains("jdbc:sap")) {
			try {
				Class.forName("com.sap.db.jdbc.Driver");
				conn = DriverManager.getConnection(url, user, passwd);
				conn.setReadOnly(true);

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

		} else {
			try {
				throw new ClassNotFoundException("Can't find class for url : "+url);
			} catch (Exception e) {e.printStackTrace();}
		}
		return conn;
	}
}
