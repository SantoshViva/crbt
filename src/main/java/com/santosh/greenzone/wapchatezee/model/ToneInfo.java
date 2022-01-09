package com.santosh.greenzone.wapchatezee.model;

public class ToneInfo {

	private String subscriberId;
	private String status;
	private String callingParty;
	private String toneId;
	private String songName;
	private String songPath;
	private String subscriberType;
	private String contentType;
	private String serviceId;
	
	public String getCallingParty() {
		return callingParty;
	}
	public void setCallingParty(String callingParty) {
		this.callingParty = callingParty;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubscriberId() {
		return subscriberId;
	}
	public void setSubscriberId(String subscribeId) {
		this.subscriberId = subscribeId;
	}
	public String getToneId() {
		return toneId;
	}
	public void setToneId(String toneId) {
		this.toneId = toneId;
	}
	public String getSubscriberType() {
		return subscriberType;
	}
	public void setSubscriberType(String subscriberType) {
		this.subscriberType = subscriberType;
	}
	public String getSongPath() {
		return songPath;
	}
	public void setSongPath(String songPath) {
		this.songPath = songPath;
	}
	public String getSongName() {
		return songName;
	}
	public void setSongName(String songName) {
		this.songName = songName;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	
	
}
