package com.leewan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.leewan.aop.Authority;
import com.leewan.aop.OperateAuth;
import com.leewan.dao.machine.MachineDao;
import com.leewan.util.BaseController;
import com.leewan.util.R;
import com.leewan.util.UID;

@RestController
public class MachineController extends BaseController {

	@Autowired
	MachineDao machineDao;
	
	@RequestMapping(value="queryUserMachineUser", method=RequestMethod.GET)
	public Object queryUserMachineUser() {
		Map<String, Object> paramters = super.getSteamParamters();
		paramters.put("userId", super.getUser().getId());
		return R.success().setData(this.machineDao.queryUserMachineUserList(paramters));
	}
	
	
	@RequestMapping(value="distributeMachine", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object distributeMachine() {
		JSONObject steamParamters = super.getSteamParamters();
		List<Map<String, Object>> distributeMachine = this.machineDao.distributeMachine(steamParamters);
		R setData = R.success().setData(distributeMachine);
		return setData;
	}
	
	@RequestMapping(value="disableMachine", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object disableMachine() {
		JSONObject param = super.getSteamParamters();
		String machineUserId = param.getString("machineUserId");
		String userId = param.getString("userId");
		try {
			this.machineDao.delUserMachineUser(userId, machineUserId);
			return R.success();
		} catch (Exception e) {
			return R.failure().setMsg(e.getMessage());
		}
	}
	
	@RequestMapping(value="ableMachine", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object ableMachine() {
		JSONObject param = super.getSteamParamters();
		String machineUserId = param.getString("machineUserId");
		String userId = param.getString("userId");
		try {
			this.machineDao.addUserMachineUser(userId, machineUserId);
			return R.success();
		} catch (Exception e) {
			return R.failure().setMsg(e.getMessage());
		}
	}
	
	
	/**
	 * :查询机器列表
	 * @return
	 */
	@RequestMapping(value="machine", method=RequestMethod.GET)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object queryMachine() {
		Map<String, Object> param = super.getParamters();
		List<Map<String, Object>> list = this.machineDao.queryMachines(param);
		R setData = R.success().setData(list);
		return setData;
	}
	
	/**
	 * :新增机器
	 * @return
	 */
	@RequestMapping(value="machine", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object addMachine() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			param.put("id", UID.getUUID());
			this.machineDao.addMachine(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	/**
	 * :删除机器
	 * @return
	 */
	@RequestMapping(value="machine", method=RequestMethod.DELETE)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object delMachine() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			String machineId = (String) param.get("id");
			this.machineDao.delMachine(machineId);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	/**
	 *: 更新机器
	 * @return
	 */
	@RequestMapping(value="machine", method=RequestMethod.PUT)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object updateMachine() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			this.machineDao.updateMachine(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	
	/**
	 * :查询机器用户
	 * @return
	 */
	@RequestMapping(value="machineUser", method=RequestMethod.GET)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object queryMachineUser() {
		Map<String, Object> param = super.getParamters();
		try {
			List<Map<String, Object>> list = this.machineDao.queryMachineUser(param);
			return R.success().setData(list);
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	/**
	 * :新增机器用户
	 * @return
	 */
	@RequestMapping(value="machineUser", method=RequestMethod.POST)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object addMachineUser() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			param.put("id", UID.getUUID());
			this.machineDao.addMachineUser(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	/**
	 * :更新机器用户信息
	 * @return
	 */
	@RequestMapping(value="machineUser", method=RequestMethod.PUT)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object updateMachineUser() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			this.machineDao.updateMachineUser(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
	/**
	 * :删除机器用户信息
	 * @return
	 */
	@RequestMapping(value="machineUser", method=RequestMethod.DELETE)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object delMachineUser() {
		Map<String, Object> param = super.getSteamParamters();
		try {
			this.machineDao.delMachineUser(param);
			return R.success();
		} catch (Exception e) {
			return R.failure(e.getMessage());
		}
	}
	
}
