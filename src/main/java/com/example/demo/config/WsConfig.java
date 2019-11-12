package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WsConfig implements WebSocketMessageBrokerConfigurer {

	
	/*
	 * public class MyHand extends DefaultHandshakeHandler{
	 * 
	 * @Override protected Principal determineUser(ServerHttpRequest request,
	 * WebSocketHandler wsHandler, Map<String, Object> attributes) {
	 * 
	 * return super.determineUser(request, wsHandler, attributes); } }
	 */
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//registry.addEndpoint("/test").setHandshakeHandler(new MyHand());
		registry.addEndpoint("/test").withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}
	
	
}
