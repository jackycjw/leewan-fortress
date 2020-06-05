package com.leewan;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.leewan.util.IOUtils;

public class SSHUtils {

	final static String user = "root";// 用户名
	final static String pwd = "zhonglizi@*@112";// 密码
	final static String ip = "47.104.80.203";// 服务器地址
	final static int port = 22;// 端口号
	final static int timeout = 60000000;

	public static void main(String[] args) throws Exception {
		JSch jsch = new JSch();
		session = jsch.getSession(user, ip, port);
		session.setPassword(pwd);
		session.setConfig("StrictHostKeyChecking", "no"); // 第一次访问服务器时不用输入yes
		session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
		session.setTimeout(timeout);
		session.connect();
		
		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		
		sftp.connect();
		
		Vector ls = sftp.ls("/root/webapps");
		for(int i=0;i<ls.size();i++) {
			LsEntry l = (LsEntry)ls.get(i);
			System.out.println(l.getLongname());
		}
	}

	private static final String ENCODING = "UTF-8";
	// private static final int timeout = 60 * 60 * 1000;
	private static final int defaultPort = 22;
	
	static Session session;

	public static Session getJSchSession(String ip, int port, String user, String pwd, int timeout)
			throws JSchException {
		JSch jsch = new JSch();
		if(session == null) {
			session = jsch.getSession(user, ip, port);
			session.setPassword(pwd);
			session.setConfig("StrictHostKeyChecking", "no"); // 第一次访问服务器时不用输入yes
			session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
			session.setTimeout(timeout);
			session.connect();
		}
		return session;
	}

	public static void execCommandByJSch(String ip, String user, String pwd, String command)
			throws IOException, JSchException {
		execCommandByJSch(ip, defaultPort, user, pwd, timeout, command, ENCODING);
	}

	public static void execCommandByJSch(String ip, int port, String user, String pwd, String command)
			throws IOException, JSchException {
		execCommandByJSch(ip, port, user, pwd, timeout, command, ENCODING);
	}

	public static void execCommandByJSch(String ip, int port, String user, String pwd, int timeout, String command,
			String resultEncoding) throws IOException, JSchException {
		Session session = getJSchSession(ip, port, user, pwd, timeout);
		execCommandByJSch(session, command, resultEncoding);
	}

	public static void execCommandByJSch(Session session, String command) throws IOException, JSchException {
		execCommandByJSch(session, command, ENCODING);
	}

	public static void execCommandByJSch(Session session, String command, String resultEncoding)
			throws IOException, JSchException {
		ChannelShell channel = (ChannelShell) session.openChannel("shell");
		
		PipedInputStream pipeIn = new PipedInputStream();
		PipedOutputStream pipeOut = new PipedOutputStream( pipeIn );
		channel.setInputStream(System.in);
		channel.setOutputStream(System.out, true);
		channel.connect(10 * 1000);
		pipeOut.write(command.getBytes());
	}
}
