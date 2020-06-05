$(function(){

	Vue.component('addMachine', {
        template: '#addMachine ',
    mixins: [mixin_basic],
    data: function () {
    	return {
    		loading: false,
        	host: "",
        	hostname:"",
        	remote_port:22
        };
    },
    created: function(){
    },
    mounted:function(){
    }, 
    methods: {
    	addMachine: function(){
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/machine";
    		post(url,{host:this.host, hostname:this.hostname,remote_port:this.remote_port}, function(res){
    			try {
    				vueThis.host="";
    				vueThis.hostname="";
    				vueThis.remote_port=22;
    				managerMachine.state.dialogAddMachineVisible = false;
    				EventBus.$emit('add_machine_success')
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
