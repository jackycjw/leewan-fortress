package com.leewan.util;

import java.util.UUID;

public class UID {

	public static String getUUID() {
		String string = UUID.randomUUID().toString();
		return string.replaceAll("-", "");
	}
}
