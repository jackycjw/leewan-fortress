package com.leewan.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.leewan.util.StringUtils;





public class ConfigManager {

	private static Properties properties;
	
	public static final String ENV = "pro";
	
	public static final String DEFAULT_SEPARATOR = ",";
	
	public static void init(String path) {
		
		try {
			File file = new File(path);
			InputStream in = new FileInputStream(file);
			properties = new Properties();
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static String getString(String key) {
		return properties.getProperty(key);
	}
	
	public static byte[] getBytes(String key) {
		String string = getString(key);
		try {
			return string.getBytes(StringUtils.DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static int getInt(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
	
	public static long getLong(String key) {
		return Long.parseLong(properties.getProperty(key));
	}
	
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(properties.getProperty(key));
	}
	
	public static Set<String> getSet(String key){
		return getSet(key, DEFAULT_SEPARATOR);
	}
	
	
	public static Set<String> getSet(String key, String separator){
		Set<String> set = new HashSet<>();
		String property = properties.getProperty(key);
		if(StringUtils.hasLength(property)) {
			String[] split = property.split(separator);
			if(split != null) {
				for(String item : split) {
					set.add(item.trim());
				}
			}
		}
		return set;
	}
	
}
