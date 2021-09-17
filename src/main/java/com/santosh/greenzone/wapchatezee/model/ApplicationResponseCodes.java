package com.santosh.greenzone.wapchatezee.model;

public enum ApplicationResponseCodes {
	SUCCESS(200), BAD_REQUEST(400), NO_DATA_FOUND(404), SESSION_ERROR(301),  INTERNAL_ERROR(500);

    public int code;
    public String message;

    ApplicationResponseCodes(int code) {
        this.code = code;
        this.message = this.toString();
    }


}
