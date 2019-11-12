package com.example.demo.listeners;



import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;
import com.example.demo.services.MessageService;

@Component
public class WebSocketListeners {

	public static final Logger logger = LoggerFactory.getLogger(WebSocketListeners.class);
	
	
	@Autowired
	SimpMessageSendingOperations messageSendingOperations;
	
	@Autowired
	private MessageService service;
	
	@EventListener
	public void handleWebsocketConnectionListner(SessionConnectEvent event) {
		logger.info("Received a new websocket connection");
		
	}
	
	
	@EventListener
	public void handleWebsocketDisconnectionListner(SessionDisconnectEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		String username = (String) accessor.getSessionAttributes().get("username");
		String chatRoom = (String) accessor.getSessionAttributes().get("chatRoom");
		if(Objects.nonNull(username)) {
			logger.info("User disconnected"+username);
			
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setChatRoom(chatRoom);
			chatMessage.setType(MessageType.LEAVE);
			chatMessage.setSender(username);
			service.saveMessage(chatMessage);
			messageSendingOperations.convertAndSend("/topic/public/"+chatRoom,chatMessage);
				
		}
		
	}
	
	
	
}
