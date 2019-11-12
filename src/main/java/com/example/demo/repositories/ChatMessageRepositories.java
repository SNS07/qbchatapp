package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UserMessages;

public interface ChatMessageRepositories extends JpaRepository<UserMessages, Integer> {
	
	public List<UserMessages> findByChatRoom(String chatRoom);

}
