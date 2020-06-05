package com.leewan.util.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class CacheContainer {

	/**
	 * 数据缓存
	 */
	private static Map<Object, CacheData> data = new ConcurrentHashMap<Object, CacheData>();
	
	/**
	 * 默认缓存时间
	 */
	private static final long defaultCacheTime = 10 * 60 * 1000;
	
	public static void save(Object key, Object obj) {
		save(key, obj, defaultCacheTime);
	}
	
	public static void save(Object key, Object obj, long expireTime) {
		data.put(key, new CacheData(obj, expireTime));
	}
	
	public static void delete(Object key) {
		data.remove(key);
	}
	
	public static Object get(Object key) {
		if(key == null) return null;
		CacheData cacheData = data.get(key);
		if(cacheData == null || cacheData.isExpire()) {
			return null;
		}
		return cacheData.get();
	}
	
	
	/*--------------------------------*/
	
	static {
		//十分钟执行一次清理
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for(Entry<Object, CacheData> item : data.entrySet()) {
					if(item.getValue().isExpire()) {
						data.remove(item.getKey());
					}
				}
				
			}
		}, new Date(), 600000);
	}
	
}

class CacheData {
	private Object data;
	
	/**
	 * 单位：毫秒
	 * 默认10分钟
	 */
	private long expireTime;
	
	private long saveTime = System.currentTimeMillis();
	
	private long updateTime = System.currentTimeMillis();
	

	public CacheData(Object data) {
		super();
		this.data = data;
	}
	
	public CacheData(Object data, long expireTime) {
		super();
		this.data = data;
		this.expireTime = expireTime;
	}

	public Object get() {
		this.updateTime = System.currentTimeMillis();
		return this.data;
	}
	/**
	 * 是否失效
	 * @return
	 */
	public boolean isExpire() {
		long now = Calendar.getInstance().getTimeInMillis();
		return now - updateTime > expireTime;
	}

	public long getSaveTime() {
		return saveTime;
	}

	public void setSaveTime(long saveTime) {
		this.saveTime = saveTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
}
