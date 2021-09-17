package com.santosh.greenzone.wapchatezee.model;
import java.util.Arrays;
import java.util.List;
public class UserBalanceInfoDetailModel {
	
	private String result;
	private String errorCode;
	private List<Object> accountsInfo;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public List<Object> getAccountsInfo() {
		return accountsInfo;
	}
	public void setAccountsInfo(List<Object> accountsInfo) {
		this.accountsInfo = accountsInfo;
	}
	
	@Override
    public String toString() {
        return "{" +
                "result='" + result + '\'' +
                ", errorCode=" + errorCode +
                ", accountsInfo='" + accountsInfo + '\'' +
                '}';
    }
}
