// @ts-nocheck
$(function(){
	window.homePageCode = "machine";
var app = new Vue({
    el: '#app',
    data: {
    	isCollapse: true,
        menu:[],
        activeIndex: "1.0",
        btnMsg:"隐藏",
        direction:"rtl",
        modifyPwdVisible: false,
        newPassword:"",
        defaultNav: {},
        userInfo:{}
    },
    created: function(){
    	window.app1= this;
    	this.init();
    	this.loadUserInfo();
    },
    methods: {
    	logout: function(){
    		var vueThis = this;
    		url = getContextPath() + "/user/logout";
    		
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
    	modifyPwd: function(){
    		var vueThis = this;
    		this.$confirm("是否确认修改密码", '提示', {
  	          confirmButtonText: '确定',
  	          cancelButtonText: '取消',
  	          type: 'warning'
  	        }).then(() => {
  	        	url = getContextPath() + "/user/password";
  	        	post(url, {password: vueThis.newPassword}, function(res){
  	    			try {
  	    				if(res.code="1000"){
  	    					message("密码修改成功，请刷新页面重新登录！");
  	    				}
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
    	initNav: function(data){
    		var hash = getHash();
    		var defNav = null;
    		var hashNav = null;
    		for(var i in data){
				if(data[i].code == homePageCode){
					defNav = data[i];
				}
				if(data[i].code == hash){
					hashNav = data[i];
				}
			}
    		if(defNav != null){
    			this.defaultNav  = defNav;
    		}
    		if(hashNav != null){
    			this.defaultNav  = hashNav;
    		}
    		this.activeIndex = this.defaultNav.id;
    	},
    	loadUserInfo: function(){
    		var vueThis = this;
    		post(getContextPath() + "/user/loadUser",{}, function(res){
    			try {
    				userStore.state.user = res.data;
				} catch (e) {
					console.error(e);
				}
    		});
    	},
    	init: function(){
    		var vueThis = this;
    		post("/module/queryModules",{}, function(res){
    			try {
    				
    				var data = res.data;
    				var map = {};
    				vueThis.initNav(data);
    				for(var i in data){
    					var id = data[i].id;
    					map[id] = data[i];
    				}
    				var rs = [];
    				for(var i in data){
    					var id = data[i].id;
    					var pid = data[i].pid;
    					if(!!pid){
    						if(map[pid].children == null){
    							map[pid].children = [];
    						}
    						map[pid].children.push(data[i]);
    					}else{
    						rs.push(data[i]);
    					}
    				}
    				vueThis.menu = rs;
    				vueThis.defaultNavigate();
				} catch (e) {
					console.error(e);
				}
    		});
    	},
    	defaultNavigate: function(){
    		this.onNavigate(this.defaultNav);
    	},
        handleMenu: function(key, path){
        },
        onNavigate: function(menu){
            router.onNavigate(menu);
        }
        
    }
})
})
