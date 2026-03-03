package com.deepthinking.ext.serialize;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;

import java.lang.reflect.Type;

import static com.deepthinking.common.constant.Constants.PERCENT;

public class PercentageWriter implements ObjectWriter<Object> {


    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        try {
            jsonWriter.writeString(object + PERCENT);
        } catch (Exception e) {
            jsonWriter.writeString(object.toString());
        }

    }

}