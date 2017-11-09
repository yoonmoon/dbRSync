import java.sql.Connection;
import java.util.Date;

import org.json.JSONObject;

import ym.dbRSync.db.MultiRowSyncer;
import ym.dbRSync.db.JdbcConnManager;
import ym.dbRSync.security.SecureStore;


public class Test {

	/**
	 * This class is for testing code purpose.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		System.out.println("Testing a AES encryption...");
		
		//String key = ym.dbRSync.security.SecureStore.generateKey4AES();
		String key = "S5AFbxENhmkflxQm0kzbpQ==";
		//String encodedIV = ym.dbRSync.security.SecureStore.generateIV4AES();
		String encodedIV = "/SuE/MKToI80TmlqW4jW6xnGJpvG8Xl/uHK7gHJ2JFMoKsn2fgzIdu/oCMbK1HNOwo1WlQOxf12xbW9ckBWl8fymODTScaYpysCa0+1Ala7TWBFU/ShF/M03dRDalSNG";
		String aad = "ym.dbRSync.security.SecureStore";
		String encryptedMessage = "AjZ8IU0vO3FWEeF7xcLZ5t2qzpVdITT2";
		
		System.out.println("aad="+aad);
		System.out.println("key="+key);
		System.out.println("iv="+encodedIV);
		System.out.println("encrypted="+encryptedMessage);
		
		//byte[] encryptedMessage = SecureStore.aesEncrypt("message", key, encodedIV, aad.getBytes());
		//System.out.println("encryptedMessage="+Base64.getEncoder().encodeToString(encryptedMessage));
		//byte[] message = SecureStore.aesDecrypt(encryptedMessage, key, encodedIV, aad.getBytes());
		byte[] passwd = SecureStore.aesDecrypt(encryptedMessage, key, encodedIV, aad.getBytes());
		//System.out.println("Message = "+new String(passwd));
		
		try {
			String url = "jdbc:db2://localhost:50000/SAMPLE";
			Connection sourceConn = JdbcConnManager.getConnection(url, "db2admin", new String(passwd));
			System.out.println((new Date())+" sourceConn - "+sourceConn.getMetaData().getDatabaseProductName());
			
			url = "jdbc:db2://localhost:50000/TEST";
			Connection targetConn = JdbcConnManager.getConnection(url, "db2admin", new String(passwd));			
			System.out.println((new Date())+" targetConn - "+targetConn.getMetaData().getDatabaseProductName());

			java.sql.Timestamp startTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
			//int returnCode = Db2RowsSyncer.executeSync(sourceConn, targetConn, "select * from gram9.employee", "TMP.EMPLOYEE", new Integer(1), "RMON_DBID", startTimestamp, "UPDATED");
			int returnCode = MultiRowSyncer.executeSync(MultiRowSyncer.INSERT_CMD, sourceConn, targetConn, "select * from gram9.employee", "TMP.EMPLOYEE", "TMP.EMPLOYEE_RMTMP", null, null, null, null, null);
			
			System.out.println("returnCode="+returnCode);
			sourceConn.close();
			targetConn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JSONObject json = new JSONObject("{\"message\":\"MultiRowSync completed.\"}");
		System.out.println(json.toString());
	}
}
