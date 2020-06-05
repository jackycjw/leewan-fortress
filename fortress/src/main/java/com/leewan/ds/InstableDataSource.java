package com.leewan.ds;


import java.sql.Connection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;

public class InstableDataSource extends DruidDataSource {

	/**
	 * 默认不稳定，使用重新实现的测试、关闭方法
	 */
	private boolean stable = false;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void setUsername(String username) {
		if (inited) {
            return;
        }
		super.setUsername(username);
	}
	
	@Override
	public void setUrl(String jdbcUrl) {
		if (inited) {
			 return;
        }
		super.setUrl(jdbcUrl);
	}
	
	@Override
	public void setDriverClassName(String driverClass) {
		if (inited) {
			 return;
        }
		super.setDriverClassName(driverClass);
	}
	
	public void discardConnection(Connection realConnection) {
		if(stable) {
			super.discardConnection(realConnection);
			return;
		}
		long time = System.currentTimeMillis();
		//异步关闭连接
		new CloseThread(realConnection).start();
        super.discardConnection(null);
        if(logger.isDebugEnabled()) {
        	logger.debug("释放连接");
        }
    }
	
	/**
	 * 异步执行测试语句，当前线程最多等待validationQueryTimeout时间
	 */
	@Override
	protected boolean testConnectionInternal(Connection conn) {
		if(stable) {
			return super.testConnectionInternal(conn);
		}
		long start = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch(1);
		TestConnection testConnection = new TestConnection(conn, validationQueryTimeout, latch);
		testConnection.start();
		boolean isSuccess = false;
		try {
			latch.await(this.validationQueryTimeout, TimeUnit.SECONDS);
			isSuccess = testConnection.isSuccess();
			if(testConnection.isAlive()) {
				testConnection.interrupt();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(logger.isDebugEnabled()) {
        	logger.debug("测试连接耗时： " + (System.currentTimeMillis() - start));
        }
		return isSuccess;
	}
	
}
