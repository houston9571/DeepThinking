package com.deepthinking.service.impl;

import com.deepthinking.common.constant.MarketType;
import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.StockPool;
import com.deepthinking.mysql.mapper.StockPoolMapper;
import com.deepthinking.service.StockKlineMinuteService;
import com.deepthinking.service.StockPoolService;
import com.deepthinking.service.StockTechMinuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockPoolServiceImpl extends MybatisBaseServiceImpl<StockPoolMapper, StockPool> implements StockPoolService {

    private final StockPoolMapper stockPoolMapper;

    private final StockKlineMinuteService stockKlineMinuteService;

    private final StockTechMinuteService stockTechMinuteService;


    /**
     * 根据股票池更新个股分时数据 1分钟
     */
    public Result<Integer> syncStockMinuteDateFromPool() {
        List<StockPool> stocks = queryList(StockPool.builder().tradeDate(LocalDate.now()).build());

        stocks.forEach(stock -> {
            String stockCode = stock.getStockCode();
            if (MarketType.contains(stockCode, stock.getStockName())) {
                stockKlineMinuteService.syncStockKlineMinute(stockCode);
                stockTechMinuteService.syncStockTrendsMinute(stockCode);
            }
        });
        return Result.success(stocks.size());
    }

    /**
     * 精选股票加入股票池
     */
    public void addStockPool() {
        // todo
    }

}
