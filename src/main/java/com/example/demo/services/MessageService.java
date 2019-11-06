package com.example.demo.services;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserMessages;
import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;
import com.example.demo.repositories.ChatMessageRepositories;

@Service
public class MessageService {
	
	@Autowired
	private ChatMessageRepositories chatMessageRepositories;
	

	public Collection<ChatMessage> findAvailableMessages(){
		List<UserMessages>  userMessages = chatMessageRepositories.findAll();
		return userMessages.stream().map(e->{
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setContent(e.getContent());
			chatMessage.setSender(e.getOwner());
			chatMessage.setType(MessageType.getMessageType(e.getMessageType()));
			return chatMessage;
		}).collect(Collectors.toList());
	}
	
	public void saveMessage (ChatMessage chatMessage) {
		UserMessages userMessages = new UserMessages();
		userMessages.setContent(chatMessage.getContent());
		userMessages.setOwner(chatMessage.getSender());
		userMessages.setMessageType(chatMessage.getType().toString());
		chatMessageRepositories.save(userMessages);
	}
	
}
