package com.leewan.util.interceptor;

public interface SqlHelper {

	public String getPagingSql(String sql, PageInfo pageInfo);
}
