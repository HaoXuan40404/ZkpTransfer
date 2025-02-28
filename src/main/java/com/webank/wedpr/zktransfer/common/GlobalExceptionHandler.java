package com.webank.wedpr.zktransfer.common;

import com.webank.wedpr.zktransfer.message.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn(e.getMessage(), e);
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
        return response;
    }

    @ExceptionHandler(value = BindException.class)
    public BaseResponse handleMethodArgumentNotValidException(BindException e) {
        log.warn(e.getMessage(), e);
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(e.getBindingResult().getFieldError().getDefaultMessage());
        return response;
    }

    @ExceptionHandler(value = PpcException.class)
    public BaseResponse handlePpcException(PpcException e) {
        BaseResponse response = e.getBaseResponse();
        return response;
    }

    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse handlePpcException(RuntimeException e) {
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(e.getMessage());
        return response;
    }

    @ExceptionHandler(value = Throwable.class)
    public BaseResponse handlePpcException(Throwable e) {
        BaseResponse response = new BaseResponse();
        response.setErrorCode(EnumResponseStatus.FAILURE.getErrorCode());
        response.setMessage(e.getLocalizedMessage());
        return response;
    }
}
