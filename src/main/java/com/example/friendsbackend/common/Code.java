package com.example.friendsbackend.common;

public enum Code {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    PARAM_NULL_ERROR(400001,"请求数据为空",""),
    NO_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"没有权限",""),
    SAVE_ERROR(50001,"保存失败",""),
    SYSTEM_ERROR(50000,"系统错误","");


    private final int code;
    private final String message;
    private final String description;

    Code(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

}
