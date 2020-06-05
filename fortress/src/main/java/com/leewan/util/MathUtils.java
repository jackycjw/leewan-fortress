package com.leewan.util;

import java.nio.ByteBuffer;

public class MathUtils {

	public static Integer parseInt(String n) {
		if(!StringUtils.hasLength(n)) {
			return null;
		}
		return Integer.parseInt(n.trim());
	}
	
	public static Long parseLong(String n) {
		if(!StringUtils.hasLength(n)) {
			return null;
		}
		return Long.parseLong(n.trim());
	}
	
	public static Double parseDouble(String n) {
		if(!StringUtils.hasLength(n)) {
			return null;
		}
		return Double.parseDouble(n.trim());
	}
	
	
	public static byte[] longToBytes(long l) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(l);
		return buffer.array();
	}
	
	public static int bytesToInteger(byte[] bs) {
		ByteBuffer buffer = ByteBuffer.wrap(bs);
		return buffer.getInt();
	}
	
	public static byte[] integerToBytes(int l) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(l);
		return buffer.array();
	}
	
	public static long bytesToLong(byte[] bs) {
		ByteBuffer buffer = ByteBuffer.wrap(bs);
		return buffer.getLong();
	}
	
	public static void main(String[] args) {
		System.out.println(bytesToLong(new byte[] {-128,0,0,0,0,0,0,0}));
		System.out.println(Long.MIN_VALUE);
	}
	
	
	
}
