package com.deepthinking.ext.serialize;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class DivideBy100Reader implements ObjectReader<Double> {
    @Override
    public Double readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        // 读取原始数值, 有时会是个字符 - ，检查是否是数字
        try {
            Number rawValue = jsonReader.readNumber();
            if (rawValue == null) return null;
            return rawValue.doubleValue() / 100.0;
        } catch (Exception e) {
            log.error(">>>>>{} 检查Number类型：{}={}", this.getClass().getSimpleName(), fieldName, jsonReader.readString());
            return null;
        }

    }
}
