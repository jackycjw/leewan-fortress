$(function(){

	Vue.component('machineUser', {
        template: '#machineUser ',
    mixins: [mixin_basic],
    data: function () {
    	return {
        	loading: false,
        	user:"",
        	addUserVisible: false,
        	addNewMachineUser: false,
        	newItem:{
        		user:"",
        		password:""
        	},
        	machineList:[]
        };
    },
    created: function(){
    },
    mounted:function(){
    	this._init();
    }, 
    methods: {
    	_init: function(){
    		this.queryMachineUserList();
    	},
    	queryMachineUserList: function(){
    		var vueThis = this;
    		url = getContextPath() + "/machineUser?id="+managerMachine.state.machine.id+
    			"&user="+this.user;
    		
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
    	addMachineUser: function(){
    		this.loading = false;
    		var vueThis = this;
    		url = getContextPath() + "/machineUser";
    		var p = {
    			machineId:managerMachine.state.machine.id,
    			user: this.newItem.user,
    			password: this.newItem.password
    		};
    		post(url,p, function(res){
    			try {
    				vueThis.addNewMachineUser = false;
    				vueThis.queryMachineUserList();
    				vueThis.newItem.user = "";
    				vueThis.newItem.password = "";
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	delMachineUser: function(row){
    		this.loading = false;
    		var vueThis = this;
    		url = getContextPath() + "/machineUser";
    		del(url,{id: managerMachine.state.machine.id}, function(res){
    			try {
    				vueThis.queryMachineUserList();
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	disableMachine: function(row){
    		var vueThis = this;
    		vueThis.loading = true;
    		url = getContextPath() + "/disableMachine";
    		post(url,{userId:managerUser.state.user.id, machineUserId: row.id}, function(res){
    			try {
    				vueThis.queryMachineList();
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	ableMachine: function(row){
    		var vueThis = this;
    		vueThis.loading = true;
    		url = getContextPath() + "/ableMachine";
    		post(url,{userId:managerUser.state.user.id, machineUserId: row.id}, function(res){
    			try {
    				vueThis.queryMachineList();
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	}
    },
    computed:{
    	machineId: function(){
    		return managerMachine.state.machine.id;
    	}
    },
    watch: {
    	machineId: function(){
    		this.queryMachineUserList();
    	}
    }
})
})
