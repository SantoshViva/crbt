package com.santosh.greenzone.wapchatezee.model;


import java.io.Serializable;

public class ResponseDTO<T> implements Serializable {
    private ResponseHeader header;
    private T body;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public void setHeader(ResponseHeader header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "header=" + header +
                ", body=" + body +
                '}';
    }
}
