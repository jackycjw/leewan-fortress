package com.leewan.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jcraft.jsch.Channel;




public class IOUtils {

	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] bs = new byte[1024];
		int len = 0;
		while((len = in.read(bs)) > 0) {
			out.write(bs, 0, len);
		}
	}
	
	public static String getCurrentPath() {
		return System.getProperty("user.dir");
	}
	
	public static File getCurrentPathFile(String name) {
		String currentPath = getCurrentPath();
		return new File(currentPath, name);
	}
	
	public static void close(Closeable closeable) {
		try {
			if(closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public static void close(Channel channel) {
		try {
			if(channel != null) {
				channel.disconnect();;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	static String charset = "utf-8";
	
	public static String streamToContent(InputStream in) throws IOException {
		byte[] bs = new byte[1024*10];
		int len = 0;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while((len = in.read(bs)) > 0) {
			out.write(bs, 0, len);
			System.out.println(new String(bs, 0, len));
		}
		return new String(bs, 0, len,charset);
	}
	
	
	
	public static void close(AutoCloseable closeable) {
		try {
			if(closeable != null) {
				closeable.close();
			}
		} catch (Exception e) {
		}
	}
	
	public static void copy(File from, File dest) throws Exception {
		InputStream in = new FileInputStream(from);
		byte[] bs = new byte[in.available()];
		in.read(bs);
		
		OutputStream out = new FileOutputStream(dest, true);
		out.write(bs);
		out.close();
		in.close();
	}
}
