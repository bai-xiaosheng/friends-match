package com.example.friendsbackend.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {
    int code;
    T data;
    String message;
    String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(Code code) {
        this(code.getCode(),null,code.getMessage(), code.getDescription());
    }
    public BaseResponse(Code code,T data) {
        this(code.getCode(),data,code.getMessage(), code.getDescription());
    }

    public BaseResponse(int code, String message, String description) {
        this(code,null,message,description);
    }
}
