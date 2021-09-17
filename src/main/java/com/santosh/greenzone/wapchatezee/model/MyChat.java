package com.santosh.greenzone.wapchatezee.model;

public class MyChat {
	
	private String chatUserId;
	private String chatMsisdn;
	private String chatDate;
	private String gender;
	
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
}
