package com.deepthinking.ext.serialize;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.time.LocalDate;

/**
 * 日期格式必须是 yyyyMMdd
 */
@Slf4j
public class StringToDateReader implements ObjectReader<LocalDate> {
    @Override
    public LocalDate readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        String date = jsonReader.readString();
        if (StringUtil.length(date) < 8)
            return null;
        try {
            if(StringUtil.containsString(date, "-")){
                return DateUtils.parseLocalDate(date.substring(0, 10), DateFormatEnum.DATE);
            }
            return DateUtils.parseLocalDate(date.substring(0, 8), DateFormatEnum.DATE_SHORT);
        } catch (Exception e) {
            log.error(">>>>>{} 日期格式错误：{}={}", this.getClass().getSimpleName(), fieldName, date);
            return null;
        }
    }
}
