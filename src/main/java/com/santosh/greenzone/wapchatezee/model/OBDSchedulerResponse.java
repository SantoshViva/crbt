package com.santosh.greenzone.wapchatezee.model;

public class OBDSchedulerResponse {

    private String responseCode;
    @Override
	public String toString() {
		return "obdControllerResponse[responseCode=" + responseCode + ", errorMsg=" + errorMsg + ", sessionId="
				+ sessionId  + "]";
	}
	private String errorMsg;
    private String sessionId;
    
    
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
