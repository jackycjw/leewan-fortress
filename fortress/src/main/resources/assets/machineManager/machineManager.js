$(function(){
	registerComponent("addMachine", true);
	registerComponent("updateMachine", true);
	registerComponent("machineUser", true);
	
var app = new Vue({
    el: '#machineManager',
    mixins: [mixin_basic],
    data: {
    	loading: false,
    	hostname:"",
    	host:"",
    	machineList:[],
    	direction:"rtl"
    	
    },
    created: function(){
    },
    mounted:function(){
    	this._init();
    	var vueThis = this;
    	EventBus.$on('add_machine_success', function(callback){
			vueThis.queryMachineUserList();
		});
    }, 
    methods: {
    	_init: function(){
    		this.queryMachineUserList();
    	},
    	queryMachineUserList: function(){
    		var vueThis = this;
    		url = getContextPath() + "/machine?hostname="+this.hostname+"&host="+this.host;
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
    	delMachine: function(row){
    		var vueThis = this;
    		this.$confirm("是否确认删除该机器？", '提示', {
  	          confirmButtonText: '确定',
  	          cancelButtonText: '取消',
  	          type: 'warning'
  	        }).then(() => {
  	    		url = getContextPath() + "/machine";
  	    		del(url, {id:row.id},function(res){
  	    			try {
  	    				vueThis.queryMachineUserList();
  					} catch (e) {
  						console.error(e);
  					} finally {
  						vueThis.loading = false;
  					}
  	    		});
  	        }).catch(() => {
  	          this.$message({
  	            type: 'info',
  	            message: '已取消删除'
  	          });          
  	        });
    	},
    	updateMachine: function(row){
    		managerMachine.state.machine = JSON.parse(JSON.stringify(row));
    		managerMachine.state.dialogUpdateMachineVisible = true;
    	},
    	managerMachineUser: function(row){
    		managerMachine.state.machine = JSON.parse(JSON.stringify(row));
    		managerMachine.state.dialogManagerMachineUserVisible = true
    	}
    }
})
})
