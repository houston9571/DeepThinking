package com.deepthinking.task;

import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.core.base.Result;
import com.deepthinking.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalTime;

//@Lazy
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTask {

    private final TradeCalendarService tradeCalendarService;

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final ConceptDelayService conceptDelayService;

    private final StockKlineMinuteService stockKlineMinuteService;


    /**
     * 根据股票池更新个股分时数据及指标计算
     * 每1分钟
     */
    @Scheduled(cron = "0 0/1 9-12,13-15 ? * 1-5")
    void syncStockKlineMinutePools(){
        if (tradeCalendarService.isTradeTime()) {
            log.info(" --> 同步个股分时数据及指标计算【stock_kline_minute、stock_tech_minute】开始 ");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            stockKlineMinuteService.syncStockKlineMinutePools();
            stopWatch.stop();
            log.info(" --> 同步个股分时数据及指标计算【stock_kline_minute、stock_tech_minute】结束 {}", DateUtils.formatDateTime(stopWatch.getTotalTimeMillis()));
        }
    }

    /**
     * 获取股票实时交易列表，不包含688 920 ST
     * 10:00:30 10:30:30 11:00:30 11:30:30
     * 13:30:30 14:00:30 14:30:30 15:00:30
     * 第30秒执行，避开其他任务
     */
    @Scheduled(cron = "30 0/30 10-11,13-15 ? * 1-5 ")
    public void syncStockKlineDailyList() {
        if (tradeCalendarService.isTradeTime(LocalTime.now().plusMinutes(-1))) {
            log.info(" --> 同步股票实时交易列表【stock_kline_daily】开始");
            stockKlineDailyService.syncStockKlineDailyList();
            log.info(" --> 同步股票实时交易列表【stock_kline_daily】结束");
        }
    }

    /**
     * 获取概念板块列表，按涨跌幅排序
     * 每10分钟执行一次 top25
     */
    @Scheduled(cron = "20 0/10 9-12,13-15 ? * 1-5 ")
    void syncConceptDaily(){
        if (tradeCalendarService.isTradeTime(LocalTime.now().plusSeconds(-20))) {
            log.info(" --> 同步概念板块列表【concept_daily】开始 top25");
            conceptDelayService.syncConceptTradeList(false, 25);
            log.info(" --> 同步概念板块列表【concept_daily】结束 top25");
        }
    }


    /**
     * 获取概念板块列表，按涨跌幅排序
     * 每天 15:05:05 执行一次全量
     */
    @Scheduled(cron = "05 5 15 ? * 1-5")
    void syncConceptDailyAll(){
        if (tradeCalendarService.isTradeDate()) {
            log.info(" --> 同步概念板块列表【concept_daily】开始 全量");
            conceptDelayService.syncConceptTradeList(true, 100);
            log.info(" --> 同步概念板块列表【concept_daily】结束 全量");
        }
    }



    /**
     * 所有股票基本信息及所属概念，不包含920
     * 每周六早上5点
     */
    @Scheduled(cron = "0 0 5 ? * 6 ")
    public void syncStockInfo() {
        log.info(" --> 每周六早上5点，同步【stock_info】开始");
        Result<Integer> result = stockInfoService.syncStockInfoAll();
        log.info(" --> 每周六早上5点，同步【stock_info】结束: {}", result);
    }



}
