package com.leewan.ssh;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.leewan.util.IOUtils;
import com.leewan.util.SSHUtil;

public class MachineSession {
	

	private Session session;
	
	private SessionProxyChannel channel;
	
	private String ip;
	private int port;
	private String user;
	private String pwd;
	private int timeout = 1000 * 30;
	
	private int cols;//列
	private int rows;//行
	private int wp;//宽
	private int hp;//高
	
	private static Map<javax.websocket.Session, MachineSession> machSessions = new ConcurrentHashMap<>();
	
	/**
	 * 
	 * @param session websocket会话
	 * @param machine 机器信息
	 * @param cols 列数
	 * @param rows 行数
	 * @param wp 宽度  单位px
	 * @param hp 高度  单位px
	 * @return
	 * @throws JSchException
	 * @throws IOException
	 */
	public static MachineSession create(javax.websocket.Session session, String machineUserId,
			int cols, int rows, int wp, int hp) throws JSchException, IOException {
		MachineSession machineSession = new MachineSession(session, machineUserId, cols, rows, wp, hp);
		machSessions.put(session, machineSession);
		return machineSession;
	}
	
	public void write(byte[] bs) throws IOException {
		this.channel.write(bs);
	}
	
	public void close() {
		this.channel.close();
	}
	
	public static MachineSession get(javax.websocket.Session session) {
		return machSessions.get(session);
	}
	
	
	private MachineSession(javax.websocket.Session session,String machineUserId,
			int cols, int rows, int wp, int hp) throws JSchException, IOException {
		this.cols = cols;
		this.rows = rows;
		this.wp = wp;
		this.hp = hp;
		initMachineSession(machineUserId);
		ChannelShell channel = (ChannelShell) this.session.openChannel("shell");
		channel.setPtySize(Integer.valueOf(cols), Integer.valueOf(rows), Integer.valueOf(wp), Integer.valueOf(hp));
		this.channel = new SessionProxyChannel(channel, session, machineUserId);
		this.channel.start();
	}
	
	private void initMachineSession(String machineUserId) throws JSchException {
		session = SSHUtil.getSession(machineUserId);
	}
	
}
