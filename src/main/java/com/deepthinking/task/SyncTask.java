package com.deepthinking.task;

import com.deepthinking.common.thread.Threads;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.utils.OSUtils;
import com.deepthinking.ext.base.Result;
import com.deepthinking.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.deepthinking.common.constant.MarketType.*;

//@Lazy
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTask {

    private final StockPoolService stockPoolService;

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final ConceptDelayService conceptDelayService;

    private final DragonDeptService dragonDeptService;

    private final DragonStockService dragonStockService;

    @Scheduled(cron = "0 0/20 * * * ?")
    void syncS() {
        LinkedHashMap  map = (LinkedHashMap )OSUtils.getSystemInfo().get("JVM");
        log.info(" --> {}:{}", "TotalMemory", map.get("TotalMemory"));
        log.info(" --> {}:{}", "MaxMemory", map.get("MaxMemory"));
        log.info(" --> {}:{}", "FreeMemory", map.get("FreeMemory"));
        log.info(" --> {}:{}", "RealUsage", map.get("RealUsage"));
    }

    /**************************** 股票行情 ***********************************/

    @Scheduled(cron = "0 0/1 9-12,13-15 ? * 1-5")
    void syncStockKlineMinutePools() {
        if (isTradeTime()) {
            log.info(" --> 同步股票K线行情及指标计算【stock_kline_minute】开始 ");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Result<Integer> result = stockPoolService.syncStockMinuteDateFromPool();
            stopWatch.stop();
            log.info(" --> 同步股票K线行情及指标计算【stock_kline_minute】结束 {} 耗时：{}", result, DateUtils.formatDateTime(stopWatch.getTotalTimeMillis()));
        }
    }

    // → 触发时间：09:42, 10:12, 10:42, 11:12, 11:42
    // → 触发时间：13:12, 13:42, 14:12, 14:42, 15:12
    @Scheduled(cron = "0 42/30  9-11 ? * 1-5 ")
    @Scheduled(cron = "0 12/30 13-15 ? * 1-5 ")
    public void syncStockKlineDailyList() {
        if (isTradeDate()) {
            Threads.sleep(20_000);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            log.info(" --> 同步股票日线行情及股票池筛选【stock_kline_daily】开始");
            stockKlineDailyService.syncStockKlineDailyList();
            stopWatch.stop();
            log.info(" --> 同步股票日线行情及股票池筛选【stock_kline_daily】结束 耗时：{}", DateUtils.formatDateTime(stopWatch.getTotalTimeMillis()));
        }
    }

    // 所有股票基本信息及所属概念，不包含920 ST
    @Scheduled(cron = "0 0 5 ? * 1-5 ")
    public void syncStockInfo() {
        if (isTradeDate()) {
            log.info(" --> 同步股票基本信息【stock_info】开始");
            Result<Integer> result = stockInfoService.syncStockInfoAll();
            log.info(" --> 同步股票基本信息【stock_info】结束 {}", result);
        }
    }

    /**************************** 概念板块 ***********************************/

    // 获取概念板块列表，按涨跌幅排序
    @Scheduled(cron = "0 0/5 9-12,13-15 ? * 1-5 ")
    void syncConceptDaily() {
        if (isTradeTime()) {
            Threads.sleep(10_000);
            log.info(" --> 同步概念板块【concept_daily】开始 top25");
            conceptDelayService.syncConceptTradeList(25);
            log.info(" --> 同步概念板块【concept_daily】结束 top25");
        }
    }

    @Scheduled(cron = "0 5 15 ? * 1-5")
    void syncConceptDailyAll() {
        if (isTradeDate()) {
            log.info(" --> 同步概念板块【concept_daily】开始 全量");
            conceptDelayService.syncConceptTradeList(1000);
            log.info(" --> 同步概念板块【concept_daily】结束 全量");
        }
    }


    /**************************** 龙虎榜 ***********************************/

    @Scheduled(cron = "0 10 17 ? * 1-5 ")
    public void syncDragonDeptList() {
        if (isTradeDate()) {
            log.info(" --> 同步龙虎榜【dragon_dept】开始");
            Result<Integer> result = dragonDeptService.syncDragonDeptList(getTradeDateStr());
            log.info(" --> 同步龙虎榜【dragon_dept 结束: {}", result);
            Threads.sleep(30_000);
            log.info(" --> 同步龙虎榜【dragon_stock】开始");
            result = dragonStockService.syncDragonStockList(getTradeDateStr());
            log.info(" --> 同步龙虎榜【dragon_stock 结束: {}", result);
        }
    }


}
