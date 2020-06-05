package com.leewan.ds;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.JdbcUtils;

/**
 * 异步测试连接是否有效
 * @author JackyCjw
 *
 */
public class TestConnection extends Thread {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Connection conn;
	
	private int validationQueryTimeout;
	
	private String validationQuery = "select 1 from dual";
	
	private CountDownLatch latch;
	
	private boolean success;
	
	
	public TestConnection(Connection conn, int validationQueryTimeout, CountDownLatch latch) {
		super();
		this.conn = conn;
		this.validationQueryTimeout = validationQueryTimeout;
		this.latch = latch;
	}

	public boolean isSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	@Override
	public void run() {
		try {
			if (conn.isClosed()) {
				success = false;
				return;
	        }
	        Statement stmt = null;
	        ResultSet rset = null;
	        try {
	            stmt = conn.createStatement();
	            if (validationQueryTimeout > 0) {
	                stmt.setQueryTimeout(validationQueryTimeout);
	            }
	            rset = stmt.executeQuery(validationQuery);
	            if (!rset.next()) {
	            	success = false;
	            }
	            success = true;
	        } finally {
	            JdbcUtils.close(rset);
	            JdbcUtils.close(stmt);
	        }

		} catch (Exception e) {
		} finally {
			//完成后主动通知主线程继续恢复执行
			this.latch.countDown();
		}
		
	}
	
}
