package com.deepthinking.service;

import com.deepthinking.mysql.entity.DragonStockDetail;

import java.time.LocalDate;
import java.util.List;

public interface DragonStockDetailService {


    List<List<DragonStockDetail>> queryDragonStockDetailWithPartner(String tradeDate);

    int syncDragonStockDetail(LocalDate date, String stockCode, String stockName) ;

}
