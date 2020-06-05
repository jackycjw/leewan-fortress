package com.leewan.util.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.leewan.util.DateUtils;
import com.leewan.util.StringUtils;

public class StringDateTypeHandler implements TypeHandler<String> {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		String val = rs.getString(columnName);
		if(!StringUtils.hasLength(val)) {
			return null;
		}
		Date date = DateUtils.parse(val);
		return DateUtils.getDateString(date.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		String val = rs.getString(columnIndex);
		if(!StringUtils.hasLength(val)) {
			return null;
		}
		Date date = DateUtils.parse(val);
		return DateUtils.getDateString(date.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String val = cs.getString(columnIndex);
		if(!StringUtils.hasLength(val)) {
			return null;
		}
		Date date = DateUtils.parse(val);
		return DateUtils.getDateString(date.getTime(), "yyyy-MM-dd HH:mm:ss");
	}

}
