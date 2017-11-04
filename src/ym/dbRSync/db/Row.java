package ym.dbRSync.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class Row {
	public ArrayList<String> columns = new ArrayList<String>();
	public ArrayList<String> types = new ArrayList<String>();
	public ArrayList<String> values = new ArrayList<String>();
	public int[] jTypes;
	
	
	public Row(ResultSet rs) throws SQLException {

		ResultSetMetaData rsmd = rs.getMetaData();
		jTypes = new int[rsmd.getColumnCount()];
		//jTypes[0] = -1; // First array cell(0) is not used;

		
		columns = new ArrayList<String>();
		types = new ArrayList<String>();
		values = new ArrayList<String>();
		
		int cc = rsmd.getColumnCount();
		for (int i=1; i <= rsmd.getColumnCount(); i++) {
			columns.add(rsmd.getColumnName(i));
			types.add(rsmd.getColumnTypeName(i));
			values.add(rs.getString(i));
			jTypes[i-1] = rsmd.getColumnType(i);
		}
	}
	
	public int getColumnCount(){
		return columns.size();
	}
	
	public String toColumnNames() {
		String columnNames = "";
		for (int c = 0; c < columns.size(); c++) {
			columnNames +=columns.get(c);
			if (c < (columns.size() - 1)) {
				columnNames += ",";
			}
		}
		
		return columnNames;
	}
	
	public String toColumnValues() {
		String columnValues = "";
		for (int c = 0; c < columns.size(); c++) {
			columnValues += values.get(c);
			if (c < (values.size() - 1)) {
				columnValues += ",";
			}
		}
		
		return columnValues;
	}
	
	public String toColumnNamesDoubleQuotes() {
		String columnNames = "";
		for (int c = 0; c < columns.size(); c++) {
			columnNames += "\""+columns.get(c)+"\"";
			if (c < (columns.size() - 1)) {
				columnNames += ",";
			}
		}
		
		return columnNames;
	}
	
	public String toParams() {
		String params = "";
		for (int c = 0; c < columns.size(); c++) {
			params += "?";
			if (c < (columns.size() - 1)) {
				params += ",";
			}
		}
		
		return params;
	}
}


