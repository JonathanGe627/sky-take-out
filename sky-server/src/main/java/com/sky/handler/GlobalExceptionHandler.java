package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获校验异常
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("异常信息：{}", e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();
            errorMap.put(field, defaultMessage);
        }
        return Result.error(e.getMessage(), errorMap);
    }

    /**
     * 处理数据库数据完整性相关的异常
     * @param e
     * @return
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("异常信息：{}", e.getMessage());
        String exceptionName = e.getClass().getName();
        String message = e.getCause().toString();
        Map<String, String> errorMap = new HashMap<>();
        if (exceptionName.contains("DuplicateKeyException")){
            // 唯一约束/主键冲突异常
            String[] split = message.split(" ");
            errorMap.put("exception", exceptionName);
            return Result.error(MessageConstant.USERNAME_ALREADY_EXISTS + split[3], errorMap);
        }
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /**
     * 捕获业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(BaseException.class)
    public Result handleBaseException(BaseException e){
        log.error("异常信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 兜底异常捕获方法
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public Result handleThrowable(Throwable e){
        log.error("捕获到异常类型：{}", e.getClass().getName(), e);
        log.error("异常信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

}
