package com.santosh.greenzone.wapchatezee.model;

public class TonePlayerDtmfInfo {
	private String aparty;
	private String bparty;
	private String toneId;
	private String digits;
	private String startTime;
	private String endTime;
	private String startToCopy;

	
	public TonePlayerDtmfInfo() {
		this.aparty="";
		this.bparty="";
		this.toneId="";
		this.digits="";
		this.startTime="";
		this.endTime="";
		this.startToCopy="";
				
				
	}
	public String getAparty() {
		return aparty;
	}
	public void setAparty(String aparty) {
		this.aparty = aparty;
	}
	public String getBparty() {
		return bparty;
	}
	public void setBparty(String bparty) {
		this.bparty = bparty;
	}
	public String getToneId() {
		return toneId;
	}
	public void setToneId(String toneId) {
		this.toneId = toneId;
	}
	public String getDigits() {
		return digits;
	}
	public void setDigits(String digits) {
		this.digits = digits;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getStartToCopy() {
		return startToCopy;
	}
	public void setStartToCopy(String startToCopy) {
		this.startToCopy = startToCopy;
	}
	
}
