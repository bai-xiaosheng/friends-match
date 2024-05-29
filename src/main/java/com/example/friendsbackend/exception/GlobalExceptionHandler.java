package com.example.friendsbackend.exception;

import com.example.friendsbackend.common.BaseResponse;
import com.example.friendsbackend.common.Code;
import com.example.friendsbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException exception){
        log.error("BusinessException"+exception.getMessage(),exception);
        return ResultUtils.error(exception.getCode(),exception.getMessage(),exception.getDescription());
    }

    @ExceptionHandler(SystemException.class)
    public BaseResponse systemExceptionHandler(SystemException exception){
        log.error("systemException",exception);
        return ResultUtils.error(Code.SYSTEM_ERROR,exception.getMessage(),"");
    }
}
