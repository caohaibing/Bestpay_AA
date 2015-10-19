package com.bestpay_aa.net;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Helper {

	
	/**
	 * MD5加密
	 * 
	 * @param input
	 * @return String
	 */
	public static String generateMD5(String input) {
		String str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(input.getBytes());
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
			str = buf.toString().toUpperCase();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}
		return str;
	}
	private final static String[] hexDigits = {
	      "0", "1", "2", "3", "4", "5", "6", "7", 
	      "8", "9", "a", "b", "c", "d", "e", "f"}; 
	public static String byteArrayToHexString(byte[] b){
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++){
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}
	private static String byteToHexString(byte b){
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}
	public static String MD5Encode(String origin){
		String resultString = null;
			try {
				resultString=new String(origin);
				MessageDigest md = MessageDigest.getInstance("MD5");
				resultString=byteArrayToHexString(md.digest(resultString.getBytes("utf-8")));
			}catch (Exception ex){}
			return resultString;
		}


}
