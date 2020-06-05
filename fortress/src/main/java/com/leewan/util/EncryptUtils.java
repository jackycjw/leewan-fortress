package com.leewan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptUtils {

	
	public static String encrypt(String str) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			byte[] encode = Base64.getEncoder().encode(md5.digest(str.getBytes("utf-8")));
		    return new String(encode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
}
