//$.(document).ready(function(){
//	alert("123");
//})

$(function(){
	var websocket = null;

	$("#send").click(function(){
		send();
	});

	$("#closeWebSocket").click(function(){
		closeWebSocket()
	});

	//判断当前浏览器是否支持websocket
	if("WebSocket" in window) {
		websocket = new WebSocket("ws://localhost:8081/chatWebsocket");
	}else{
		alert("当前浏览器不支持WebSocket");
	}
	
	//连接发生错误的回调方法
	websocket.onerror = function() {
		setMessageInnerHTML("error");
	}

	//连接成功建立的回调方法
	websocket.onopen = function() {
		setMessageInnerHTML("open");
	}

	//接收到消息的回调方法
	websocket.onmessage = function(event){
		setMessageInnerHTML(event.data);
	}

	//连接关闭的回调方法
	websocket.onclose = function(){
		setMessageInnerHTML("close");
	}

	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function(){
		websocket.close();
	}

	//关闭连接
	function closeWebSocket(){
		websocket.close();
	}

	//发送消息
	function send(){
		var message = $("#content").val();
		websocket.send(message);
	}
})

/**
 * 将消息显示在网页上
 */
function setMessageInnerHTML(message) {
	$("#message").append(`${message}<br/>`);
}