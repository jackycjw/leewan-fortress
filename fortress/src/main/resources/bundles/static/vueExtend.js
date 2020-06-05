window.mixin_basic={
	methods: {
		goBack: function(){
			router.goBack();
		},
		_init_: function(){
			
		},
		round: function(data, accuracy){
			if(accuracy == null){
				accuracy = 2;
			}
			var tmp = Math.pow(10,accuracy);
			return Math.round(data*tmp)/tmp;
		},
		formatComputer: function(data, unit){
			if(unit == 'K'){
				return Math.round(100 * data / 1024) / 100 + "KB";
			}
			if(unit == 'M'){
				return Math.round(100 * data / 1024 / 1024) / 100 + "MB";
			}
			if(unit == 'G'){
				return Math.round(100 * data / 1024 / 1024 / 1024) / 100 + "GB";
			}
		},
		formatPercent(data){
			return Math.round(data * 10000)/100 + "%";
		},
		
		formatCompName: function(row){
			if(!!row.nameCh && !!row.nameEn){
				return row.nameCh + "(" + row.nameEn + ")";
			}else if(!!row.nameCh){
				return row.nameCh;
			}else if(!!row.nameEn){
				return row.nameEn;
			}else{
				return "";
			}
		},
		formatDateTime:function(time, format)
		{
			if(!time){
				return "";
			}
			if(typeof time == "string"){
				tArray = time.split("");
				if(time.length > 4){
					tArray.splice(4,0,"-")
				}
				if(time.length > 6){
					tArray.splice(7,0,"-")
				}
				if(time.length > 8){
					tArray.splice(10,0," ")
				}
				if(time.length > 10){
					tArray.splice(13,0,":")
				}
				if(time.length > 12){
					tArray.splice(16,0,":")
				}
				return tArray.join("");
			}
			var d = new Date(time);
			if(!format){
				format = "yyyy-MM-dd";
			}
			return d.Format(format);
		},
		translate: function(nbr, code){
			if(dicStore.state.dic[nbr] == null){
        		var url = getContextPath() + "/dic/queryDics";
        		post(url,{dicNbr: nbr}, function(res){
        			dicStore.state.dic[nbr] = res.data;
        		}, true);
        	}
        	var list = dicStore.state.dic[nbr];
        	if(list != null){
        		for(var key in list){
            		if(list[key].code == code){
            			return list[key].value;
            		}
            	}
        	}
            return "";
		},
		formatMoney: function(amount, code){
			if(amount == null){
				return "0";
			}
			var nbr = "CURRENCY";
			if(dicStore.state.dic[nbr] == null){
        		var url = getContextPath() + "/dic/queryDics";
        		post(url,{dicNbr: nbr}, function(res){
        			dicStore.state.dic[nbr] = JSON.parse(res).data;
        		}, true);
        	}
        	var list = dicStore.state.dic[nbr];
        	var currency;
        	if(list != null){
        		for(var key in list){
            		if(list[key].code == code){
            			currency = list[key];
            		}
            	}
        	}
        	if(currency == null){
        		return amount;
        	}
            return currency.remark + "" + amount;
		}
	},
	computed: {
		addAble: function(){
    		for(i in userStore.state.user.authority){
    			if(userStore.state.user.authority[i] == "NEW"){
    				return true;
    			}
    		}
    	},
    	delAble: function(){
    		for(i in userStore.state.user.authority){
    			if(userStore.state.user.authority[i] == "DELETE"){
    				return true;
    			}
    		}
    	},
    	isEdit: function(){
    		return serviceStore.state.isEdit;
    	},
    	hasService: function(){
    		var service = serviceStore.state.service;
    		if(service == null || service.id == null || service.id == ""){
    			return false;
    		}
    		return true;
    	}
	}
}




window.mixin_finance_child_compo={
		mixins: [mixin_basic],
		mounted: function(){
			this.init();
	    },
	    methods: {
	    	init: function(){
	    		Object.assign(this.$data, this.$options.data());
				if(this.mode == UPDATE_MODE){
					if(this.initUpdateData != null && typeof(this.initUpdateData) == "function"){
						this.initUpdateData();
					}
					return;
				}
				if(this.mode == DETAIL_MODE){
					if(this.initDetailData != null && typeof(this.initDetailData) == "function"){
						this.initDetailData();
					}
					return;
				}
				
				if(this.mode == ADD_MODE){
					if(this.initAddData != null && typeof(this.initAddData) == "function"){
						this.initAddData();
					}
					return;
				}
	    	}
	    }
}