package com.santosh.greenzone.wapchatezee.model;

import java.io.Serializable;

public class ResponseHeader implements Serializable {
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseHeader{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
