package common.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
* RSA公钥/私钥/签名工具包
* 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式
* 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密
* 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
*/
public class RSAUtils {

	/** */
	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/** */
	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/** */
	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/** */
	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/** */
	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;
	//private static final int MAX_ENCRYPT_BLOCK = 245;

	/** */
	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;
	//private static final int MAX_DECRYPT_BLOCK = 256;

	/** */
	/**
	 * RSA 位数 如果采用2048 上面最大加密和最大解密则须填写:  245 256
	 */
	private static final int INITIALIZE_LENGTH = 1024;
	//private static final int INITIALIZE_LENGTH = 2048;

	/** */
	/**
	 * <p>
	 * 生成密钥对(公钥和私钥)
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> genKeyPair() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(INITIALIZE_LENGTH);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Object> keyMap = new HashMap<String, Object>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/** */
	/**
	 * <p>
	 * 用私钥对信息生成数字签名
	 * </p>
	 * 
	 * @param data
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return Base64.encodeBase64String(signature.sign());
	}

	/** */
	/**
	 * <p>
	 * 校验数字签名
	 * </p>
	 * 
	 * @param data
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @param sign
	 *            数字签名
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}

	/** */
	/**
	 * <P>
	 * 私钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/** */
	/**
	 * <p>
	 * 公钥解密
	 * </p>
	 * 
	 * @param encryptedData
	 *            已加密数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicK);
		int inputLen = encryptedData.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/** */
	/**
	 * <p>
	 * 公钥加密
	 * </p>
	 * 
	 * @param data
	 *            源数据
	 * @param publicKey
	 *            公钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/** */
	/**
	 * <p>
	 * 私钥加密
	 * </p>
	 * 
	 * @param data
	 *            源数据
	 * @param privateKey
	 *            私钥(BASE64编码)
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateK);
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();
		out.close();
		return encryptedData;
	}

	/** */
	/**
	 * <p>
	 * 获取私钥
	 * </p>
	 * 
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/** */
	/**
	 * <p>
	 * 获取公钥
	 * </p>
	 * 
	 * @param keyMap
	 *            密钥对
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * java端公钥加密
	 */
	public static String encryptedDataOnJava(String data, String PUBLICKEY) {
		try {
			data = Base64.encodeBase64String(encryptByPublicKey(data.getBytes(), PUBLICKEY));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * java端私钥解密
	 */
	public static String decryptDataOnJava(String data, String PRIVATEKEY) {
		String temp = "";
		try {
			byte[] rs = Base64.decodeBase64(data);
			temp = new String(RSAUtils.decryptByPrivateKey(rs, PRIVATEKEY),"UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	public static void main(String[] args) throws  Exception{
		Map<String, Object> map = genKeyPair();
		//String publicKey = getPublicKey(map);
		//String privateKey = getPrivateKey(map);
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWFgDcEHbzVmfv+qUwRJDF8/YPKTTLYPQPcBFdbPtM88nEhgOneAmNxXC+Fl8bqEr7zwygz4431DXYG7pSMAb1iFPoKS1OSeZVY9enhjkTtMUIW2+gbLXa6HGF0WWKURyImA+kcZpaChJ5Jg5OuUEULudsrXdgZG4xY53OcTg8HQIDAQAB";
		String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJYWANwQdvNWZ+/6pTBEkMXz9g8pNMtg9A9wEV1s+0zzycSGA6d4CY3FcL4WXxuoSvvPDKDPjjfUNdgbulIwBvWIU+gpLU5J5lVj16eGORO0xQhbb6BstdrocYXRZYpRHIiYD6RxmloKEnkmDk65QRQu52ytd2BkbjFjnc5xODwdAgMBAAECgYEAifNjiuKfUv8ivdN2Li13nFum2TyYyC8vZVkuvN7FBXA3DqnXhgCxV1wZSr8pYDSJ53XiN5VPNUWa0C58TbrTSUUqzwusK3eL7rf4u5N5q0zCIjJLYvBxAWZVP/qhYtVT+dy9jsrj8dvouZi6Zic2bppQCeM9jKVvzaKC4SwI8gkCQQDatCdz9fVYg1K2/j3YCQleQfglZVy2t/Xkp/eLhU+OtkXHDCoICnrJEYlFAXLdTbJUrWbZsJwORih9sgzSpWL7AkEAr648Nrpsd74mliP/tvzrnlYNxRcnNmD1VBA+6ZtTTaDV1v3Ets/YdHDcsqJC6QMbePIfpvOrwF/L07iXCE7xxwJBANBmFKYh/E0AriPjdijSy5N+xydTRBp0hyIAkBjpyhqSQ0FdrPuGiOPqCuSvcbXBYKnhpfzPlpTfc5v6us9Ke1sCQDtVRzTcpYGTkQioQ9cTeKhTO/dSqIWvHWw0yGqxcOMI7O/daTCs5df9viv+rUZxLDge3h3OsjUYpYhuEWRW9UUCQQC/rNzXesUAD4v2vdjGerU5tLZAgl+QVuXvTYYATL7jmb6LeSiMv3Y45d5gInbAYlgC7JJCTSyjqtDquKt6l2XB";
		System.out.println(publicKey);
		System.out.println(privateKey);

		 String test = "c36014e4ea3564767fb03b08a1eac2d1";
		 System.out.println("加密前："+test);
		 byte[] resPu = encryptByPublicKey(test.getBytes(), publicKey);
		 String tt = Base64.encodeBase64String(resPu);
		 System.out.println("公钥加密后："+tt);
		 
		 byte[] resPr = decryptByPrivateKey(Base64.decodeBase64(tt), privateKey);
		 System.out.println("解密后："+new String(resPr));
		 
		 String privateKey2 = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMiDrA+AuHYBy07mdgnA59gbYHkRYkyjIp0/qkFuW+QdtKetK2UYYbz3mN5hsPWO2GjloplGCVeXneJeZpAeodJg/nidKovpeNDcywaJjj4o8FOu+j94NQ3R9+/J2o3DYeNMvEMkRl1dvKkZWdipDHR0km5dBjr9VHdaT+YpqJHhAgMBAAECgYBHvZSaF1voHMw49ovVAYfx8hmaN3YMJXIFAPpRXsLR4y73ryWPjiGqEoxHRHyj3u/e2ApM/JnrFPwQOeZIOi17DW69cJuFfC4ec3S5ZJ4WONg+oWk+YEyoOYYDpV629f45zjx6gPMlFckRsJBnfsFmNLETD72kjPn3n096/xH4gQJBAPIE7815QKDern9gBaC4OuT3H2RXWDVR2+2mscr3figPATLkEQwAQRec06KAB4+WPKxWqTVtNn/ebis5HN0ld60CQQDUGPI9jv1KT0DUhKEUmaDXr5Scpuxlyo8EvZPQ60+qHYTyCQMLooo6RBYqwL+6P7/h1SVPPk859jQ6zXskt5mFAkBBcARG0lofJRaZaiUbZ7TE4+yg/NzkzdVhVIkOEA0UV7pDQFc7n3X6JHU0otlogX62OlhWEcmCmWnAVhjv0omhAkEAwEWZHCsK/RlZiQ5uxDD8+WonS9eayDBXdJGzdZpMDshR4+Q9iYegzW85tSok5N+zwDMsbmjrA31pHcF3F/MEzQJBALl5L8CZoAkmMZ2dcYDzv06hwCEBUOy6MHSI8zdpTT0t54NAk2nLv75ZsTErPDIMcBZo+fbEWRNqZcOHGzSToVo=";
		 String test2 = "yCmpo6dxUCq2H7ETCUROqkN58/qfjt6rO+4yo2HkiwnM2FVS/BCumaEJXRerb4UFH+jKstLQNFPOT36c/dT0gryP5AwjjAaqumsWgFKXcUL7eldXBvKQyozxJQEJO4S5/itV/3FVqKSgrGrnARO/rBVdaTU+a0ELFW2Y9vJKkps="; 
		 byte[] resPr2 = decryptByPrivateKey(Base64.decodeBase64(test2), privateKey2);
		 System.out.println("解密后2："+new String(resPr2));
	}
}
