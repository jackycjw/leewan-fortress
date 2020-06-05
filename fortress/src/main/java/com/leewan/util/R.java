package com.leewan.util;

import java.util.HashMap;

import com.alibaba.fastjson.JSONObject;

public class R extends HashMap<String, Object> {

	public static String SUCCESS_CODE = "1000";
	
	public static String FAILURE_CODE = "1001";
	
	public static String UNLOGIN_CODE = "1002";
	
	public static String NOAUTHORITY_CODE = "1003";
	
	public static String ILLEGAL_PARAMETER = "1004";
	
	private R() {}
	
	public static R ok() {
		return new R();
	}
	
	public static R success() {
		R r = new R();
		r.put("code", SUCCESS_CODE);
		return r;
	}
	
	public static R failure() {
		R r = new R();
		r.put("code", FAILURE_CODE);
		r.put("msg", "失败");
		return r;
	}
	
	public static R failure(String msg) {
		R r = new R();
		r.put("code", FAILURE_CODE);
		r.put("msg",msg);
		return r;
	}
	
	public static R unlogin() {
		R r = new R();
		r.put("code", UNLOGIN_CODE);
		r.put("msg", "未登录");
		return r;
	}
	
	public static R noAuth() {
		R r = new R();
		r.put("code", NOAUTHORITY_CODE);
		return r;
	}
	
	public static R illegalParam() {
		R r = new R();
		r.put("code", ILLEGAL_PARAMETER);
		r.put("msg", "非法参数");
		return r;
	}
	
	public R setMsg(Object data) {
		return this.put("msg", data);
	}
	
	public R put(String key, Object val) {
		if("roleId".equals(key)) {
			System.out.println(val.getClass());
		}
		super.put(key, val);
		return this;
	}
	
	public R setData(Object data) {
		return this.put("data", data);
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
