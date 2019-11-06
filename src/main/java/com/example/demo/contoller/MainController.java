package com.example.demo.contoller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.services.MessageService;

@Controller
public class MainController {
	
	@Autowired
	private MessageService service;
	
	
	@MessageMapping("/chat.register")
	@SendTo("/topic/public")
	public  Collection<ChatMessage> register(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor messageHeaderAccessor) {
		messageHeaderAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		service.saveMessage(chatMessage);
		return service.findAvailableMessages();
	}
	
	@MessageMapping("/chat.send")
	@SendTo("/topic/public")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
		service.saveMessage(chatMessage);
		return chatMessage;
		
	}
	
}
