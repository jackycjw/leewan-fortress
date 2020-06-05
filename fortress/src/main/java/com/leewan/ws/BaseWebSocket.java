package com.leewan.ws;

import javax.websocket.Session;

import com.leewan.util.StringUtils;

public class BaseWebSocket {

	protected String getParameter(Session session, String key) {
		String queryString = session.getQueryString();
		if(!StringUtils.hasLength(queryString)) {
			return null;
		}
		String[] split = queryString.split("&");
		
		if(split != null) {
			for(String s : split) {
				if(StringUtils.hasLength(s)) {
					String[] kv = s.split("=",-1);
					if(kv[0].equals(key)) {
						return kv[1];
					}
				}
			}
		}
		return null;
	}
}
