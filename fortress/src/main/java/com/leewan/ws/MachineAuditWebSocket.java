package com.leewan.ws;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import com.leewan.aop.Authority;
import com.leewan.aop.OperateAuth;
import com.leewan.bean.AuditOperater;
import com.leewan.bean.User;
import com.leewan.dao.machine.MachineDao;
import com.leewan.ssh.MachineSession;
import com.leewan.ssh.SessionProxyChannel;
import com.leewan.util.IOUtils;
import com.leewan.util.SpringContextUtils;
import com.leewan.util.UserUtils;

@ServerEndpoint("/audit")
@Component
public class MachineAuditWebSocket extends BaseWebSocket {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	static Map<Session, AuditOperater> mapping = new HashMap<Session, AuditOperater>();
	
	static final String NEXT = "0";
	static final String LAST = "1";
	
	
	@OnClose
	public void close(Session session) {
		AuditOperater audit = mapping.get(session);
		audit.close();
	}
	
	@OnMessage
    public void onMessage(String message, Session session) throws IOException {
		AuditOperater audit = mapping.get(session);
		if(NEXT.equals(message)) {
			byte[] next = audit.getNext();
			try {
				session.getBasicRemote().sendText(new String(next));
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			
		}else if(LAST.equals(message)){
			audit.last();
		}
    }

	
	@OnOpen
    public void onOpen(Session session) throws JSchException, IOException {
		String auditId = getParameter(session, "auditId");
		mapping.put(session, new AuditOperater(auditId));
	}
}
