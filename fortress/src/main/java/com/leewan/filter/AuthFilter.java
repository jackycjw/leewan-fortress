package com.leewan.filter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.PatternMatchUtils;

import com.leewan.util.cache.CacheContainer;
import com.leewan.bean.User;
import com.leewan.pageContext.ContextConstant;
import com.leewan.util.BaseController;
import com.leewan.util.R;
import com.leewan.util.StringUtils;
import com.leewan.util.UserUtils;

import com.alibaba.fastjson.JSONObject;

public class AuthFilter implements Filter {

	Set<String> uncheckURI = new HashSet<String>();
	
	String[] uncheckurls;
	
	public AuthFilter(String uncheckURI) {
		StringTokenizer tokenizer = new StringTokenizer(uncheckURI, ",");
		List<String> list = new ArrayList<String>();
		
		while(tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			if(StringUtils.hasLength(token)) {
				list.add(token.trim());
			}
		}
		
		uncheckurls = new String[list.size()];
		
		for(int i=0;i<list.size();i++) {
			uncheckurls[i] = list.get(i);
		}
	}
	
	public static void main(String[] args) {
		String s = "/page/*,/user/login, /machine/upLoadMachineStatus,,11";
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		while(tokenizer.hasMoreElements()) {
			System.out.println(tokenizer.nextToken());
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		
		HttpServletResponse response = (HttpServletResponse) res;
		response.addHeader("access-control-allow-origin", "http://172.16.100.102");
		
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		requestURI = requestURI.substring(contextPath.length());
		
		if(PatternMatchUtils.simpleMatch(uncheckurls, requestURI)) {
			chain.doFilter(req, res);
		} else {
			User user = (User) CacheContainer.get(getToken(request));
			if(user != null) {
				UserUtils.setUser(user);
				chain.doFilter(req, res);
			}else {
				setUnlogin(res);
			}
		}
	}
	
	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("token");
		if(!StringUtils.hasLength(token)) {
			token = request.getParameter("token");
		}
		return token;
	}
	
	private void setUnlogin(ServletResponse res) throws IOException {
		res.getWriter().write(R.unlogin().toString());
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}
