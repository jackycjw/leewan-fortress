
window.serviceStore = new Vuex.Store({
    state: {
    	service:{
    	},
    	isEdit: true
    }
});

window.userStore = new Vuex.Store({
    state: {
    	user:{
    		code: "",
            role: {
                code: "",
                name: "",
                authLevel: 1
            },
            authority: [],
            name: "",
    	},
    	outerRoles:[]
    }
});

window.dicStore = new Vuex.Store({
    state: {
        dic: {}
    },
    getters: {
        getDicValue: function (state, nbr, code) {
        	
        },
        getDicItem: function(state, nbr){
        	return (nbr)=>{
        		if(state.dic[nbr] == null){
        			var url = getContextPath() + "/dic/queryDics";
            		post(url,{dicNbr: nbr}, function(res){
            			state.dic[nbr] = res.data;
            		}, true);
        		}
        		return state.dic[nbr];
        	};
        }
    },
    mutations: {
        setUserInfo: function (state, userInfo) {
            state.userInfo = userInfo;
        },
    },
    actions: {
        setUserInfo: function (context, user) {
        },
    }
});


window.managerUser = new Vuex.Store({
    state: {
    	user:{
    		id:"",
    		name:""
    	},
    	roleList:[],
    	dialogVisible: false,
    	dialogUserVisible: false
    }
});


window.managerMachine = new Vuex.Store({
    state: {
    	dialogAddMachineVisible: false,
    	dialogUpdateMachineVisible: false,
    	dialogManagerMachineUserVisible: false,
    	machine:{}
    }
});
