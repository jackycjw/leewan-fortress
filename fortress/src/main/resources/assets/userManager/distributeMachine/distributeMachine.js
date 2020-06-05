$(function(){

	Vue.component('distributeMachine', {
        template: '#distributeMachine ',
    mixins: [mixin_basic],
    data: function () {
    	return {
        	loading: false,
        	machineList:[]
        };
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
    		url = getContextPath() + "/distributeMachine";
    		post(url,{userId:managerUser.state.user.id}, function(res){
    			try {
    				vueThis.machineList = res.data;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	lineclass: function(row, rowIndex){
    		if(row.row.user_id != null){
    			return "auth";
    		}else{
    			return "unauth";
    		}
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
    	dilogTitle: function(){
    		return "用户："+managerUser.state.user.name;
    	},
    	userId: function(){
    		return managerUser.state.user.id;
    	}
    },
    watch: {
    	userId: function(){
    		this.queryMachineList();
    	}
    }
})
})
