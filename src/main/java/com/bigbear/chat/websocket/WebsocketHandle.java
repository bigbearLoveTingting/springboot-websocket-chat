package com.bigbear.chat.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

/**
  * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
  * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint("/chatWebsocket")
@Component
public class WebsocketHandle {
	
	// 在线连接数，应设计成线程安全的
	private static int onlineNum = 0;
	
	// concurrent包中线程安全set,用来存放每个客户端对应的WebsocketHandle对象,若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
	private static CopyOnWriteArraySet<WebsocketHandle> websocketSet = new CopyOnWriteArraySet<WebsocketHandle>();
	
	// 与某个客户端的连接会话，需要通过它向客户端发送数据
	private Session session;
	
	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		websocketSet.add(this);	//加入set中
		addOnlineNum();	//在线数+1
		System.out.println("有新连接加入，当前连接数：" + onlineNum);
	}
	
	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void closeOpen() {
		websocketSet.remove(this);		//从set中删除
		subOnlineNum();
		System.out.println("有连接退出，当前连接数：" + onlineNum);
	}
	
	/**
	 * 收到客户端消息后调用的方法
	 * @param message  客户端发送过来的消息
	 * @param session     可选的参数
	 */
	@OnMessage
	public void onMessage(String message,Session session) {
		System.out.println("来自客户端的消息：" + message);
		//群发消息
		for(WebsocketHandle websocketHandle : websocketSet){
			try {
				websocketHandle.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}
	
	/**
	 * 发生错误时调用的方法
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session,Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}
	
	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}
	
	public static synchronized void addOnlineNum() {
		onlineNum++;
	}
	
	public synchronized void subOnlineNum() {
		onlineNum--;
	}
}
