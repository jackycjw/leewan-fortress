package com.leewan.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class StringUtils {
	
	public static String DEFAULT_CHARSET = "UTF-8";

	public static boolean hasLength(String string) {
		return string != null && string.trim().length() > 0;
	}
	
	public static String getString(byte[] bs) {
		return getString(bs, DEFAULT_CHARSET);
	}
	
	public static String getString(byte[] bs, String charset) {
		try {
			return new String(bs, charset);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static String getFromSteam(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bs = new byte[1024];
		int len = 0;
		while((len = in.read(bs)) > 0) {
			out.write(bs, 0, len);
		}
		return new String(out.toByteArray(), DEFAULT_CHARSET);
	}
	
	public static String getFromFile(File file) throws IOException {
		return getFromSteam(new FileInputStream(file));
	}
	
	
	
	public static byte[] getBytes(String content) {
		try {
			return content.getBytes(DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
