package com.leewan.dao.operate;

import java.util.List;
import java.util.Map;

import com.leewan.bean.SftpOperateBean;
import com.leewan.util.interceptor.PageInfo;

public interface OperateLogDao {

	public int saveSftpOperate(SftpOperateBean log);
	
	public List<Map<String, Object>> querySftpLog(Map<String, Object> param, PageInfo page);
}
