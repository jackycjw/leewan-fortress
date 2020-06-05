package com.leewan.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leewan.bean.User;
import com.leewan.util.BaseController;
import com.leewan.util.R;


@RestController
@RequestMapping("module")
public class ModuleController extends BaseController {

	
	@RequestMapping("queryModules")
	public Object queryModules(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = super.getUser();
		return R.success().setData(menus.get(user.getRole().getId()));
	}
	
	static Map<String, List<Map<String, Object>>> menus = new HashMap<String, List<Map<String,Object>>>();
	
	static {
		List<Map<String, Object>> adminMenu = new ArrayList<Map<String,Object>>();
		R menu = R.ok().put("code", "machine").put("node_type", "0").put("name", "机器列表").put("id", 0);
		adminMenu.add(menu);
		menu = R.ok().put("code", "userManager").put("node_type", "0").put("name", "人员管理").put("id", 2).put("pid", 1);
		adminMenu.add(menu);
		menu = R.ok().put("node_type", "1").put("name", "运维管理").put("id", 1);
		adminMenu.add(menu);
		menu = R.ok().put("code", "machineManager").put("node_type", "0").put("name", "机器管理").put("id", 3).put("pid", 1);
		adminMenu.add(menu);
		menu = R.ok().put("code", "audit").put("node_type", "0").put("name", "SSH审计").put("id", 4).put("pid", 1);
		adminMenu.add(menu);
		menu = R.ok().put("code", "sftpLog").put("node_type", "0").put("name", "SFTP审计").put("id", 5).put("pid", 1);
		adminMenu.add(menu);
		menus.put("0", adminMenu);
		
		
		List<Map<String, Object>> userMenu = new ArrayList<Map<String,Object>>();
		menu = R.ok().put("code", "machine").put("node_type", "0").put("name", "机器列表").put("id", 0);
		userMenu.add(menu);
		menus.put("1", userMenu);
	}
	
}
