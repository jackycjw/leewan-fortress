// @ts-nocheck
$(function(){
registerComponent("distributeMachine", true);
registerComponent("addUser", true);

var app = new Vue({
    el: '#userManager',
    mixins: [mixin_basic],
    data: {
    	loading: false,
    	dialogVisible: false,
    	statusVisible: false,
    	code:"",
    	name:"",
    	userList:[],
    	role: '1'
    },
    beforeDestroy: function(){
    },
    
    mounted:function(){
    	this.queryUserList();
    	this.queryRoleList();
    	var vueThis = this;
    	EventBus.$on('add_user_success', function(callback){
    		managerUser.state.dialogUserVisible = false;
			vueThis.queryUserList();
		});
    }, 
    methods: {
    	queryUserList: function(){
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/user/users";
    		post(url,{code:this.code, name:this.name}, function(res){
    			try {
    				vueThis.userList = res.data;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	queryRoleList: function(){
    		
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/user/roles";
    		get(url, function(res){
    			try {
    				managerUser.state.roleList = res.data;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	distributeMachine(row){
    		managerUser.state.user.id = row.id;
    		managerUser.state.user.name = row.name;
    		managerUser.state.dialogVisible=true
    	},
    	distributeRole(row){
    		managerUser.state.user.id = row.id;
    		managerUser.state.user.name = row.name;
    	},
    	modifyRole: function(roleId){
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/user/distributeRole";
    		post(url, {userId: managerUser.state.user.id, roleId: roleId}, function(res){
    			try {
    				vueThis.queryUserList();
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	changeStatus: function(row){
    		var msg = row.status==1?"确定禁用该用户吗？":"确定启动该用户吗？";
    		var vueThis = this;
    		
    		this.$confirm(msg, '提示', {
    	          confirmButtonText: '确定',
    	          cancelButtonText: '取消',
    	          type: 'warning'
    	        }).then(() => {
    	        	var url = getContextPath() + "/user/status";
    	        	var p = {
    	        		userId: row.id,
    	        		status: row.status==1?0:1
    	        	}
    	        	post(url, p, function(res){
    	    			try {
    	    				
    	    				vueThis.queryUserList();
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
    	
    	delUser: function(row){
    		var vueThis = this;
    		
    		this.$confirm("确定删除"+row.code+"用户", '提示', {
    	          confirmButtonText: '确定',
    	          cancelButtonText: '取消',
    	          type: 'warning'
    	        }).then(() => {
    	        	var url = getContextPath() + "/user/user";
    	        	var p = {
    	        		userId: row.id
    	        	}
    	        	del(url, p, function(res){
    	    			try {
    	    				vueThis.queryUserList();
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
    	}
    }
})
})
