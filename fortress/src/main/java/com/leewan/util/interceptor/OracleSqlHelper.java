package com.leewan.util.interceptor;

public class OracleSqlHelper implements SqlHelper {

	@Override
	public String getPagingSql(String sql, PageInfo pageInfo) {
		// TODO Auto-generated method stub
		StringBuffer pagingSql;
		try {
			int offset = (pageInfo.getPageNo() - 1) * pageInfo.getPageSize() + 1; 
			pagingSql = new StringBuffer();
			pagingSql
					.append("SELECT * FROM(SELECT ROW_.*,ROWNUM ROWNUM_ FROM(");
			pagingSql.append(sql.trim());
			pagingSql.append(")ROW_ )WHERE ROWNUM_>=" + offset);
			pagingSql.append(" AND ROWNUM_<"
					+ (offset + pageInfo.getLimit()));
			return pagingSql.toString();
		} finally {
			pagingSql = null;
		}
	}

}
