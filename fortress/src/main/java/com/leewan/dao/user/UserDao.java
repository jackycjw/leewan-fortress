package com.leewan.dao.user;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.leewan.bean.Role;
import com.leewan.bean.User;

public interface UserDao {

	public User getUser(Map map);
	
	public int changeStatus(@Param("userId") String userId, @Param("status") int status);
	
	public Role getRole(User user);
	
	public Set<String> getAuthorities(Role role);
	
	public List<Map<String, Object>> queryUserList(Map<String, Object> param);
	
	public int addUser(Map<String, Object> param);
	
	public int delUser(String userId);
	
	public List<Role> queryRoles();

	public int updateUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
	
	public int inserUserRole(@Param("userId") String userId, @Param("roleId") String roleId);
	
	public int modifyPwd(@Param("password")String password,@Param("userId") String userId);
}
