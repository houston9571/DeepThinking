package com.deepthinking.service;

import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.core.base.Result;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockKlineMinute;

public interface StockKlineMinuteService extends MybatisBaseService<StockKlineMinute> {

    void syncStockKlineMinutePools();

    Result<StockKlineMinute> syncStockKlineMinute(String stockCode);


    Result<JSONObject> getFirstRequest2Data(String code);

}
