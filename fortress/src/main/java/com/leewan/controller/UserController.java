package com.leewan.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.leewan.aop.Authority;
import com.leewan.aop.OperateAuth;
import com.leewan.bean.Role;
import com.leewan.bean.User;
import com.leewan.util.cache.CacheContainer;
import com.leewan.dao.user.UserDao;
import com.leewan.util.BaseController;
import com.leewan.util.EncryptUtils;
import com.leewan.util.R;
import com.leewan.util.UID;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

	@Autowired
	UserDao userDao;
	
	
	@RequestMapping(value="login",method=RequestMethod.POST)
	public Object login() throws IOException {
		Map<String, Object> paramters = getSteamParamters();
		String password = (String) paramters.get("password");
		password = EncryptUtils.encrypt(password);
		paramters.put("password", password);
		User user = this.userDao.getUser(paramters);
		if(user != null) {
			if(!User.STATUS_NORMAL.equals(user.getStatus())) {
				return R.failure().setMsg("您的账号已失效，请联系管理员");
			}
			user.setRole(this.userDao.getRole(user));
			user.setAuthority(this.userDao.getAuthorities(user.getRole()));
			String token = UID.getUUID();
			user.setToken(token);
			CacheContainer.save(token, user, 30 * 60 * 1000);
			return R.success().setData(token).toString();
		}else {
			return R.failure().setData("用户名或密码错误").toString();
		}
	}
	
	@RequestMapping(value="logout",method=RequestMethod.GET)
	public Object logout() throws IOException {
		User user = super.getUser();
		CacheContainer.delete(user.getToken());
		return R.unlogin();
	}
	

	@RequestMapping(value="password", method=RequestMethod.POST)
	public Object modifyPwd() throws IOException {
		JSONObject param = super.getSteamParamters();
		String password = param.getString("password");
		password = EncryptUtils.encrypt(password);
		try {
			this.userDao.modifyPwd(password, super.getUser().getId());
			this.logout();
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	@RequestMapping("loadUser")
	public Object loadUser() throws IOException {
		return R.success().setData(getUser());
	}
	
	@RequestMapping(value="user", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object user() throws IOException {
		JSONObject param = super.getSteamParamters();
		String password = param.getString("password");
		password = EncryptUtils.encrypt(password);
		param.put("id", UID.getUUID());
		param.put("password", password);
		try {
			this.userDao.addUser(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	@RequestMapping(value="user", method=RequestMethod.DELETE)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object delUser() throws IOException {
		JSONObject param = super.getSteamParamters();
		String userId = param.getString("userId");
		try {
			this.userDao.delUser(userId);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	@RequestMapping(value="users", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object users() throws IOException {
		List<Map<String, Object>> users = this.userDao.queryUserList(super.getSteamParamters());
		return R.success().setData(users);
	}
	
	@RequestMapping(value="status", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object status() throws IOException {
		JSONObject param = super.getSteamParamters();
		int status = param.getInteger("status");
		String userId = param.getString("userId");
		try {
			this.userDao.changeStatus(userId, status);
			return R.success();
		} catch (Exception e) {
			e.printStackTrace();
			return R.failure().setMsg(e.getMessage());
		}
	}
	
	@RequestMapping(value="roles")
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object roles() throws IOException {
		List<Role> roles = this.userDao.queryRoles();
		return R.success().setData(roles);
	}
	
	@RequestMapping(value="distributeRole", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object distributeRole() throws IOException {
		JSONObject param = super.getSteamParamters();
		String userId = param.getString("userId");
		String roleId = param.getString("roleId");
		int rs = 0;
		try {
			rs = this.userDao.updateUserRole(userId, roleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(rs <= 0) {
			try {
				this.userDao.inserUserRole(userId, roleId);
			} catch (Exception e) {
				return R.failure().setMsg(e.getMessage());
			}
		}
		return R.success();
	}
	
}
