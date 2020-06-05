
//设置标题
window.document.title="SSH-"+getQueryVariable("host")
var cols = Math.floor((innerWidth-50)/9);
var rows = Math.floor((innerHeight-50)/17);
var wp = cols * 9;
var hp = rows * 17;
	window.terminal = new Terminal({
		rows: rows,
		cols:cols
	});
	terminal.open(document.getElementById('terminal'));
	var auditId = getQueryVariable("auditId");
	var token = localStorage.getItem("chenjwToken");
	var ws = "ws://"+window.location.host+"/audit?auditId="+auditId+"&token="+token;
	var socket = new WebSocket(ws);
	window.socket = socket;
	socket.onopen = function() {
		next();
	};
	socket.onmessage = function(msg) {
		if(msg.length==0){
			clearInterval(nextThread);
			return;
		}
		terminal.write(msg.data);
		if(msg.data.length >= 1000){
			next();
		} else {
			setTimeout(next,100);
		}
	};
	socket.onclose = function() {
		console.log("websocket已关闭");
	};
	socket.onerror = function(e) {
		console.log("websocket发生了错误");
	}
	
	terminal.onKey(function(event){
		event.domEvent.preventDefault();
		socket.send(event.key);
	});
	
	
	
	function next(){
		try {
			socket.send("0");
		} catch (e) {
			console.error(e);
			clearInterval(nextThread)
		}
	}
