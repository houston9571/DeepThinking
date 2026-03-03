package com.deepthinking.service;

import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockKlineDaily;
import com.deepthinking.mysql.entity.StockTechDaily;

import java.util.List;

public interface StockTechDailyService extends MybatisBaseService<StockTechDaily> {

    void calculateDailyIndicatorAndSave(List<StockKlineDaily> barList);


}
