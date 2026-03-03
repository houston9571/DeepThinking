package com.deepthinking.ext.aspect;

import com.deepthinking.common.exception.ParamValidatorException;
import com.deepthinking.common.exception.ServiceException;
import com.deepthinking.ext.base.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static cn.hutool.core.text.StrPool.COLON;
import static com.deepthinking.common.enums.ErrorCode.*;

/**
 * 全局异常处理
 **/
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {



    /**
     * 处理自定义异常
     */
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleException(ServiceException e) {
        log.debug("code:{} msg:{} {} ", e.getCode(), e.getMsg(), e.getCause());
        return loggingError(Result.fail(e.getCode(), e.getMsg()));
    }

    @ResponseBody
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public Result<Void> handleException(HttpRequestMethodNotSupportedException e) {
        return loggingError(Result.fail(REQUEST_UNSUPPORTED));
    }

    @ResponseBody
    @ExceptionHandler({DuplicateKeyException.class, SQLIntegrityConstraintViolationException.class, SQLException.class})
    public Result<Void> handleException(Exception e) {
        log.error(e.getMessage());
        if (e instanceof DuplicateKeyException || e instanceof SQLIntegrityConstraintViolationException) {
            return loggingError(Result.fail(DATE_DUPLICATE.getCode(), DATE_DUPLICATE.getMsg() + COLON + e.getCause().getMessage()));
        }
        log.error("", e);
        return loggingError(Result.fail(DB_ERROR));
    }

    @ResponseBody
    @ExceptionHandler({ServletRequestBindingException.class})
    public Result<Void> handleException(ServletRequestBindingException e) {
        String msg = e.getMessage();
        if (StringUtils.contains(msg, "'")) {
            msg = "miss parameters " + e.getMessage().substring(msg.indexOf("'") + 1, msg.lastIndexOf("'"));
        }
        return loggingError(Result.fail(PARAM_ERROR_S.getCode(), String.format(PARAM_ERROR_S.getMsg(), msg)));
    }

    @ResponseBody
    @ExceptionHandler({ParamValidatorException.class})
    public Result<Void> handleException(ParamValidatorException e) {
        return Result.fail(PARAM_ERROR_S.getCode(), String.format(PARAM_ERROR_S.getMsg(), e.getMessage()));
    }


    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleException(MethodArgumentNotValidException e) {
        StringBuilder msg = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            msg.append(error.getDefaultMessage()).append(" ");
        }
        return loggingError(Result.fail(PARAM_ERROR_S.getCode(), String.format(PARAM_ERROR_S.getMsg(), msg.toString().trim())));
    }

/*    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleException(ConstraintViolationException e) {
        StringBuilder msg = new StringBuilder();
        for (ConstraintViolation<?> error : e.getConstraintViolations()) {
            msg.append(error.getMessage()).append(" ");
        }
        return loggingError(Result.fail(PARAM_ERROR_S.getCode(), String.format(PARAM_ERROR_S.getMsg(), msg.toString().trim())));
    }*/


    @ResponseBody
    @ExceptionHandler({Exception.class})
    public Result<Void> handleDefaultException(Exception e) {
        log.error(" - handleException:{}", e.getMessage(), e);
        return loggingError(Result.fail(SYSTEM_ERROR));
    }

    private Result<Void> loggingError(Result<Void> result){
//        apiParamAccessor.saveSysLog(result);
        return result;
    }
}
