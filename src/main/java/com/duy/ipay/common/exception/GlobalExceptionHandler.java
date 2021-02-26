package com.duy.ipay.common.exception;

import com.duy.ipay.common.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String result = e.getBindingResult().getFieldError().getField() + e.getBindingResult().getFieldError().getDefaultMessage();
        log.error("参数异常：{}",result);
        return R.error(HttpStatus.BAD_REQUEST.value(),result);
    }
}
