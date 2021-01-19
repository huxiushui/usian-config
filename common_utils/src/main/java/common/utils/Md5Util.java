package common.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	
	public static void main(String[] args) {
		System.out.println(Base64.encodeBase64String(getMd532("{\"data\":{\"custToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJsb2dpbmVkIiwiTGFzdExvZ2luVGltZSI6ImszV3RUY3Yzd3l2eHlubG51U3IvdVE9PSIsIlVzZXJOYW1lIjoiV0UyYVZrbFlkT0R2TERyQ055NW1WZz09IiwiVXNlcklkIjoiSTJaR1RxakNBNHR0V2lVcVJ6U1dCdz09IiwiVXNlclNlcSI6IkprRzN5VEw3NGdsY0N5MHdZTUQzQ2c9PSIsInNvdXJjZSI6IkIzMHNUdDJMeFNLQ2tGS1RRd1NmRGc9PSIsImV4cCI6MTYwNTE2NjMzOSwiaWF0IjoxNjA0OTA2ODM5fQ.beDEIzM6oTXC1HCRrb_FtoJZFxQu8w1w2ZU3jRPoh6Y\"},\"status\":\"200000\",\"message\":\"操作成功\"}").getBytes()));
	}
	
	/**
	 * 
	 * 方法: getMd532 <br>
	 * 描述: 32位md5加密 <br>
	 * @param plainText
	 * @return
	 */
	public static String getMd532(String plainText) {  
	    try {  
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        md.update(plainText.getBytes());  
	        byte b[] = md.digest();  
	        int i;
	        StringBuffer buf = new StringBuffer("");  
	        for (int offset = 0; offset < b.length; offset++) {  
	            i = b[offset];  
	            if (i < 0)  
	                i += 256;  
	            if (i < 16)  
	                buf.append("0");  
	            buf.append(Integer.toHexString(i));  
	        }  
	        //32位加密  
	        return buf.toString();  
	        // 16位的加密  
	        //return buf.toString().substring(8, 24);  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	}  
	/**
	 * 
	 * 方法: getMd516 <br>
	 * 描述: 16位MD5加密 <br>
	 * @param plainText
	 * @return
	 */
	public static String getMd516(String plainText) {  
	    try {  
	        MessageDigest md = MessageDigest.getInstance("MD5");  
	        md.update(plainText.getBytes());  
	        byte b[] = md.digest();  
	        int i;  
	        StringBuffer buf = new StringBuffer("");  
	        for (int offset = 0; offset < b.length; offset++) {  
	            i = b[offset];  
	            if (i < 0)  
	                i += 256;  
	            if (i < 16)  
	                buf.append("0");  
	            buf.append(Integer.toHexString(i));  
	        }  
	        //32位加密  
	       // return buf.toString();  
	        // 16位的加密  
	        return buf.toString().substring(8, 24);  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	}
}
