package com.santosh.greenzone.wapchatezee.model;

public class ResponseObdDTO {
	private String type;
	  private Value value;

	  public ResponseObdDTO() {
	  }

	  public String getType() {
	    return type;
	  }

	  public void setType(String type) {
	    this.type = type;
	  }

	  public Value getValue() {
	    return value;
	  }

	  public void setValue(Value value) {
	    this.value = value;
	  }

	  @Override
	  public String toString() {
	    return "Quote{" +
	        "type='" + type + '\'' +
	        ", value=" + value +
	        '}';
	  }
}
