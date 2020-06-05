package com.leewan.util.interceptor;

public class SQLiteHelper implements SqlHelper {

	@Override
	public String getPagingSql(String sql, PageInfo pageInfo) {
		// TODO Auto-generated method stub
		StringBuffer pagingSql;
		try {
			int offset = (pageInfo.getPageNo() - 1) * pageInfo.getPageSize(); 
			pagingSql = new StringBuffer();
			pagingSql.append(sql.trim());
			pagingSql.append(" LIMIT ").append(pageInfo.getPageSize()).append(" OFFSET ").append(offset);
			return pagingSql.toString();
		} finally {
			pagingSql = null;
		}
	}

}
