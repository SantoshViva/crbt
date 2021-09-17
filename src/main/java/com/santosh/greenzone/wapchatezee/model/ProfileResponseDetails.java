package com.santosh.greenzone.wapchatezee.model;

public class ProfileResponseDetails {
	private String result;
	private String data;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ProfileResponseDetails{" +"result=" + result + ", data=" + data + "}";
	}
	
	
	
	
	

}
