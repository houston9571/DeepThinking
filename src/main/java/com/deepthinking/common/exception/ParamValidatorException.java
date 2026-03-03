package com.deepthinking.common.exception;

/**
 * 参数验证错误异常
 **/
public class ParamValidatorException extends RuntimeException{

    public ParamValidatorException(String message) {
        super(message);
    }

}
