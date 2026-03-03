package com.deepthinking.ext.serialize;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.deepthinking.common.utils.NumberUtils;

import java.lang.reflect.Type;

public class DivideBy100Writer implements ObjectWriter<Object> {


    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        if (object == null) {
            jsonWriter.writeNull();
            return;
        }

        try {
            jsonWriter.writeDecimal(NumberUtils.moneyDivide(object.toString()));
        } catch (Exception e) {
            jsonWriter.writeString(object.toString());
        }

    }


}
