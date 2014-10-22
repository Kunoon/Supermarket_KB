package com.koobest.m.authenticate.toolkit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5ResultBuilder {
	private static MessageDigest md = null;

	static char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
	'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static String getMD5(byte[] source) {
		// String result=null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		if (md != null) {
			md.update(source);
			byte[] temp = md.digest();
			char str[] = new char[16 * 2];
			byte byte0;
			for (int i = 0, k = 0; i < 16; i++) {
				byte0 = temp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		}
		return null;
	}
}
