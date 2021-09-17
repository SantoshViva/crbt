package com.santosh.greenzone.wapchatezee.model;

import com.fasterxml.jackson.annotation.JsonInclude;


public class ChatMessageDetailModel {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String chatUserId;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String chatMsisdn;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String chatMessage;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String chatDate;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String chat_read_date;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String friendMsisdn;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String friendUserId;
	private String chatAlignment; 
	
	public void setChatUserId(String chatUserId)
	{
		this.chatUserId=chatUserId;
	}
	public String getChatUserId()
	{
		return chatUserId;
	}
	public String getChatMsisdn() {
		return chatMsisdn;
	}
	public void setChatMsisdn(String chatMsisdn) {
		this.chatMsisdn = chatMsisdn;
	}
	public String getChatDate() {
		return chatDate;
	}
	public void setChatDate(String chatDate) {
		this.chatDate = chatDate;
	}
	public String getChatMessage() {
		return chatMessage;
	}
	public void setChatMessage(String chatMessage) {
		this.chatMessage = chatMessage;
	}
	public String getChat_read_date() {
		return chat_read_date;
	}
	public void setChat_read_date(String chat_read_date) {
		this.chat_read_date = chat_read_date;
	}
	public String getFriendMsisdn() {
		return friendMsisdn;
	}
	public void setFriendMsisdn(String friendMsisdn) {
		this.friendMsisdn = friendMsisdn;
	}
	public String getFriendUserId() {
		return friendUserId;
	}
	public void setFriendUserId(String friendUserId) {
		this.friendUserId = friendUserId;
	}
	public String getChatAlignment() {
		return chatAlignment;
	}
	public void setChatAlignment(String chatAlignment) {
		this.chatAlignment = chatAlignment;
	}
	
	
}
