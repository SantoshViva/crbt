package com.santosh.greenzone.wapchatezee.model;

public class AccountInfo {
	private String msisdn;
	private String operatorId;
	private String status;
	private String renewalDate;
	private String gender;
	private String ageGroup;
	private String userId;
	
	public AccountInfo(){
		this.msisdn="";
		this.operatorId="";
		this.status="";
		this.renewalDate="";
		this.gender="";
		this.ageGroup="";
		this.userId="";
	}
	
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRenewalDate() {
		return renewalDate;
	}
	public void setRenewalDate(String renewalDate) {
		this.renewalDate = renewalDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAgeGroup() {
		return ageGroup;
	}

	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
