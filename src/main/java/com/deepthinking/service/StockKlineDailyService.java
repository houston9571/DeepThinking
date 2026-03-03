package com.deepthinking.service;

import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockKlineDaily;

import java.util.List;

public interface StockKlineDailyService extends MybatisBaseService<StockKlineDaily> {

    List<StockKlineDaily>  syncStockKlineDailyList();




}
