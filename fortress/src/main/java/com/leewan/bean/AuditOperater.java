package com.leewan.bean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.Map;

import com.leewan.dao.machine.MachineDao;
import com.leewan.util.IOUtils;
import com.leewan.util.SpringContextUtils;

public class AuditOperater {

	private RandomAccessFile accessFile;
	
	private long total;
	
	private LinkedList<Long> pos = new LinkedList<>();
	
	public void close() {
		pos.clear();
		IOUtils.close(accessFile);
	}
	
	public AuditOperater(String auditId) {
		Map<String, Object> audit = SpringContextUtils.getBean(MachineDao.class).getSessionAudit(auditId);
		String audit_file = (String) audit.get("audit_file");
		try {
			this.accessFile = new RandomAccessFile(IOUtils.getCurrentPathFile(audit_file), "r");
			this.total = accessFile.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isOver(long filePointer, int len) {
		if(filePointer + len > total) {
			return true;
		}
		return false;
	}
	
	public byte[] getNext() {
		try {
			long filePointer = accessFile.getFilePointer();
			pos.add(filePointer);
			int len = accessFile.readInt();
			
			byte[] bs = new byte[len];
			accessFile.read(bs);
			return bs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.MAX_VALUE);
	}
	
	public void last() {
		if(pos.size() == 0) {
			return;
		}
		pos.removeLast();
	}
}
