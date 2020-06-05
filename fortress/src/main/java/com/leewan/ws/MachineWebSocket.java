package com.leewan.ws;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSchException;
import com.leewan.bean.User;
import com.leewan.dao.machine.MachineDao;
import com.leewan.ssh.MachineSession;
import com.leewan.ssh.SessionProxyChannel;
import com.leewan.util.SpringContextUtils;
import com.leewan.util.UserUtils;

@ServerEndpoint("/ssh")
@Component
public class MachineWebSocket extends BaseWebSocket {

	public static ExecutorService proxyChannels = Executors.newCachedThreadPool();
	
	private static Map<Integer, MachineWebSocket> userSession = new ConcurrentHashMap<Integer, MachineWebSocket>();
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@OnClose
	public void close(Session session) {
		MachineSession machineSession = MachineSession.get(session);
		machineSession.close();
	}
	
	@OnMessage
    public void onMessage(String message, Session session) throws IOException {
		MachineSession machineSession = MachineSession.get(session);
		machineSession.write(message.getBytes());
    }

	
	@OnOpen
    public void onOpen(Session session) throws JSchException, IOException {
		String machineUserId = getParameter(session, "machineUserId");
		int cols = Integer.valueOf(getParameter(session, "cols"));//列
		int rows = Integer.valueOf(getParameter(session, "rows"));//行
		int wp = Integer.valueOf(getParameter(session, "wp"));//宽
		int hp = Integer.valueOf(getParameter(session, "hp"));//高
		MachineSession.create(session, machineUserId, cols, rows, wp, hp);
	}
}
