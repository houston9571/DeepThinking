package com.deepthinking.service;

import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.entity.StockTechMinute;

import java.util.List;

public interface StockTechMinuteService extends MybatisBaseService<StockTechMinute> {

    void calculateMinuteIndicatorAndSave(List<StockKlineMinute> last10);


}
