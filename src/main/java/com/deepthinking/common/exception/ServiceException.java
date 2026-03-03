package com.deepthinking.common.exception;

import com.deepthinking.common.enums.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code = 1000;

    private String msg;


    public ServiceException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg);
    }

    public ServiceException(int code, String msg, Throwable e) {
        super(msg, e);
        this.code = code;
        this.msg = msg;
        log.error("code:{} msg:{}", code, msg, e);
    }

    public ServiceException(String msg) {
        this(ErrorCode.SYSTEM_ERROR, msg);
    }

    public ServiceException(String msg, Throwable e) {
        this(ErrorCode.SYSTEM_ERROR, msg, e);
    }

    public ServiceException(ErrorCode error, Object... args) {
        this(error.getCode(), error.getMsg(args));
    }

}
