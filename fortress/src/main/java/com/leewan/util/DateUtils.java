package com.leewan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {

	public static String getDateString(long time) {
		return getDateString(time, "yyyyMMdd");
	}
	
	public static String getNow() {
		return getNow("yyyyMMddHHmmss");
	}
	
	public static String getNow(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	
	public static String getNowTimeStamp() {
		return getNow("yyyyMMddHHmmss");
	}
	
	public static boolean isToday(long time) {
		String pattern = "yyyyMMdd";
		String dateString = getDateString(time, pattern);
		String now = getNow(pattern);
		return now.equals(dateString);
	}
	
	
	public static String getDateString(long time, String pattern) {
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static String getQuarter(long time) {
		Calendar instance = Calendar.getInstance();
		instance.setTimeInMillis(time);
		int min = instance.get(Calendar.MINUTE);
		min = min/15 * 15;
		instance.set(Calendar.MINUTE, min);
		return getDateString(instance.getTimeInMillis(),"yyyyMMddHHmm");
	}
	
	public static Date parse(String date, String pattern) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(date);
	}
	
	public static Date parse(String date) {
		String pattern = "yyyyMMdd";
		if(date.length() == 14) {
			pattern = "yyyyMMddHHmmss";
		} else if (date.length() == 12) {
			pattern = "yyyyMMddHHmm";
		} else if (date.length() == 10) {
			pattern = "yyyyMMddHH";
		} else if (date.length() == 8) {
			pattern = "yyyyMMdd";
		} else if (date.length() == 6) {
			pattern = "yyyyMM";
		} else if (date.length() == 4) {
			pattern = "yyyy";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
