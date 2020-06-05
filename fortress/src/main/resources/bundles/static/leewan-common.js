const FILE_TYPE_FILE = 0;
const FILE_TYPE_FOLDER = 1;
const FILE_TYPE_LINK = 2;
const FILE_TYPE_SOCKET = 3;
const FILE_TYPE_DEVICE = 4;
const FILE_TYPE_OTHER = 5;

Date.prototype.Format = function(fmt)   
{ //author: meizz   
  var o = {   
    "M+" : this.getMonth()+1,                 //月份   
    "d+" : this.getDate(),                    //日   
    "h+" : this.getHours(),                   //小时   
    "m+" : this.getMinutes(),                 //分   
    "s+" : this.getSeconds(),                 //秒   
    "q+" : Math.floor((this.getMonth()+3)/3), //季度   
    "S"  : this.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}


window.getContextPath = function() {
    var pathName = document.location.pathname;
    var index = pathName.substr(1).indexOf("/");
    var result = pathName.substr(0,index+1);
    return result;
}

window.getHash= function(){
	var match = window.location.href.match(/#(.*)$/);
    return match ? match[1] : '';
}

window.getOldHash= function(oldUrl){
	var match = oldUrl.match(/#(.*)$/);
    return match ? match[1] : '';
}

window.getContainer= function(hash){
	return $("[container-name='"+hash+"']");
}

window.registerComponent = function(name, async){
	var comp = $("#components").find("#"+name);
	if(comp.length > 0){
		//该组件已存在，无需再次加载
		return;
	}
	$.ajax({
		url:"/page/"+name,
		type:"get",
		async:async?false: true,
		success: function(data){
			$("#components").append(data);
		}
	});
};

window.message=function(msg){
	var vue = new Vue();
	vue.$message({
	    type: 'success',
	    message: msg
	  });	
}


window.error=function(msg){
	var vue = new Vue();
	vue.$notify.error(msg);
}

window.post = function(url, data, callBack, async){
	if(typeof(data) == "object"){
		data = JSON.stringify(data)
	}
	$.ajax({
		url: url,
		type:"post",
		data: data,
		dataType:"json",
		contentType: "application/json",
		headers:{token:localStorage.getItem("chenjwToken")},
		async:async?false: true,
		success: function(data){
			try {
				var tmp = data;
				try {var tmp = JSON.parse(data);} catch (e) {}
				
				if(tmp.code == "1001"){
					error(data.msg);
				}
				
				if(tmp.code == "1002"){
					window.location.href = getContextPath() + "/page/login";
					return;
				}
				if(tmp.code == "1003"){
					error("您没有权限进行该操作，请联系管理员！");
				}
				
			} catch (e) {
				// TODO: handle exception
			}
			if(!!callBack){
				callBack(data);
			}
		}
	});
};


window.del = function(url, data, callBack, async){
	if(typeof(data) == "object"){
		data = JSON.stringify(data)
	}
	$.ajax({
		url: url,
		type:"delete",
		data: data,
		dataType:"json",
		contentType: "application/json",
		headers:{token:localStorage.getItem("chenjwToken")},
		async:async?false: true,
		success: function(data){
			try {
				var tmp = data;
				try {var tmp = JSON.parse(data);} catch (e) {}
				
				if(tmp.code == "1001"){
					error(data.msg);
				}
				
				if(tmp.code == "1002"){
					window.location.href = getContextPath() + "/page/login";
					return;
				}
				if(tmp.code == "1003"){
					error("您没有权限进行该操作，请联系管理员！");
				}
				
			} catch (e) {
				// TODO: handle exception
			}
			if(!!callBack){
				callBack(data);
			}
		}
	});
};


window.put = function(url, data, callBack, async){
	if(typeof(data) == "object"){
		data = JSON.stringify(data)
	}
	$.ajax({
		url: url,
		type:"PUT",
		data: data,
		dataType:"json",
		contentType: "application/json",
		headers:{token:localStorage.getItem("chenjwToken")},
		async:async?false: true,
		success: function(data){
			try {
				var tmp = data;
				try {var tmp = JSON.parse(data);} catch (e) {}
				
				if(tmp.code == "1001"){
					error(data.msg);
				}
				
				if(tmp.code == "1002"){
					window.location.href = getContextPath() + "/page/login";
					return;
				}
				if(tmp.code == "1003"){
					error("您没有权限进行该操作，请联系管理员！");
				}
				
			} catch (e) {
				// TODO: handle exception
			}
			if(!!callBack){
				callBack(data);
			}
		}
	});
};


window.get = function(url, callBack, async){
	$.ajax({
		url: url,
		type:"get",
		dataType:"json",
		headers:{token:localStorage.getItem("chenjwToken")},
		async:async?false: true,
		success: function(data){
			try {
				var tmp = data;
				try {var tmp = JSON.parse(data);} catch (e) {}
				
				if(tmp.code == "1002"){
					window.location.href = getContextPath() + "/page/login";
					return;
				}
				if(tmp.code == "1003"){
					error("您没有权限进行该操作，请联系管理员！");
					return;
				}
			} catch (e) {
				// TODO: handle exception
			}
			if(!!callBack){
				callBack(data);
			}
		}
	});
};

window.ADD_MODE=1
window.UPDATE_MODE=2
window.DETAIL_MODE=3


window.isEmptyString = function(str){
	return str == null || str.length == 0;
}

//全局事件总线
window.EventBus = new Vue(
    {
        methods: {
            //避免重复触发同一个方法
            updateEvent: function (name, f) {
                if (this._events[name] == f) {
                    return;
                } else {
                    this.$on(name, f);
                }
                ;
            }
        }
    }
);


window.getContextPath = function(){
	return "";
}

function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}