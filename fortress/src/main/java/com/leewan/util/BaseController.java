package com.leewan.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.leewan.bean.User;
import com.leewan.util.interceptor.PageInfo;

import com.alibaba.fastjson.JSONObject;


public class BaseController {

	@Autowired
	protected HttpServletRequest request;
	
	@Autowired
	protected HttpServletResponse response;
	
	protected User getUser() {
		return UserUtils.getUser();
	}
	
	/**
	 * 提取查询参数构造成map
	 * @param urlParams
	 * @return 
	 */
	public static Map<String, Object> getUrlParamValue(String urlParams){
		// publicCode=abcd&type=yx
		Map<String, Object> values = new HashMap<String, Object>();
		if(urlParams != null && !"".equals(urlParams)){
			String[] params = urlParams.split("&");
			for(String param : params){
				String[] split = param.split("=");
				String key = split[0];
				String value = split.length>1?split[1]:"";
				values.put(key, value);
			}
		}
		return values;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJsonPostParameters(JSONObject jo){
		Map<String, Object> dest = new HashMap<String, Object>(jo);
		return dest;
	}
	
	protected Map<String, Object> getParamters() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		String params = request.getQueryString();
		paramsMap.putAll(getUrlParamValue(params));
		
		Map<String, String[]> tmp = request.getParameterMap();
		for(Entry<String, String[]> item : tmp.entrySet()) {
			if(item.getValue() == null || item.getValue().length == 0) {
				paramsMap.put(item.getKey(), null);
			}else if(item.getValue().length == 1) {
				paramsMap.put(item.getKey(), item.getValue()[0]);
			}else {
				paramsMap.put(item.getKey(), item.getValue());
			}
		}
		return paramsMap;
	}
	
	
	protected JSONObject getSteamParamters() {
		try {
			ServletInputStream in = request.getInputStream();
			String fromSteam = StringUtils.getFromSteam(in);
			JSONObject parseObject = JSONObject.parseObject(fromSteam);
			return parseObject == null ? new JSONObject() : parseObject;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	
	protected PageInfo getPageInfo(Map<String, Object> paramters) {
		int pageIndex = Integer.valueOf((String)paramters.get("pageIndex"));
		int pageSize = Integer.valueOf((String)paramters.get("pageSize"));
		return new PageInfo(pageIndex, pageSize);
	}
	
	
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public String exception(Exception e) {
    	e.printStackTrace();
    	return R.failure().setData(e.getMessage()).toString();
    }     
}
