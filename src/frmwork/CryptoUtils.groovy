package frmwork;

// https://mvnrepository.com/artifact/commons-codec/commons-codec
//@Grapes(
//	@Grab(group='commons-codec', module='commons-codec', version='1.10')
//)



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
/**
 * Funcionalidad que ejecuta la encriptacion y desencriptacion de archivos
 * 
 * En jenkins como credencials se tendr� la credencial por proyecto que almacena la clave
 * y la clave privada de estos modulos JK 
 * @author jpalm
 *
 */
public class CryptoUtils {
	
	private static 	String credentialPrivateKey = "PRIVADA"
	private static 	String privatekey = "PRIVADA";
	
	public setPrivateKey(String credentialId) {
		def cred = new frmwork.Credentials();
		privatekey = cred.getText(credentialId);
	}
	public CryptoUtils() {
		
	}
	public  byte[] MD5(String val) {
		 MessageDigest md;
		 byte[] messageDigest = null;
		try {
			md = MessageDigest.getInstance("MD5");
			messageDigest = md.digest(val.getBytes()); // "UTF-8"
			return messageDigest;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return messageDigest;
		}
	}
	 
	public String encrypt(String value, String publicKey, String privateKey) {
	    try {
			
	        IvParameterSpec iv = new IvParameterSpec(MD5(privateKey));
	        SecretKeySpec skeySpec = new SecretKeySpec(MD5(publicKey), "AES");
	 
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
	 
	        byte[] encrypted = cipher.doFinal(value.getBytes());
	        return Base64.encodeBase64String(encrypted);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}
	
	public String decrypt(String encrypted, String credentialId) {
		def cred = new frmwork.Credentials();
		String publicKey = cred.getText(credentialId);
		setPrivateKey(credentialPrivateKey);
		
		return decrypt( encrypted,  publicKey, privatekey);
	}
	public String decrypt(String encrypted, String publicKey, String privateKey)  {
	   try {
	        IvParameterSpec iv = new IvParameterSpec(MD5(privateKey));
	        SecretKeySpec skeySpec = new SecretKeySpec(MD5(publicKey), "AES");
	 
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	        byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
	 
	        return new String(original);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	 
	    return null;
	}

}
