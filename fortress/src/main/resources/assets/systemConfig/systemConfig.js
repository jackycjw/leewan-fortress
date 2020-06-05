$(function(){
	
var app = new Vue({
    el: '#systemConfig',
    mixins: [mixin_basic],
    data: {
    	serviceName:"",
    	serviceList:[],
    	loading: false,
    	
    	
    	machineIP:'',
    	condition: {
    		compName: "",
    		invoiceDate: [],
    		invoiceType:"",
    		arriveDate:[],
    	},
    	pageInfo:{
    		pageIndex: 1,
    		pageSize: 15,
    		total: 0
    	},
    	workerList: [],
    	memUnits:[{"code":'K', "name":"KB"}, {"code":'M', "name":"MB"}, {"code":'G', "name":"GB"}],
    	memUnit:'G'
    },
    beforeDestroy: function(){
    	
    },
    created: function(){
    },
    mounted:function(){
    	this._init();
    }, 
    methods: {
    	_init: function(){
    		this.queryServiceList();
    	},
    	newLoadBalance: function(){
    		serviceStore.state.service = {}
    		router.reference("newLoadBalance",this);
    		serviceStore.state.isEdit = true
    	},
    	queryServiceList: function(){
    		var vueThis = this;
    		url = getContextPath() + "/service/query";
    		post(url,{name: this.serviceName}, function(res){
    			try {
    				vueThis.serviceList = res.data;
				} catch (e) {
					console.error(e);
				} finally {
					vueThis.loading = false;
				}
    		});
    	},
    	detail: function(service){
    		service.container = JSON.parse(service.container)
    		serviceStore.state.service = service
    		router.reference("newLoadBalance",this);
    		serviceStore.state.isEdit = false
    	}

    	
    	
    }
})
})
