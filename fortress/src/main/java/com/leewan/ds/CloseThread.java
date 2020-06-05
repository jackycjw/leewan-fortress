package com.leewan.ds;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于异步关闭connection
 * 由于连接不稳定，同步关闭很耗时，故将连接关闭动作单独抽离，做成异步操作，这样则不影响主线程的性能
 * @author JackyCjw
 *
 */
public class CloseThread extends Thread {
	
	private final Logger logger = LoggerFactory.getLogger(CloseThread.class);
	
	private Connection con;
	
	
	public CloseThread(Connection con) {
		super();
		this.con = con;
	}


	@Override
	public void run() {
		long time = System.currentTimeMillis();
		try {
			this.con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("关闭连接耗时： " + (System.currentTimeMillis() - time));
	}
}
