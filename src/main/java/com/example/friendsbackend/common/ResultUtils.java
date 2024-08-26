package com.example.friendsbackend.common;

public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse(Code.SUCCESS.getCode(),data,Code.SUCCESS.getMessage(),"");
    }
    public static <T> BaseResponse<T> success(T data,String description){
        return new BaseResponse(Code.SUCCESS.getCode(),data,Code.SUCCESS.getMessage(),description);
    }

    public static <T> BaseResponse<T> error(Code code){
        return new BaseResponse<>(code);
    }

    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse<>(code,message,description);
    }

    public static BaseResponse error(int code, String message) {
        return new BaseResponse<>(code,message,"");
    }

    public static BaseResponse error(Code code, String message, String description) {
        return new BaseResponse<>(code.getCode(),message,description);
    }
    public static BaseResponse error(Code code, String message) {
        return new BaseResponse<>(code.getCode(),message,"");
    }
}
