package com.leewan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.leewan.aop.Authority;
import com.leewan.aop.OperateAuth;
import com.leewan.dao.audit.AuditDao;
import com.leewan.util.BaseController;
import com.leewan.util.R;
import com.leewan.util.interceptor.PageInfo;

@RestController
public class AuditController extends BaseController {

	@Autowired
	AuditDao auditDao;
	
	@RequestMapping(value="audit", method=RequestMethod.GET)
	@OperateAuth(value=Authority.ADMIN_ACTION)
	public Object audits() {
		Map<String, Object> paramters = super.getParamters();
		PageInfo pageInfo = super.getPageInfo(paramters);
		List<Map<String, Object>> list = this.auditDao.queryAudits(paramters, pageInfo);
		pageInfo.setList(list);
		return R.success().setData(pageInfo);
	}
}
