package com.leewan.util.interceptor;

import java.util.List;

import org.apache.ibatis.session.RowBounds;


public class PageInfo extends RowBounds{
	//页码，默认是第一页  
    private int pageNo = 1;
    //每页显示的记录数，默认是15  
    private int pageSize = 15;
    //总记录数  
    private int totalRecord;
    //总页数  
    private int totalPage;
    //对应的当前页记录  
    private List list;

	public PageInfo(int offset,int limit){
		super(0,limit);
		this.pageSize = limit;
		//前台入参是是页码
		this.pageNo = offset; 
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;  
		int totalPage = totalRecord%pageSize==0 ? totalRecord/pageSize : totalRecord/pageSize + 1;  
		this.setTotalPage(totalPage);  
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}
    
}
