package com.deepthinking.service;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.entity.DragonStock;
import com.deepthinking.mysql.vo.DragonDetailPartner;
import com.deepthinking.mysql.vo.DragonDetailStockKline;

import java.util.List;

public interface DragonStockService {

    List<DragonDetailStockKline> queryDragonStockList(String tradeDate);

    List<DragonDetailStockKline> queryDragonStockDetail(String stockCode);

    List<DragonDetailPartner> queryDragonPartnerDetail(String partnerCode);

    Result<List<DragonStock>> syncDragonStockList(String date);

}
