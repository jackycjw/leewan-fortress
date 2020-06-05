$(function(){

	Vue.component('addUser', {
        template: '#addUser ',
    mixins: [mixin_basic],
    data: function () {
    	return {
    		loading: false,
        	code: "",
        	name:"",
        	password:""
        };
    },
    created: function(){
    },
    mounted:function(){
    }, 
    methods: {
    	addUser: function(){
    		this.loading = true;
    		var vueThis = this;
    		var url = getContextPath() + "/user/user";
    		post(url,{code:this.code, name:this.name,password:this.password}, function(res){
    			try {
    				vueThis.code="";
    				vueThis.name="";
    				vueThis.password="";
    				EventBus.$emit('add_user_success')
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
