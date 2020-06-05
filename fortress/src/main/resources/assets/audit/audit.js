$(function(){

var app = new Vue({
    el: '#audit',
    mixins: [mixin_basic],
    data: {
    	loading: false,
    	page:{
    		total:0,
    		pageSize:10,
    		pageIndex:1,
    	},
    	auditList:[],
    	username:"",
    	hostname:"",
    	timeRange:[]
    	
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
    		this.queryAudits();
    	},
    	queryAudits: function(){
    		if(arguments.length == 1){
    			if(typeof(arguments[0]) == "number"){
    				this.page.pageIndex = arguments[0];
    			}
    			
    		}
    		var vueThis = this;
    		url = getContextPath() + "/audit";
    		url += "?pageIndex="+this.page.pageIndex+"&pageSize="+this.page.pageSize
    		url += "&username="+this.username;
    		url += "&hostname="+this.hostname;
    		if(this.timeRange != null && this.timeRange.length > 0){
    			url += "&startTime="+this.timeRange[0].Format("yyyyMMddhhmmss");
    		}
    		if(this.timeRange != null && this.timeRange.length > 1){
    			url += "&endTime="+this.timeRange[1].Format("yyyyMMddhhmmss");
    		}
    		get(url, function(res){
    			try {
    				vueThis.auditList = res.data.list;
    				vueThis.page.total = res.data.totalRecord;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	playBack: function(row){
    		var url = "/page/playback?auditId="+row.id;
    		window.open(url);
    	}
    	
    	
    }
})
})
