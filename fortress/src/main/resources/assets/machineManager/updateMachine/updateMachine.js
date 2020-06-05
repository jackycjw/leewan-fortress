$(function(){

	Vue.component('updateMachine', {
        template: '#updateMachine ',
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
    	updateMachine: function(){
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/machine";
    		put(url,managerMachine.state.machine, function(res){
    			try {
    				managerMachine.state.machine.hostname = "";
    				managerMachine.state.machine.host = "";
    				managerMachine.state.machine.remote_port = 22;
    				managerMachine.state.dialogUpdateMachineVisible = false;
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
