package com.leewan.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class CacheContainer {
	
	public static final long DEFUALT_ACTIVE_TIME = 24 * 60 * 60 * 1000;

	public static Map<Object, CacheItem> caches = new ConcurrentHashMap<>();
	
	
	public static void set(Object key, Object value, long activeTime) {
		CacheItem cacheItem = new CacheItem(value, activeTime);
		caches.put(key, cacheItem);
	}
	
	public static void set(Object key, Object value) {
		set(key, value, DEFUALT_ACTIVE_TIME);
	}
	
	public static Object get(Object key) {
		if(!caches.containsKey(key)) {
			return null;
		}
		
		CacheItem cacheItem = caches.get(key);
		if(cacheItem.isExpires()) {
			caches.remove(key);
			return null;
		} else {
			cacheItem.resetTime();
			return cacheItem.get();
		}
	}
	
	public static boolean contains(Object key) {
		if(!caches.containsKey(key)) {
			return false;
		}
		
		CacheItem cacheItem = caches.get(key);
		if(cacheItem.isExpires()) {
			caches.remove(key);
			return false;
		} else {
			cacheItem.resetTime();
			return true;
		}
	}
	
	static void clean() {
		List<Object> cleanKeys = new ArrayList<Object>();
		for(Entry<Object, CacheItem> entry : caches.entrySet()) {
			if(entry.getValue().isExpires()) {
				cleanKeys.add(entry.getKey());
			}
		}
		
		for(Object obj : cleanKeys) {
			caches.remove(obj);
		}
	}
	
	static {
		new CleanThread().start();
	}
}

class CleanThread extends Thread {
	
	public void run() {
		while(true) {
			try {
				CacheContainer.clean();
				Thread.sleep(30 * 60 * 1000);
			} catch (Exception e) {
			}
		}
	}
}


class CacheItem {
	private Object value;
	
	private long lastActiveTime = System.currentTimeMillis();
	
	//15分钟
	private long activeTime;
	
	
	
	public CacheItem(Object value, long activeTime) {
		super();
		this.value = value;
		this.activeTime = activeTime;
	}

	public void resetTime() {
		this.lastActiveTime = System.currentTimeMillis();
	}
	
	public boolean isExpires() {
		return System.currentTimeMillis() - lastActiveTime > this.activeTime;
	}
	
	public Object get() {
		return this.value;
	}
	
}