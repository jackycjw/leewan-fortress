package com.leewan.util;

import com.leewan.bean.User;

public class UserUtils {

	private static ThreadLocal<User> localUser = new ThreadLocal<>();
	
	public static User getUser() {
		return localUser.get();
	}
	
	public static void setUser(User user) {
		localUser.set(user);
	}
	
	public static void clear() {
		localUser.remove();
	}
}
