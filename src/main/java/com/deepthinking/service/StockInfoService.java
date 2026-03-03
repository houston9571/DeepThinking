package com.deepthinking.service;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseService;
import com.deepthinking.mysql.entity.StockInfo;

public interface StockInfoService extends MybatisBaseService<StockInfo> {

    Result<Integer> syncStockInfoAll() ;

    Result<StockInfo> syncStockInfo(String stockCode);

    Result<Void> syncStockConceptList(String stockCode);


}
