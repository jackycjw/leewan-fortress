package com.leewan.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leewan.dao.dic.DicDao;
import com.leewan.util.BaseController;
import com.leewan.util.R;

@RestController
@RequestMapping("dic")
public class DicController extends BaseController {

	@Autowired
	DicDao dicDao;
	
	@RequestMapping("queryDics")
	public Object queryDics(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> paramters = getParamters();
		if(!paramters.containsKey("dicNbr")) {
			//
			return R.failure().setData("缺少入参: dicNbr");
		}
		List<Map> list = this.dicDao.queryDics(paramters);
		return R.success().setData(list);
	}
	
}
