package com.leewan.dao.audit;

import java.util.List;
import java.util.Map;

import com.leewan.util.interceptor.PageInfo;

public interface AuditDao {

	public List<Map<String, Object>> queryAudits(Map<String, Object> param, PageInfo pageInfo);
}
