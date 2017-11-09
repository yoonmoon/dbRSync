package ym.dbRSync.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class MultiRowSyncer {

	public static int INSERT_CMD = 10;
	public static int REPLACE_CMD = 20;
	public static int UPDATE_CMD = 30;
	public static int MERGE_CMD = 40;
	
	public static int READY = 0;
	public static int UPDATED = 1;
	public static int UNKNOWN = -1;
	public static int HOSTERROR = -2;
	public static int CONNECTED = 10;
	public static int CONNECTERROR = -10;
	public static int CLEARED = 15;
	public static int CLEARERROR = -15;
	public static int QUERIED = 20;
	public static int QUERYERROR = -20;
	public static int ROWRETRIEVING = 32;
	public static int ROWRETRIEVED = 30;
	public static int NOROW = -30;

	public static int executeSync(int cmd, Connection sourceConn, Connection targetConn, 
			String sourceSql, String targetTab, String tempTab, String histTab, Object extraVal1, String extraColName1, Object extraVal2, String extraColName2) {
		
		int status = READY;
		String message = null;
		int errorcode = 0;
		
		if (sourceConn != null && targetConn!=null) {
			try {
				status = CONNECTED;
				// NO Temporary Table Defined.
				if (tempTab==null || tempTab.equals("")) {
					tempTab = targetTab;
				} else {
					status = CLEARERROR;
					String deleteSql = "TRUNCATE "+tempTab+" IMMEDIATE";
					System.out.println((new Date()).toString()+" "+deleteSql);
					targetConn.createStatement().executeUpdate(deleteSql);
					status = CLEARED;
				}
				
				status = QUERYERROR;
				PreparedStatement sourcePStmt = sourceConn.prepareCall(sourceSql);
				ResultSet rs = sourcePStmt.executeQuery();
				status = QUERIED;

				// INSERTING INTO TARGET DATABASE
				PreparedStatement pStmt = null;
				Row row;
				int i = 0;
				String params = null;
				String columnNames = null;
				String insertSql = null;
				int columnCount = 0;
				int updatedRowCount = 0;
				targetConn.setAutoCommit(false);
				
				while (rs.next()) {
					i++;
					status = ROWRETRIEVING;
					row = new Row(rs);
					if (i == 1) {
						params = row.toParams();
						columnNames = row.toColumnNames();
						if (extraColName1!=null) {
							columnNames += ","+extraColName1; params += ",?";
						}
						if (extraColName2!=null) {
							columnNames += ","+extraColName2; params += ",?";
						}
						insertSql = "INSERT INTO " + tempTab + "(" + columnNames + ") VALUES(" + params
								+ ")";
						columnCount = row.getColumnCount();
					}
					if (i == 1) {
						pStmt = targetConn.prepareStatement(insertSql);
					}
					// System.out.println("   #DEBUG probe - row's column size : "+row.getColumnCount());
					// System.out.println("   #DEBUG probe - row's columns: "+columnNames);
					for (int i1 = 0; i1 < columnCount; i1++) {
						// System.out.println(i1+" COL/VAL : " + row.columns.get(i1) +
						// "/" + row.values.get(i1));
						pStmt.setString(i1 + 1, row.values.get(i1));
					}
					int nextColCount = 0;
					if (extraColName1!=null) {
						nextColCount++;
						pStmt.setObject(columnCount + nextColCount, extraVal1);
					}
					if (extraColName2!=null) {
						nextColCount++;
						pStmt.setObject(columnCount + nextColCount, extraVal2);
					}
					try {
						pStmt.addBatch();
						if (i % 10 == 0) {
							int[] numUpdates = pStmt.executeBatch();
							for (int n = 0; n < numUpdates.length; n++) {
								if (numUpdates[n] > 0)
									updatedRowCount += numUpdates[n];
							}
							targetConn.commit();
							pStmt.clearBatch();
						}
					} catch (SQLException e) {
						// pStmt.close();
						System.out.println((new Date()).toString()+ " INSERT error with : " + insertSql);
						System.out.println("  Problem Row columns : " + row.toColumnNames());
						System.out.println("  Problem Row values	 : " + row.toColumnValues());
						e.printStackTrace();
					}
				} rs.close(); sourcePStmt.close();
				
				if (!pStmt.isClosed()) {
					int[] numUpdates = pStmt.executeBatch();
					for (int n = 0; n < numUpdates.length; n++) {
						if (numUpdates[n] > 0)
							updatedRowCount += numUpdates[n];
					}
					pStmt.close();
				}
				
				try {
					// If Temporary table used then INSERT to target
					if (!targetTab.equals(tempTab)) {
						insertSql = "INSERT INTO "+targetTab+" SELECT * FROM "+tempTab;
						targetConn.createStatement().executeUpdate(insertSql);
					}
				} catch (SQLException e) {
					System.out.println((new Date()).toString()+" Insert from tempTab into targetTab Failed!");
					throw e;
				}				
				System.out.println((new Date()).toString()+" Row(s) Insert/Read : "+updatedRowCount+"/"+i);
				targetConn.setAutoCommit(true);

				if (status == ROWRETRIEVING) {
					status = UPDATED;
				} else {
					status = NOROW;
				}
			} catch (SQLException e) {
				errorcode = e.getErrorCode();
				message = e.getMessage();
				e.printStackTrace();
			}
		} else {
			status = CONNECTERROR;
			System.out.println("sourceConn is null!");
		}
		return status;
	}
}
