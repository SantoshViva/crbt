package com.santosh.greenzone.wapchatezee.model;

public class VoiceSmsMessageDetails {
	private int id;
	private String subscriber_id;
	private String v_msisdn;
	private String interface_id;
	
	private String status;
	private String duration;
	private String send_date;
	private String listening_date;
	private String recording_path;
	public VoiceSmsMessageDetails() {
		
		this.id = 0;
		this.subscriber_id = "";
		this.v_msisdn = "";
		this.interface_id = "";
		
		this.status = "";
		this.duration = "";
		this.send_date = "";
		this.listening_date = "";
		this.recording_path = "";
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSubscriber_id() {
		return subscriber_id;
	}
	public void setSubscriber_id(String subscriber_id) {
		this.subscriber_id = subscriber_id;
	}
	public String getV_msisdn() {
		return v_msisdn;
	}
	public void setV_msisdn(String v_msisdn) {
		this.v_msisdn = v_msisdn;
	}
	public String getInterface_id() {
		return interface_id;
	}
	public void setInterface_id(String interface_id) {
		this.interface_id = interface_id;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String send_date) {
		this.send_date = send_date;
	}
	public String getListening_date() {
		return listening_date;
	}
	public void setListening_date(String listening_date) {
		this.listening_date = listening_date;
	}
	public String getRecording_path() {
		return recording_path;
	}
	public void setRecording_path(String recording_path) {
		this.recording_path = recording_path;
	}
	
	
}
