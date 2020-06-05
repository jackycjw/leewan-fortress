$(function(){

var app = new Vue({
    el: '#sftpLog',
    mixins: [mixin_basic],
    data: {
    	loading: false,
    	types:[
    		{label:"文件上传",value:0},
    		{label:"新建文件夹",value:1},
    		{label:"删除文件",value:2},
    		{label:"删除文件夹",value:3},
    	],
    	page:{
    		total:0,
    		pageSize:10,
    		pageIndex:1,
    	},
    	username:"",
    	type:"",
    	timeRange:[],
    	logList:[],
    },
    created: function(){
    	window.audit = this;
    },
    mounted:function(){
    	window.aa = this;
    	this._init();
    }, 
    methods: {
    	_init: function(){
    		this.querySftpLogs();
    	},
    	querySftpLogs: function(){
    		var vueThis = this;
    		url = getContextPath() + "/sftp/sftpLog";
    		url += "?pageIndex="+this.page.pageIndex+"&pageSize="+this.page.pageSize
    		url += "&name="+this.username;
    		url += "&type="+this.type;
    		if(this.timeRange != null && this.timeRange.length > 0){
    			url += "&startTime="+this.timeRange[0].Format("yyyyMMddhhmmss");
    		}
    		if(this.timeRange != null && this.timeRange.length > 1){
    			url += "&endTime="+this.timeRange[1].Format("yyyyMMddhhmmss");
    		}
    		get(url, function(res){
    			try {
    				debugger;
    				vueThis.logList = res.data.list;
    				vueThis.page.total = res.data.totalRecord;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	pageChanged: function(pageIdx){
    		this.page.pageIndex = pageIdx;
    		this.querySftpLogs();
    	},
    	formateType: function(row){
    		var type = row.type;
    		if(type == "0"){
    			return "上传文件";
    		} else if(type == "1"){
    			return "新建文件夹";
    		} else if(type == "2"){
    			return "删除文件";
    		} else if(type == "3"){
    			return "删除文件夹";
    		}
    		return "其它";
    	}
    	
    	
    }
})
})
