package com.deepthinking.service;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;

import java.util.List;

public interface StockTechMinuteService extends MybatisBaseService<StockTechMinute> {


    Result<Void> syncStockTrendsMinute(String stockCode);

    @Deprecated
    void calculateMinuteIndicatorAndSave(List<StockKlineMinute> last10);


}
