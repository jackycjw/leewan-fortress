package com.leewan.util.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 通用分页处理逻辑的拦截器
 * 
 * @author PPM项目组
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}) })
public class PageInterceptor implements Interceptor {

	private static Logger LOG = LoggerFactory.getLogger(PageInterceptor.class);

	private static final String SELECT = "select";
	private static final String FROM = "from";
	private static final String ORDER_BY = "order by";
	private static final String GROUP_BY = "group by";
	private static final String UNION = "union";
	private static final Pattern PATTERN_SQL = Pattern.compile("\\s+");

	/**
	 * 在myBatis生成Statement对象前修改SQL语句 替换掉myBatis的SQL 使用我们自己构造的分页SQL
	 * 同时查询总记录数,并设置到pageInfo对象中 使其支持分页查询
	 */
	public Object intercept(Invocation invocation) throws Throwable {

		RoutingStatementHandler statementHandler = (RoutingStatementHandler)realTarget(invocation.getTarget());
		Connection connection = (Connection) invocation.getArgs()[0];

		StatementHandler handler = (StatementHandler) FieldUtils.readField(statementHandler, "delegate", true);
		PageInfo pageInfo = (PageInfo) FieldUtils.readField(handler,"rowBounds", true);
		MappedStatement mappedStatement = (MappedStatement) FieldUtils.readField(handler, "mappedStatement", true);
		BoundSql boundSql = handler.getBoundSql();
		LOG.info("获取第{}页 数据 " , pageInfo.getPageNo());
		LOG.info("mybaits原生SQL = " + boundSql.getSql());

		// 获取总记录数并且设置到pageInfo中
		getCountAndSetInpageInfo(boundSql, pageInfo, mappedStatement,connection);

		// 获取改造后分页的SQL
		String pagingSql = getSqlHelper(connection).getPagingSql(boundSql.getSql(), pageInfo);
		LOG.debug("改造支持分页的SQL = " + pagingSql);

		// 修改BoundSql对象的sql,使用我们带分页的SQL
		FieldUtils.writeDeclaredField(boundSql, "sql", pagingSql, true);
		// 设置pageInfo对象为初始状态
		// pageInfo.setMeToDefault();
		Object proceed = invocation.proceed();
		return proceed;
	}

	/**
	 * 根据参数进行判断当前操作是否为分页查询 如果是分页操作则返回代理对象,否则返回原生对象
	 * 代理对象会调用intercept方法,动态修改SQL支持分页
	 */
	public Object plugin(Object arg0) {
		if (arg0 instanceof RoutingStatementHandler) {
			try {
				// 拿到操作类
				Field delegate = FieldUtils.getField(RoutingStatementHandler.class, "delegate", true);
				StatementHandler handler = (StatementHandler) delegate.get(arg0);
				// 拿到RowBounds对象,判断是否为分页操作
				RowBounds rowBounds = (RowBounds) FieldUtils.readField(handler,"rowBounds", true);
				if (rowBounds != RowBounds.DEFAULT
						&& rowBounds instanceof PageInfo) {
					// 返回代理对象
					return Plugin.wrap(arg0, this);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return arg0;
	}

	/**
	 * 将SQL语句改造成查询总记录数的SQL
	 * 
	 * @param sql
	 *            正常查询的SQL语句
	 * @return 改造后的SQL语句
	 */
	private String getCountSql(String sql) {
		StringBuilder sb = new StringBuilder();
		String processSql = processSql(sql);
		//SQL语句转为小写,用作关键字查找
		String lowerSql = processSql(sql).toLowerCase();
		int orderByPos = 0;
		orderByPos=lowerSql.lastIndexOf(ORDER_BY);
		// 查询SQL中包含order by需要去掉
		if (orderByPos != -1) {
			processSql = processSql.substring(0, orderByPos);
		}
		// 查询SQL中包含union,group by 需要进行包装
		if (lowerSql.indexOf(UNION) != -1
				|| lowerSql.indexOf(GROUP_BY) != -1) {
			sb.insert(0, "SELECT COUNT(1) AS COUNT FROM ( ").append(processSql)
					.append(" ) TEMP");
			LOG.debug("查询总数的SQL = " + sb.toString());
			return sb.toString();
		}
		int fromPos = lowerSql.indexOf(FROM);
		sb.append("SELECT COUNT(1) AS COUNT").append(" ").append(
				processSql.substring(fromPos));
		LOG.debug("查询总数的SQL = " + sb.toString());
		return sb.toString();
	}

	/**
	 * 将myBatis原生SQL语句去掉多余空格与换行
	 * 
	 * @param sql
	 *            (myBatis配置文件解析后的原生SQL)
	 * @return 转换后的SQL
	 */
	private String processSql(String sql) {
		Matcher matcher = PATTERN_SQL.matcher(sql);
		return matcher.replaceAll(" ");
	}

	/**
	 * 将SQL语句转换成分页的SQL语句
	 * 
	 * @param sql
	 *            myBatis生成的原生SQL
	 * @return 转换后的SQL
	 */
	private String getPagingSql(String sql, PageInfo pageInfo) {
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

	/**
	 * 获得分页的总记录数并且设置到pageInfo对象中
	 * 
	 * @param boundSql
	 * @param rowBounds
	 *            (pageInfo分页参数)
	 * @param mappedStatement
	 * @param connection
	 */
	private void getCountAndSetInpageInfo(BoundSql boundSql,
			PageInfo pageInfo, MappedStatement mappedStatement,
			Connection connection) {
		String countSql = getCountSql(boundSql.getSql());
		Object parameterObject = boundSql.getParameterObject();
		BoundSql countBoundSql = copyFromBoundSql(mappedStatement, boundSql, countSql);	
		// 绑定参数的处理类
		ParameterHandler parameterHandler = new DefaultParameterHandler(
				mappedStatement, parameterObject, countBoundSql);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int totalRecord = rs.getInt(1);
				pageInfo.setTotalRecord(totalRecord);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getCause());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setProperties(Properties arg0) {
	}
	
	/** 
	 * 复制BoundSql对象 
	 */  
	private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {  
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, boundSql.getParameterMappings(), boundSql.getParameterObject());  
	    for (ParameterMapping mapping : boundSql.getParameterMappings()) {  
	        String prop = mapping.getProperty();  
	        if (boundSql.hasAdditionalParameter(prop)) {  
	            newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));  
	        }  
	    }  
	    return newBoundSql;  
	}  
	
	private SqlHelper getSqlHelper(Connection con) {
		try {
			String dbType = con.getMetaData().getDatabaseProductName();
			if("ORACLE".equals(dbType.toUpperCase())) {
				return new OracleSqlHelper();
			} else if ("MYSQL".equals(dbType.toUpperCase())) {
				return new MySQLHelper();
			} else if ("SQLITE".equals(dbType.toUpperCase())) {
				return new SQLiteHelper();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new OracleSqlHelper();
	}
	
	public static Object realTarget(Object target) {
        if (Proxy.isProxyClass(target.getClass())) {
            MetaObject metaObject = SystemMetaObject.forObject(target);
            return realTarget(metaObject.getValue("h.target"));
        }
        return target;
    }
	
}
