package com.leewan.dao.machine;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leewan.util.interceptor.PageInfo;

public interface MachineDao {

	public List<Map<String, Object>> queryUserMachineUserList(Map<String, Object> param);
	
	public List<Map<String, Object>> distributeMachine(Map<String, Object> param);
	
	//机器
	public List<Map<String, Object>> queryMachines(Map<String, Object> param);
	public int updateMachine(Map<String, Object> param);
	public int addMachine(Map<String, Object> param);
	public int delMachine(String id);
	
	//机器用户
	public List<Map<String, Object>> queryMachineUser(Map<String, Object> param);
	public int addMachineUser(Map<String, Object> param);
	public int delMachineUser(Map<String, Object> param);
	public int updateMachineUser(Map<String, Object> param);
	
	/**
	 * 新增会话审计
	 * @param param
	 * @return
	 */
	public int addNewSessionAudit(Map<String, Object> param);
	
	public int updateSessionAuditEndTime(@Param("id")String id, @Param("endTime")String endTime);
	
	
	public List<Map<String, Object>> querySessionAudits(Map<String, Object> param);
	
	public Map<String, Object> getSessionAudit(String auditId);
	
	
	public Map<String, Object> getMachineConnection(@Param("machineUserId")String machineUserId);
	
	public int delUserMachineUser(@Param("userId")String userId,@Param("machineUserId")String machineUserId);
	
	public int addUserMachineUser(@Param("userId")String userId,@Param("machineUserId")String machineUserId);
}
