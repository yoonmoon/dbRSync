package ym.dbRSync.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author https://github.com/1MansiS/java_crypto/blob/master/cipher/SecuredGCMUsage.java
 * @author Yoon Moon
 * This class shows how to securely perform AES encryption in GCM mode, with 128 bits key size.
 */
public class SecureStore {

	public static int AES_KEY_SIZE = 128;
	public static int IV_SIZE = 96;
	public static int TAG_BIT_LENGTH = 128;
	public static String ALGO_TRANSFORMATION_STRING = "AES/GCM/PKCS5Padding";

	public static String generateKey4AES() {
		// Generating Key
		SecretKey aesKey = null;
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("AES"); // Specifying
																						// algorithm
																						// Key
			keygen.init(AES_KEY_SIZE); // Specifying Key size to be used
			aesKey = keygen.generateKey();
		} catch (NoSuchAlgorithmException noSuchAlgoExc) {
			System.out
					.println("Key being request is for AES algorithm, but this cryptographic algorithm is not available in the environment "
							+ noSuchAlgoExc);
			System.exit(1);
		}
		String encodedAesKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());
		return encodedAesKey;
	}

	/**
	 * @param message
	 * @param encodedAesKey
	 * @param encodedIV
	 * @param aadData
	 * @return
	 */
	public static byte[] aesEncrypt(String message, String encodedAesKey, String encodedIV, byte[] aadData) {
		Cipher c = null;
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedAesKey);
		// rebuild key using SecretKeySpec
		SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

		byte[] iv = Base64.getDecoder().decode(encodedIV);
		GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);

		try {
			c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
		} catch (NoSuchAlgorithmException noSuchAlgoExc) {
			System.out
					.println("Exception while encrypting. Algorithm being requested is not available in this environment "
							+ noSuchAlgoExc);
			System.exit(1);
		} catch (NoSuchPaddingException noSuchPaddingExc) {
			System.out
					.println("Exception while encrypting. Padding Scheme being requested is not available this environment "
							+ noSuchPaddingExc);
			System.exit(1);
		}

		try {
			c.init(Cipher.ENCRYPT_MODE, aesKey, gcmParamSpec, new SecureRandom());
		} catch (InvalidKeyException invalidKeyExc) {
			System.out
					.println("Exception while encrypting. Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized "
							+ invalidKeyExc);
			System.exit(1);
		} catch (InvalidAlgorithmParameterException invalidAlgoParamExc) {
			System.out
					.println("Exception while encrypting. Algorithm parameters being specified are not valid "
							+ invalidAlgoParamExc);
			System.exit(1);
		}

		try {
			c.updateAAD(aadData); // add AAD tag data before encrypting
		} catch (IllegalArgumentException illegalArgumentExc) {
			System.out.println("Exception thrown while encrypting. Byte array might be null "
					+ illegalArgumentExc);
			System.exit(1);
		} catch (IllegalStateException illegalStateExc) {
			System.out.println("Exception thrown while encrypting. CIpher is in an illegal state "
					+ illegalStateExc);
			System.exit(1);
		} catch (UnsupportedOperationException unsupportedExc) {
			System.out
					.println("Exception thrown while encrypting. Provider might not be supporting this method "
							+ unsupportedExc);
			System.exit(1);
		}

		byte[] cipherTextInByteArr = null;
		try {
			cipherTextInByteArr = c.doFinal(message.getBytes());
		} catch (IllegalBlockSizeException illegalBlockSizeExc) {
			System.out.println("Exception while encrypting, due to block size " + illegalBlockSizeExc);
			System.exit(1);
		} catch (BadPaddingException badPaddingExc) {
			System.out.println("Exception while encrypting, due to padding scheme " + badPaddingExc);
			System.exit(1);
		}

		return cipherTextInByteArr;
	}

	public static byte[] aesDecrypt(byte[] encryptedMessage, String encodedAesKey,
			String encodedIV, byte[] aadData) {
		Cipher c = null;
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedAesKey);
		// rebuild key using SecretKeySpec
		SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		byte[] iv = Base64.getDecoder().decode(encodedIV);
		GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
		
		try {
			c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
		} catch (NoSuchAlgorithmException noSuchAlgoExc) {
			System.out
					.println("Exception while decrypting. Algorithm being requested is not available in environment "
							+ noSuchAlgoExc);
			System.exit(1);
		} catch (NoSuchPaddingException noSuchAlgoExc) {
			System.out
					.println("Exception while decrypting. Padding scheme being requested is not available in environment "
							+ noSuchAlgoExc);
			System.exit(1);
		}

		try {
			c.init(Cipher.DECRYPT_MODE, aesKey, gcmParamSpec, new SecureRandom());
		} catch (InvalidKeyException invalidKeyExc) {
			System.out
					.println("Exception while encrypting. Key being used is not valid. It could be due to invalid encoding, wrong length or uninitialized "
							+ invalidKeyExc);
			System.exit(1);
		} catch (InvalidAlgorithmParameterException invalidParamSpecExc) {
			System.out.println("Exception while encrypting. Algorithm Param being used is not valid. "
					+ invalidParamSpecExc);
			System.exit(1);
		}

		try {
			c.updateAAD(aadData); // Add AAD details before decrypting
		} catch (IllegalArgumentException illegalArgumentExc) {
			System.out.println("Exception thrown while encrypting. Byte array might be null "
					+ illegalArgumentExc);
			System.exit(1);
		} catch (IllegalStateException illegalStateExc) {
			System.out.println("Exception thrown while encrypting. CIpher is in an illegal state "
					+ illegalStateExc);
			System.exit(1);
		}

		byte[] plainTextInByteArr = null;
		try {
			plainTextInByteArr = c.doFinal(encryptedMessage);
		} catch (IllegalBlockSizeException illegalBlockSizeExc) {
			System.out.println("Exception while decryption, due to block size " + illegalBlockSizeExc);
			System.exit(1);
		} catch (BadPaddingException badPaddingExc) {
			System.out.println("Exception while decryption, due to padding scheme " + badPaddingExc);
			System.exit(1);
		}

		return plainTextInByteArr;
	}

	public static String generateIV4AES() {
		// Generating IV
		byte iv[] = new byte[IV_SIZE];
		SecureRandom secRandom = new SecureRandom();
		secRandom.nextBytes(iv); // SecureRandom initialized using self-seeding
		String encodedIV = Base64.getEncoder().encodeToString(iv);
		return encodedIV;
	}
}
