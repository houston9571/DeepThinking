package com.deepthinking.common.enums;


import com.deepthinking.common.utils.SpringContextUtils;
import lombok.Getter;

import static com.deepthinking.common.enums.LanguageEnum.ENGLISH;

@Getter
public enum ErrorCode {

    FAIL(-1, "失败", "Fail"),

    /**
     * 错误码 1000-2000 公共错误码
     */
    SYSTEM_ERROR(1000, "系统错误", "System error"),

    NETWORK_ERROR(1001, "网络异常 %s", "Network exception %s"),

    REQUEST_UNSUPPORTED(1002, "不支持该请求: %s", "The request is not supported: %s"),

    INTERFACE_UNSUPPORTED(1003, "不支持该接口", "This interface is not supported"),

    ATTACK_FILTER(1004, "攻击过滤: 该请求包含非法字符", "Attack Filter: The request contains illegal characters"),

    FREQUENT_REQUEST_S(1005, "访问频率限制: %s", "Access frequency limit: %s"),

    PARAM_ERROR_S(1006, "参数错误: %s", "Parameter error: %s"),

    DB_ERROR(1007, "数据库操作异常", "Database operation exception"),

    DATA_NOT_EXIST(1008, "数据不存在", "Data does not exist"),

    DATE_DUPLICATE(1009, "数据已存在", "Data duplication"),

    NOT_GET_PAGE_ERROR(1010, "未获取到页面信息:%s", "Do not get the page:%s"),

    DATE_PARSE_ERROR(1011, "数据解析错误:%s", "Date parse error: %s"),


    DATA_UPDATED(1012, "数据已经更新 %s:%s", "Date was updated %s:%s"),

    DATA_UNPAIR(1013, "数据不匹配 %s -> %s", "Date was unpair %s -> %s"),

    NETWORK_FAILED(1014, "网络请求失败。url=%s body=%s", ""),
    ;

    private final int code;

    private final String zh;

    private final String en;

    ErrorCode(int code, String zh, String en) {
        this.code = code;
        this.zh = zh;
        this.en = en;
    }

    public String getMsg() {
        if (ENGLISH.getLanguage().equals(SpringContextUtils.getLanguage())) {
            return en;
        }
        return zh;
    }

    public String getMsg(Object... args) {
        return args != null ? String.format(getMsg(), args) : getMsg();
    }
}
