import java.util.Base64;

import ym.dbRSync.db.JdbcConnManager;
import ym.dbRSync.security.SecureStore;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello World!");
		System.out.println("Testing a AES encryption...");
		
		String key = ym.dbRSync.security.SecureStore.generateKey4AES();
		String encodedIV = ym.dbRSync.security.SecureStore.generateIV4AES();
		String aad = "ym.dbRSync.security.SecureStore";
		
		System.out.println("key="+key);
		System.out.println("iv="+encodedIV);
		System.out.println("aad="+aad);
		
		byte[] encryptedMessage = SecureStore.aesEncrypt("Hello world! This#$%^&*&^% is testing program.", key, encodedIV, aad.getBytes());
		Base64.getEncoder().encodeToString(encryptedMessage);
		System.out.println("encryptedMessage="+Base64.getEncoder().encodeToString(encryptedMessage));
		byte[] message = SecureStore.aesDecrypt(encryptedMessage, key, encodedIV, aad.getBytes());
		System.out.println("Message = "+new String(message));
		
		try {
			JdbcConnManager jConn = new JdbcConnManager("Hello");
			jConn.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
