package com.deepthinking.core.base;

import com.alibaba.fastjson2.annotation.JSONField;
import com.deepthinking.common.enums.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.beans.Transient;

import static com.deepthinking.common.constant.Constants.OK;
import static com.deepthinking.common.enums.ErrorCode.SYSTEM_ERROR;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Result", description = "接口返回结果")
public class Result<T> {

    @ApiModelProperty("返回代码")
    @JSONField(ordinal = 1)
    protected int code;

    @ApiModelProperty("代码信息")
    @JSONField(ordinal = 2)
    protected String msg;

    @ApiModelProperty(hidden = true)
    @JSONField(serialize = false)
    protected boolean logData;

    @ApiModelProperty("返回数据")
    @JSONField(ordinal = 10)
    private T data;

    @Transient
    public boolean isSuccess() {
        return code == 0;
    }

    @Transient
    public boolean hasData() {
        return isSuccess() && data != null;
    }

    public static <T> Result<T> success() {
        return Result.<T>builder().code(0).msg(OK).build();
    }

    public static <T> Result<T> success(T data) {
        return Result.<T>builder().code(0).msg(OK).data(data).build();
    }

//    public static <T> Result<T> successAndLogDate(T data) {
//        return Result.<T>builder().code(0).msg(OK).data(data).logData(true).build();
//    }

    public static <T> Result<T> isSuccess(int n, ErrorCode failedCode) {
        return n > 0 ? success() : fail(failedCode);
    }

    public static <T> Result<T> fail(ErrorCode code, Object... args) {
        String msg = args != null ? String.format(code.getMsg(), args) : code.getMsg();
        log.error("[{}]{}", code, msg);
        return fail(code.getCode(), msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return Result.<T>builder().code(code).msg(msg).build();
    }

    public static <T> Result<T> fail(String msg) {
        return Result.<T>builder().code(SYSTEM_ERROR.getCode()).msg(msg).build();
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Result<T> setLogData(boolean logData) {
        this.logData = logData && code == 0;
        return this;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
