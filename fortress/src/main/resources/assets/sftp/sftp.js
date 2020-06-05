// @ts-nocheck
$(function(){
	//设置标题
	window.document.title="SFTP-"+getQueryVariable("host")
	
var app = new Vue({
    el: '#fileList',
    data: {
    	headers:{
    		"token":localStorage.getItem("chenjwToken")
    	},
    	sftp:{
    		machineUserId: getQueryVariable("id"),
        	path:".",
        	isFolder:false,
        	relativePath:"."
    	},
    	relativePath: "",
    	reload:{},
    	leftWidth: window.innerWidth - 450,
    	totalHeight: window.innerHeight-20,
    	loading: false,
    	fileList:[],
    	hideFiles:[],
    	menuVisible: false,
    	currentRow: {},
    },
    created: function(){
    	window.aa = this;
    	this.queryFileList();
    },
    mounted: function(){
    	var file = $(document.getElementsByName("file"));
    	if(this.sftp.isFolder){
			file.attr("directory",true);
			file.attr("webkitdirectory",true);
		}else{
			file.removeAttr("directory");
			file.removeAttr("webkitdirectory");
		}
    },
    methods: {
    	queryFileList: function(){
    		var vueThis = this;
    		vueThis.loading = true;
    		var p = {
    			machineUserId:this.sftp.machineUserId,
    			path:this.sftp.path
    		}
    		post(getContextPath() + "/sftp/file",p, function(res){
    			try {
    				if(res.code!=1000){
    					error(res.msg);
    					return;
    				}
    				var hideFiles = [];
    				var fileList = [];
    				for(var i=0;i<res.data.length;i++){
    					var item = res.data[i];
    					var name = item.name;
    					if(name == '..'){
    						fileList.splice(0,0,item)
    					}else if(name.startsWith(".")){
    						hideFiles.push(item);
    					}else{
    						fileList.push(item);
    					}
    				}
    				vueThis.hideFiles = hideFiles;
    				vueThis.fileList = fileList;
    				vueThis.sftp.path = res.path;
				} catch (e) {
					console.error(e);
				} finally{
					vueThis.loading = false;
					vueThis.$nextTick(function(){
						vueThis.totalHeight= window.innerHeight-20;
						vueThis.sortChange();
					});
					
				}
    		});
    	},
    	handleFile: function(row, column, event){
    		if(row.type == FILE_TYPE_FOLDER){
    			if(!this.sftp.path.endsWith("/")){
    				this.sftp.path += "/"; 
    			}
    			this.sftp.path += row.name;
    			this.queryFileList();
    		}
    	},
    	onChange: function(file){
    		if(this.sftp.isFolder){
    			var relativePath  = file.raw.webkitRelativePath;
        		var idx = relativePath.indexOf("/");
        		this.relativePath = relativePath.substring(0,idx);
    		}else{
    			this.relativePath = "";
    		}
    	},
    	uploadSuccess: function(response, file, fileList){
    		if(response.code != 1000){
    			error(res.msg);
    			return;
    		}
    		var refresh = true;
    		if(this.sftp.isFolder && this.reload[this.relativePath] == null){
    			this.reload[this.relativePath] = 1;
    			this.queryFileList();
    			return;
    		}
    		if(!this.sftp.isFolder){
    			this.queryFileList();
    		}
    		
    	},
    	
    	beforeUpload: function(file){
    		if(this.sftp.isFolder){
    			var relativePath  = file.webkitRelativePath;
        		var idx = relativePath.lastIndexOf("/");
        		this.sftp.relativePath = relativePath.substring(0,idx);
    		}
    		
    	},
    	switchMode: function(){
    		window.event.stopPropagation();
    		var file = $(document.getElementsByName("file"));
    		if(this.sftp.isFolder){
    			file.attr("directory",true);
    			file.attr("webkitdirectory",true);
    		}else{
    			file.removeAttr("directory");
    			file.removeAttr("webkitdirectory");
    		}
    	},
    	formatSize: function(row){
    		if(row.name=='energy-service.log'){
    			debugger;
    		}
    		if(row.type==1){
    			return row.size;
    		}
    		var size = row.size;
    		if(size>1024*1024*1024){
    			return Math.round(size/1024/1024/1024*100)/100 +"G"
    		}
    		if(size>1024*1024){
    			return Math.round(size/1024/1024*100)/100 +"M"
    		}
    		if(size>1024){
    			return Math.round(size/1024*100)/100 +"K"
    		}
    		return size;
    	},
    	sortByName: function(a,b){
    		var type = a.type - b.type;
    		return type==0?a.name.localeCompare(b.name):type;
    	},
    	sortBySize: function(a,b){
    		var type = a.type - b.type;
    		return type==0?a.size-b.size:type;
    	},
    	sortByTime: function(a,b){
    		var type = a.type - b.type;
    		return type==0?a.modifyTime.localeCompare(b.modifyTime):type;
    	},
    	sortChange: function(a,b){
    		var tableData = this.$refs.fileListTable.tableData;
    		var idx = 0;
    		for(var i=0;i<tableData.length;i++){
    			if(tableData[i].name == '..'){
    				idx = i;
    				break;
    			}
    		}
    		var o = tableData[idx];
    		tableData.splice(idx,1);
    		tableData.splice(0,0,o)
    	},
    	rightClick(row, column, event) {
    	      this.menuVisible = false; // 先把模态框关死，目的是 第二次或者第n次右键鼠标的时候 它默认的是true
    	      this.menuVisible = true; // 显示模态窗口，跳出自定义菜单栏
    	      var menu = document.querySelector('#menu');
    	      this.styleMenu(menu);
    	      event.preventDefault();
    	      event.stopPropagation();
    	      this.currentRow = row;
    	      this.$refs.fileListTable.setCurrentRow(row);
    	},
    	styleMenu(menu) {
    	      if (event.clientX > 1800) {
    	        menu.style.left = event.clientX - 100 + 'px';
    	      } else {
    	        menu.style.left = event.clientX + 1 + 'px';
    	      }
    	      document.addEventListener('click', this.foo); // 给整个document新增监听鼠标事件，点击任何位置执行foo方法
    	      if (event.clientY > 700) {
    	        menu.style.top = event.clientY - 30 + 'px';
    	      } else {
    	        menu.style.top = event.clientY - 10 + 'px';
    	      }
    	},
    	downLoad: function(){
    		var url = "/sftp/downLoad?";
    		url += "machineUserId="+this.sftp.machineUserId;
    		url += "&name="+this.currentRow.name;
    		url += "&path="+this.sftp.path;
    		url += "&token="+localStorage.getItem("chenjwToken");
    		window.open(url);
    	},
    	del: function(){
    		this.loading = true;
    		var url = "/sftp/delete?";
    		url += "machineUserId="+this.sftp.machineUserId;
    		url += "&name="+this.currentRow.name;
    		url += "&path="+this.sftp.path;
    		url += "&type="+this.currentRow.type;
    		url += "&token="+localStorage.getItem("chenjwToken");
    		
    		var vueThis = this;
    		get(url,function(res){
    			try {
    				if(res.code!=1000){
    					error(res.msg);
    					return;
    				}
    				vueThis.queryFileList();
				} catch (e) {
					console.error(e);
				} finally{
					vueThis.loading = false;
				}
    		});
    	},
    	mkdir: function(){
    		var vueThis = this;
    		this.$prompt('输入文件夹名称', '提示', {
    	          confirmButtonText: '确定',
    	          cancelButtonText: '取消',
    	          inputPattern: /^[a-zA-Z0-9]+$/,
    	          inputErrorMessage: '字母或数字'
    	        }).then(({ value }) => {
    	        	debugger;
    	        	var p = {
    	        			machineUserId: vueThis.sftp.machineUserId,
    	        			path: vueThis.sftp.path,
    	        			name: value
    	        	};
    	        	post("/sftp/mkdir",p,function(res){
    	    			try {
    	    				if(res.code!=1000){
    	    					error(res.msg);
    	    					return;
    	    				}
    	    				vueThis.queryFileList();
    					} catch (e) {
    						console.error(e);
    					} finally{
    						vueThis.loading = false;
    					}
    	    		});
    	        }).catch(() => {
    	          this.$message({
    	            type: 'info',
    	            message: '取消输入'
    	          });       
    	        });
    	},
    	mkdirCmd: function(e){
    		e.preventDefault();
    		this.menuVisible = true;
    		this.currentRow = null;
    		var menu = document.querySelector('#menu');
  	      	this.styleMenu(menu);
    	}
    }
})
})
