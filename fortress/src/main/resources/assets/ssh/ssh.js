
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
	var id = getQueryVariable("id");
	var user = getQueryVariable("user");
	var token = localStorage.getItem("chenjwToken");
	var ws = "ws://"+window.location.host+"/ssh?machineUserId="+id+"&user="+user+"&token="+token+"&cols="+cols+"&rows="+rows;
	ws += "&wp="+wp+"&hp="+hp;
	var socket = new WebSocket(ws);
	window.socket = socket;
	socket.onopen = function() {console.log("websocket已打开");};
	socket.onmessage = function(msg) {
		terminal.write(msg.data)
	};
	socket.onclose = function() {console.log("websocket已关闭");};
	socket.onerror = function(e) {console.log("websocket发生了错误");}
	
	terminal.onKey(function(event){
		event.domEvent.preventDefault();
		socket.send(event.key);
	});
	
	terminal.onData(function(e){
		var content = window.event.clipboardData.getData("text/plain");
		socket.send(content);
	});
	
	/*document.getElementById("terminal").addEventListener("paste", function (e){
		
	    if ( !(e.clipboardData && e.clipboardData.items) ) {
	        return;
	    }
	});*/
	
	function press(event){
		var value = event.key;
		if(value=='Enter'){
			value = "\r\n";
		}
		if(value=='Tab'){
			value = "\t";
		}
		if(value=='Tab'){
			value = "\t";
		}
		if(value=='Tab'){
			value = "\t";
		}
		if(value=='Control'){
			return;
		}
		if(event.ctrlKey && value=='c' ){
			value = String.fromCharCode(3);
		}
		//退格键
		if(value=='Backspace'){
			value = String.fromCharCode(127);
		}
		
		if(value=='Shift' || value=='Alt' || 
			value=='ArrowDown' || value=='ArrowRight' || value=='ArrowLeft' || value=='ArrowUp' || 
			value=='F1'||value=='F2'||value=='F3'||value=='F4'||value=='F5'||value=='F6'||value=='F7'||
			value=='F8'||value=='F9'||value=='F10'||value=='F11'||value=='F12'){
			return;
		}
		event.preventDefault();
		socket.send(value);
	}