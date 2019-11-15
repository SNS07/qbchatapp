'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var isCurrentUser='Y';

var colors = [ '#2196F3', '#32c787', '#00BCD4', '#ff5652', '#ffc107',
		'#ff85af', '#FF9800', '#39bbb0' ];


window.onload = function() {

	showNotificationToUser();
}

	
function isNewNotificationSupported() {
	    if (!window.Notification || !Notification.requestPermission)
	        return false;
	    if (Notification.permission == 'granted')
	    	return true;
	    try {
	        new Notification('');
	    } catch (e) {
	        if (e.name == 'TypeError')
	            return false;
	    }
	    return true;
	}	
	
function connect(event) {

	
	username = document.querySelector('#name').value.trim();
	
	if (username) {
		usernamePage.classList.add('hidden');
		chatPage.classList.remove('hidden');

		var socket = new SockJS('/test');
		//var socket = new WebSocket("ws://" + window.location.host +   "/test");
		stompClient = Stomp.over(socket);

		stompClient.connect({}, onConnected, onError);
	}
	event.preventDefault();
}

function onConnected() {
	
	
	var chatRoom = document.querySelector('#chatRoom').value.trim().toUpperCase();
	if(!chatRoom){
		chatRoom = 'GLOBAL';
	}
	
	
	findAndUpdateExistingMessagesFromGroup(chatRoom);
	
	// Subscribe to the Public Topic
	stompClient.subscribe('/topic/public/'+chatRoom, onMessageReceived);

	// Tell your username to the server
	stompClient.send("/app/chat.register", {}, JSON.stringify({
		sender : username,
		chatRoom : chatRoom,
		type : 'JOIN'
	}))
		document.querySelector('#chatHeading').innerHTML=chatRoom+' CHAT ROOM';
	connectingElement.classList.add('hidden');
}

function findAndUpdateExistingMessagesFromGroup(chatRoom){
	var request = new XMLHttpRequest()
	request.open('GET', '/chatRoomMessages/'+chatRoom, true)
	request.onload = function() {
	  // Begin accessing JSON data here
	  var data = JSON.parse(this.response)
	  if (request.status >= 200 && request.status < 400) {
	    data.forEach(message => {
	    	displayMessage(message);
	    })
	  } 
	}

	request.send();
	
}

function onError(error) {
	connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
	connectingElement.style.color = 'red';
}

function send(event) {
	isCurrentUser = 'Y';
	var messageContent = messageInput.value.trim();

	if (messageContent && stompClient) {
		var chatMessage = {
			sender : username,
			content : messageInput.value,
			type : 'CHAT'
		};

		stompClient.send("/app/chat.send", {}, JSON.stringify(chatMessage));
		messageInput.value = '';
	}
	event.preventDefault();
}

function showNotificationToUser(){

	 if (isNewNotificationSupported() && Notification.permission !== 'granted')
		  Notification.requestPermission();
	
		
}

function notifyMe(message) {
	
	if(isNewNotificationSupported() && isCurrentUser !=  'Y'){
		
	var content = null;
	  if (message.type === 'CHAT') {
		  content = message.content
	  }else{
		  content = message.type
	  }
	 if (Notification.permission !== 'granted')
	  Notification.requestPermission();
	 else {
	  var notification = new Notification('Message from '+message.sender, {
	   body: content,
	  });
	  notification.onclick = function() {
	   window.focus();
	  };
	 }
	}
	isCurrentUser = null;
	}

function onMessageReceived(payload) {
	
	var sessionValue= document.querySelector('#hdnSession');
	var message = JSON.parse(payload.body);
	
	/*if(flag && messageArea.childElementCount > 1 ){
		  var child = messageArea.lastElementChild;  
	        while (child) { 
	        	messageArea.removeChild(child); 
	            child = messageArea.lastElementChild; 
	        } 
	    	
		
		messageArea.remove();
		
	}*/
	if(message.length == undefined ){
		notifyMe(message);
		displayMessage(message);
	}
	else if( message.length == 1){
		message = message[0];
		
		displayMessage(message);
	}else{
		for(var i=0;i<message.length;i++){
			displayMessage(message[i]);
		}
		
	}
	
}

function displayMessage(message){
	
	var messageElement = document.createElement('li');

	if (message.type === 'JOIN') {
		messageElement.classList.add('event-message');
		message.content = message.sender + ' joined!';
	} else if (message.type === 'LEAVE') {
		messageElement.classList.add('event-message');
		message.content = message.sender + ' left!';
	} else {
		messageElement.classList.add('chat-message');

		var avatarElement = document.createElement('i');
		var avatarText = document.createTextNode(message.sender[0]);
		avatarElement.appendChild(avatarText);
		avatarElement.style['background-color'] = getAvatarColor(message.sender);

		messageElement.appendChild(avatarElement);

		var usernameElement = document.createElement('span');
		var usernameText = document.createTextNode(message.sender);
		usernameElement.appendChild(usernameText);
		messageElement.appendChild(usernameElement);
	}

	var textElement = document.createElement('p');
	var messageText = document.createTextNode(message.content);
	textElement.appendChild(messageText);

	messageElement.appendChild(textElement);

	messageArea.appendChild(messageElement);
	messageArea.scrollTop = messageArea.scrollHeight;
	
	
}
function getAvatarColor(messageSender) {
	var hash = 0;
	for (var i = 0; i < messageSender.length; i++) {
		hash = 31 * hash + messageSender.charCodeAt(i);
	}

	var index = Math.abs(hash % colors.length);
	return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', send, true)
