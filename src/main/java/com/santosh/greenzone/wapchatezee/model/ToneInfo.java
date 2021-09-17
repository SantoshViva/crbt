package com.santosh.greenzone.wapchatezee.model;

public class ToneInfo {

	private String subscriberId;
	private String toneId;
	private String status;
	private String callingParty;
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
	
}
