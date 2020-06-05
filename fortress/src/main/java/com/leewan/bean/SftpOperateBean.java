package com.leewan.bean;

import com.leewan.util.DateUtils;
import com.leewan.util.UID;

public class SftpOperateBean {

	public static final String OP_TYPE_UPLOAD_FILE = "0";//上传文件
	public static final String OP_TYPE_MKDIR = "1";//新建文件夹
	public static final String OP_TYPE_DEL_FILE = "2";//删除文件
	public static final String OP_TYPE_DEL_DIR = "3";//删除文件夹
	
	private String id;
	private String path;
	private String type;
	private String userId;
	private String machineUserId;
	private String time;
	
	public SftpOperateBean(String path, String type, String userId, String machineUserId) {
		super();
		this.id = UID.getUUID();
		this.path = path;
		this.type = type;
		this.userId = userId;
		this.machineUserId = machineUserId;
		this.time = DateUtils.getNowTimeStamp();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMachineUserId() {
		return machineUserId;
	}

	public void setMachineUserId(String machineUserId) {
		this.machineUserId = machineUserId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
