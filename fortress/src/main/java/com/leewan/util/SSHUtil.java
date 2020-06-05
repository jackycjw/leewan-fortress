package com.leewan.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.leewan.dao.machine.MachineDao;

public class SSHUtil {

	public static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();
	
	static int timeout = 1000 * 30;
	
	public static int FILE_TYPE_FILE = 0;
	public static int FILE_TYPE_FOLDER = 1;
	public static int FILE_TYPE_LINK = 2;
	public static int FILE_TYPE_SOCKET = 3;
	public static int FILE_TYPE_DEVICE = 4;
	public static int FILE_TYPE_OTHER = 5;
	
	public static String getUserFromLongName(String longName) {
		String[] split = longName.split("\\s+",-1);
		if(split != null && split.length > 2) {
			return split[2];
		}
		return "";
	}
	
	public static Map<String, Object> format(LsEntry fLsEntry) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, Object> item = new HashMap<String, Object>();
		String filename = fLsEntry.getFilename();
		SftpATTRS attrs = fLsEntry.getAttrs();
		String permissions = attrs.getPermissionsString();
		long size = attrs.getSize();
		String modifyTime = sdf.format(new Date(attrs.getMTime()*1000L));
		String owner = SSHUtil.getUserFromLongName(fLsEntry.getLongname());
		item.put("name", filename);
		item.put("permissions", permissions);
		item.put("modifyTime", modifyTime);
		item.put("owner", owner);
		item.put("size", size);
		item.put("type", SSHUtil.getFileType(attrs));
		return item;
	}
	
	public static int getFileType(SftpATTRS attrs) {
		String p = attrs.getPermissionsString();
		if(p.startsWith("-")) {
			return FILE_TYPE_FILE;
		}
		if(p.startsWith("d")) {
			return FILE_TYPE_FOLDER;
		}
		if(p.startsWith("b") || p.startsWith("c")) {
			return FILE_TYPE_DEVICE;
		}
		if(p.startsWith("l")) {
			return FILE_TYPE_LINK;
		}
		if(p.startsWith("s")) {
			return FILE_TYPE_SOCKET;
		}
		return FILE_TYPE_OTHER;
	}
	
	public static boolean uploadFile(String machineUserId, String remotePath, String remoteFileName,InputStream in)
    {
        try {
        	ChannelSftp sftp = openSftp(machineUserId);
        	sftp.cd(remotePath);
            sftp.put(in, remoteFileName);
            return true;
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
            
        } finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
	/**
	 * 
	 * @param sftp
	 * @param createpath
	 * @return
	 */
	public synchronized static boolean createDir(ChannelSftp sftp, String createpath) {
        try {
            if (isDirExist(sftp, createpath)) {
                sftp.cd(createpath);
                return true;
            }
            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(sftp,filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }

            }
            sftp.cd(createpath);
            return true;
        }
        catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }
	 
	public static boolean isDirExist(ChannelSftp sftp, String directory)
    {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }
	
	public static ChannelSftp openSftp(String machineUserId) throws JSchException {
		Session session = getSession(machineUserId);
		ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
		sftp.connect();
		return sftp;
	}
	
	public static Session getSession(String machineUserId) throws JSchException {
		String sessionKey = getSessionKey(machineUserId);
		if(sessions.containsKey(sessionKey)) {
			Session session = sessions.get(sessionKey);
			boolean connected = session.isConnected();
			if(connected) {
				return session;
			}else {
				return getNewSession(machineUserId);
			}
		}else {
			return getNewSession(machineUserId);
		}
	}
	
	public static Session getNewSession(String machineUserId) throws JSchException {
		MachineDao machineDao = SpringContextUtils.getBean(MachineDao.class);
		Map<String, Object> machine = machineDao.getMachineConnection(machineUserId);
		String ip = (String) machine.get("host");
		String user = (String) machine.get("user");
		int port = (Integer) machine.get("remote_port");
		String pwd = (String) machine.get("password");
		
		JSch jsch = new JSch();
		Session session = jsch.getSession(user, ip, port);
		session.setPassword(pwd);
		session.setConfig("StrictHostKeyChecking", "no"); // 第一次访问服务器时不用输入yes
		session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
		session.setTimeout(timeout);
		session.connect();
		String sessionKey = getSessionKey(machineUserId);
		
		if(sessions.containsKey(sessionKey)) {
			Session last = sessions.get(sessionKey);
			try {
				last.disconnect();
			} catch (Exception e) {
			}
		}
		sessions.put(sessionKey, session);
		return session;
	}
	
	public static String combinePath(String parent, String name) {
		String completePath = completePath(parent);
		return completePath + name;
	}
	
	public static void rmDir(ChannelSftp sftp, String path) throws SftpException {
		Vector ls = sftp.ls(path);
		for(int i=0;i<ls.size();i++) {
			LsEntry l = (LsEntry)ls.get(i);
			String name = l.getFilename();
			if(name.equals(".") || name.equals("..")) {
				continue;
			}
			SftpATTRS attrs = l.getAttrs();
			String combinePath = combinePath(path, name);
			if(attrs.isDir()) {
				rmDir(sftp, combinePath);
			}else {
				sftp.rm(combinePath);
			}
		}
		sftp.rmdir(path);
	}
	
	private static String getSessionKey(String machineUserId) {
		return machineUserId;
	}
	
	
	public static void completePath(StringBuilder path) {
		if(!path.toString().endsWith("/")) {
        	path.append("/");
        }
	}
	
	public static String completePath(String path) {
		if(!path.endsWith("/")) {
        	path = path + "/";
        }
		return path;
	}
}
