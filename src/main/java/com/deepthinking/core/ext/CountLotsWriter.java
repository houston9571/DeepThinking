package com.deepthinking.core.ext;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.deepthinking.common.utils.NumberUtils;

import java.lang.reflect.Type;

public class CountLotsWriter implements ObjectWriter<Object> {


    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        try {
            jsonWriter.writeString(NumberUtils.addCountUtil(object.toString()) + "(手)");
        } catch (Exception e) {
            jsonWriter.writeString(object.toString());
        }

    }
}