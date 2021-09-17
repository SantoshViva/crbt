package com.santosh.greenzone.wapchatezee.model;

public class BalanceAccountInfo {
	
	private String accountId;
	private String balance;
	private String accountType;
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	@Override
    public String toString() {
        return "{" +
                "accountId='" + accountId + '\'' +
                ", balance=" + balance +
                ", accountType='" + accountType + '\'' +
                '}';
    }
	
	
	
	
	
}
