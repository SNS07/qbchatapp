package com.example.demo.contoller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.ChatMessage;
import com.example.demo.services.MessageService;

@Controller
public class MainController {
	
	@Autowired
	private MessageService service;
	
	String baseUrl = "/topic/public/";
	
	@Autowired
	SimpMessageSendingOperations messageSendingOperations;
	
	
	
	/**
	 * Method Used to get all existing chats from the room
	 * @param chatRoom
	 * @return
	 */
	@ResponseBody
	@GetMapping("/chatRoomMessages/{chatRoom}")
	public  Collection<ChatMessage> findAvailableMessages(@PathVariable(name = "chatRoom") String chatRoom) {
		return service.findAvailableMessages(chatRoom);
	}
	
	@MessageMapping("/chat.register")
	//@SendTo("/topic/public")
	public  Collection<ChatMessage> register(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor messageHeaderAccessor) {
		String chatRoom = chatMessage.getChatRoom().trim();
		messageHeaderAccessor.getSessionAttributes().put("username", chatMessage.getSender());
		messageHeaderAccessor.getSessionAttributes().put("chatRoom", chatRoom);
		
		service.saveMessage(chatMessage);
		messageSendingOperations.convertAndSend(baseUrl+chatRoom,chatMessage);
		return service.findAvailableMessages(chatRoom);
	}
	
	@MessageMapping("/chat.send")
	//@SendTo("/topic/public")
	public void sendMessage(@Payload ChatMessage chatMessage,SimpMessageHeaderAccessor messageHeaderAccessor) {
		
		String chatRoom = (String) messageHeaderAccessor.getSessionAttributes().get("chatRoom");
		chatMessage.setChatRoom(chatRoom.trim());
		service.saveMessage(chatMessage);
		messageSendingOperations.convertAndSend(baseUrl+chatRoom.trim(),chatMessage);
		//return chatMessage;
		
	}
	
}
