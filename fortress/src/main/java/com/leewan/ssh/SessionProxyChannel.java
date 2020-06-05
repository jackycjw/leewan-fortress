package com.leewan.ssh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;

import javax.websocket.Session;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import com.leewan.dao.machine.MachineDao;
import com.leewan.util.DateUtils;
import com.leewan.util.IOUtils;
import com.leewan.util.MathUtils;
import com.leewan.util.R;
import com.leewan.util.SpringContextUtils;
import com.leewan.util.UID;
import com.leewan.util.UserUtils;
import com.leewan.ws.MachineWebSocket;

public class SessionProxyChannel {
	
	public static final int BUFFER_SIZE = 1024 * 10;

	private ChannelShell channel;
	private Session session;
	private PipedOutputStream outputStream;
	private PipedInputStream inputStream;
	
	private IOThread io;
	
	private String machineUserId;
	//会话审计ID
	private String id;
	//审计文件
	private String auditFile;
	
	public void write(byte[] bs) throws IOException {
		try {
			this.outputStream.write(bs);
			this.outputStream.flush();
		} catch (Exception e) {
			this.close();
		}
		
	}
	
	public SessionProxyChannel(ChannelShell channel, Session session, String machineUserId) {
		super();
		this.channel = channel;
		this.session = session;
		this.machineUserId = machineUserId;
	}

	/**
	 * 关闭
	 */
	public void close() {
		try {
			SpringContextUtils.getBean(MachineDao.class).updateSessionAuditEndTime(id, DateUtils.getNow());
		} catch (Exception e) {}
		
		try {
			this.channel.disconnect();
		} catch (Exception e) {}
		
		try {
			this.io.close();
		} catch (Exception e) {}
		
		try {
			this.session.close();
		} catch (Exception e) {}
	}

	public void start() throws IOException, JSchException {
		this.outputStream = new PipedOutputStream();
		this.inputStream = new PipedInputStream(BUFFER_SIZE);
		PipedInputStream in = new PipedInputStream(outputStream,BUFFER_SIZE);
		
		PipedOutputStream out = new PipedOutputStream(inputStream);
		channel.setInputStream(in);
		channel.setOutputStream(out, true);
		channel.connect(10 * 1000);
		
		this.auditFile = UID.getUUID();
		this.id = UID.getUUID();
		R param = R.ok().put("id", this.id).put("userId", UserUtils.getUser().getId())
			.put("machineUserId", this.machineUserId).put("startTime", DateUtils.getNow())
			.put("auditFile", auditFile);
		
		SpringContextUtils.getBean(MachineDao.class).addNewSessionAudit(param);
		io = new IOThread(this.auditFile, this);
		MachineWebSocket.proxyChannels.submit(io);
	}
	
	class IOThread implements Runnable {
		private SessionProxyChannel proxyChannel;
		OutputStream out;
		private String auditFile;
		public IOThread(String auditFile, SessionProxyChannel proxyChannel) {
			try {
				this.proxyChannel = proxyChannel;
				this.auditFile = auditFile;
				String currentPath = IOUtils.getCurrentPath();
				out = new FileOutputStream(new File(currentPath, this.auditFile));
			} catch (Exception e) {
				
			}
		}
		
		public void close() {
			IOUtils.close(out);
		}
		
		private void writeAudit(byte[] bs, int len) {
			try {
				out.write(MathUtils.integerToBytes(len));
				out.write(bs, 0, len);
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
			try {
				byte[] bs = new byte[BUFFER_SIZE];
				int len = 0;
				while((len = SessionProxyChannel.this.inputStream.read(bs)) > 0) {
					writeAudit(bs, len);
					SessionProxyChannel.this.session.getBasicRemote().sendText(new String(bs, 0, len));;
					SessionProxyChannel.this.session.getBasicRemote().flushBatch(); 
				}
			} catch (Exception e) {
				SessionProxyChannel.this.close();
			}
			
		}
	}
	
}
