package com.example.demo.model;

public class ChatMessage {
	
	private String content;
	private String sender;
	private String chatRoom;
	private MessageType type;

	
	
	public String getChatRoom() {
		return chatRoom;
	}

	public void setChatRoom(String chatRoom) {
		this.chatRoom = chatRoom;
	}

	public ChatMessage() {
		super();
	}

	public  enum MessageType {
		CHAT, LEAVE, JOIN;
		public static MessageType getMessageType(String type) {
			MessageType messageType = null;
			if("CHAT".equals(type)) {
				messageType = MessageType.CHAT;
			}else if("LEAVE".equals(type)) {
				messageType = MessageType.LEAVE;
			}else if("JOIN".equals(type)) {
				messageType = MessageType.JOIN;
			}
			return messageType;
		}
		
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

}
