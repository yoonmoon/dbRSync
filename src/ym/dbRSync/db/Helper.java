package ym.dbRSync.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Helper {
	
	public static PreparedStatement assignParams(PreparedStatement pstmt, Row row) throws SQLException {
		
		for (int i = 0; i < row.getColumnCount(); i++) {
			//System.out.println(i+" COL/VAL : " + row.columns.get(i)	+ "/" + "\""+row.values.get(i)+"\"");
			int jType = row.jTypes[i];
			if (row.values.get(i)==null) {
				//System.out.println(" 	---NULL:"+jType);
				pstmt.setNull(i+1, jType);
			} else {
				switch (jType) {
					case java.sql.Types.INTEGER: pstmt.setInt(i + 1, Integer.valueOf(row.values.get(i)));
					default : pstmt.setString(i + 1, row.values.get(i).trim());
				}
			}
		}
		return pstmt;
	}
}
