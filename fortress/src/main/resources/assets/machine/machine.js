$(function(){

var app = new Vue({
    el: '#machine',
    mixins: [mixin_basic],
    data: {
    	loading: false,
    	machineList:[]
    	
    },
    created: function(){
    },
    mounted:function(){
    	window.aa = this;
    	this._init();
    }, 
    methods: {
    	_init: function(){
    		this.queryMachineList();
    	},
    	queryMachineList: function(){
    		var vueThis = this;
    		url = getContextPath() + "/queryUserMachineUser";
    		get(url, function(res){
    			try {
    				vueThis.machineList = res.data;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	openSSH: function(row){
    		var url = "/page/ssh?host="+row.host+"&id="+row.machineUserId+"&user="+row.user;
    		window.open(url);
    	},
    	openSFTP: function(row){
    		var url = "/page/sftp?host="+row.host+"&id="+row.machineUserId+"&user="+row.user;
    		window.open(url);
    	},
    	mergeSpan: function({ row, column, rowIndex, columnIndex }){
    		if(columnIndex < 2){
    			var height = 1;
    			var host = row.host;
    			if(rowIndex > 0){
    				if(host == this.machineList[rowIndex-1].host){
    					return {
    			              rowspan: 0,
    			              colspan: 0
    			            };
    				}
    			}
    			for(var i=rowIndex+1;i<this.machineList.length;i++){
    				if(this.machineList[i].host == host){
    					height++;
    				}else{
    					break;
    				}
    			}
    			return {
	              rowspan: height,
	              colspan: 1
	            };
    		}
    	}

    	
    	
    }
})
})
