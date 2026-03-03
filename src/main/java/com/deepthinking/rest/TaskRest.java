package com.deepthinking.rest;

import com.deepthinking.common.thread.Threads;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.common.utils.NumberUtils;
import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.entity.DragonStock;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.service.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.enums.DateFormatEnum.DATE;
import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "task", produces = APPLICATION_JSON)
public class TaskRest {


    private final ConceptDelayService conceptDelayService;

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final StockKlineMinuteService stockKlineMinuteService;

    private final TradeCalendarService tradeCalendarService;

    private final DragonStockService dragonStockService;

    private final DragonStockDetailService dragonStockDetailService;

    private final DragonDeptService dragonDeptService;

    /**
     * 同步更新所有股票基本信息，所属概念
     */
    @GetMapping("stock/overview")
    public Result<Integer> overview() {
        return stockInfoService.syncStockInfoAll();
    }

    /**
     * 同步单个股票基本信息，所属概念
     */
/*    @GetMapping("stock/{stockCode}}")
    public Result<StockInfo> stock(@PathVariable String stockCode) {
        Result<StockInfo> result = stockInfoService.syncStockInfo(stockCode);
        if (result.isSuccess()) {
            stockInfoService.syncStockConceptList(stockCode);
        }
        return Result.success();
    }*/

    /**
     * 同步概念板块列表
     */
    @GetMapping("concept/daily")
    public Result<Void> syncConceptTradeList() {
        Threads.asyncExecute(() -> conceptDelayService.syncConceptTradeList(true, 100));
        return Result.success();
    }

    /**
     * 获取所有股票当天交易行情
     */
    @GetMapping("stock/daily")
    public Result<Void> syncStockTradeList() {
        Threads.asyncExecute(stockKlineDailyService::syncStockKlineDailyList);
        return Result.success();

    }

    /**
     * 龙虎榜营业部列表
     */
    @GetMapping("dragon/dept/{date}")
    public Result<Void> syncDragonDeptList(@PathVariable String date) {
        LocalDate end = DateUtils.parseLocalDate("2025-10-01", DATE);
        LocalDate from = DateUtils.parseLocalDate(date, DATE);
        while (from.isAfter(end)) {
            if (from.getDayOfWeek().getValue() < 6) {
                dragonDeptService.syncDragonDeptList(DateUtils.format(from, DATE));
                Threads.sleep(2000);
            }
            from = from.plusDays(-1);
        }
        return Result.success();
    }

    /**
     * 龙虎榜个股列表
     */
    @GetMapping("dragon/stock/{date}")
    public Result<Void> syncDragonStockList(@PathVariable String date) {
        LocalDate end = DateUtils.parseLocalDate("2025-10-01", DATE);
        LocalDate from = DateUtils.parseLocalDate(date, DATE);
        while (from.isAfter(end)) {
            if (from.getDayOfWeek().getValue() < 6) {
                Result<List<DragonStock>> result = dragonStockService.syncDragonStockList(DateUtils.format(from, DATE));
                if (result.hasData()) {
                    List<DragonStock> list = result.getData();
                    int count = 0;
                    for (DragonStock d : list) {
                        int cc = dragonStockDetailService.syncDragonStockDetail(d.getTradeDate(), d.getStockCode(), d.getStockName());
                        if (cc == 0) {
                            // 连续请求容易超时，重试一次
                            Threads.sleep(NumberUtils.random(5000));
                            cc = dragonStockDetailService.syncDragonStockDetail(d.getTradeDate(), d.getStockCode(), d.getStockName());
                        }
                        count += cc;
                    }
                    log.info(">>>>>getStockDragonDetail: {} total_save_size:{}", date, count);
                }
                Threads.sleep(2000);
            }
            from = from.plusDays(-1);
        }
        return Result.success();
    }


    /**
     * 获取股票实时交易行情 资金流向
     *
     * @param codes
     * @return
     */
    @GetMapping("stock/kline/{codes}")
    public Result<List<StockKlineMinute>> getStockTradeRealtime(@PathVariable String codes) {
        List<StockKlineMinute> list = Lists.newArrayList();
        String[] codeArray = codes.split(COMMA);
        for (String code : codeArray) {
            Result<StockKlineMinute> result = stockKlineMinuteService.syncStockKlineMinute(code);
            if (result.isSuccess()) {
                list.add(result.getData());
            }
        }
        return Result.success(list);
    }


    @GetMapping("genCalendar")
    public Result<Integer> genCalendar() {
        return Result.success(tradeCalendarService.genYearCalendar());
    }


   /* @GetMapping("fundHoldInfo/{scode}")
    public JSONResult fundHoldInfo(@PathVariable String scode) {
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S02-fundHoldInfo.json", createMap(scode));
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        stockFundHoldService.save(scode, factors.get(0)[0]);
        return fundHoldDetail(scode, "", 1);
    }

    private JSONResult fundHoldDetail(String scode, String exDate, int page) {
        int pageSize = 50;
        Map<String, String> param = createMap(scode);
        param.put("pageNo", page + "");
        param.put("pageSize", pageSize + "");
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S02-fundHoldDetail.json", param);
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        Map<String, String>[] fundHoldDetailData = factors.get(0);
        log.info("---->fundHoldDetailData pageNo:{} size:{} ", page, fundHoldDetailData.length);
        if (fundHoldDetailData.length > 0) {
            stockFundHoldDetailService.batchSave(fundHoldDetailData);
            String expirationDate = fundHoldDetailData[0].get("expirationDate");
            if (StringUtils.isEmpty(exDate) || exDate.equals(expirationDate)) {  // 该接口会返回往年所有的明细，遇到时间改变时停止
                fundHoldDetail(scode, expirationDate, page + 1);
            }
        }
        return JSONResult.success();
    }*/

  /*  @GetMapping("moneyFlow/{scode}")
    public JSONResult moneyFlow(@PathVariable String scode) {
        List<Map<String, String>[]> factors = spiderTemplateParser.parserAsMap("S03-moneyFlow.json", createMap(scode));
        if (CollectionUtils.isEmpty(factors)) {
            return JSONResult.failed("未获取到页面信息");
        }
        Map<String, String>[] maps = factors.get(0);
        if (ArrayUtils.isNotEmpty(maps)) {
            return stockMoneyFlowService.save(scode, maps[0]);
        }
        return JSONResult.success();
    }*/


    /*

     */
/**
 * 执行股票表和基金持有的股票
 *//*

    @GetMapping("moneyFlowTask")
    public JSONResult moneyFlowTask() {
        JSONResult result = stockOverviewService.allScode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                moneyFlow(array.getJSONObject(i).getString("scode"));
            }
        }
        return JSONResult.success("");
    }

    @GetMapping("overviewTask")
    public JSONResult overviewTask() {
        JSONResult result = stockOverviewService.allScode();
        if (result.isSuccess()) {
            JSONArray array = result.getData();
            for (int i = 0; i < array.size(); i++) {
                overview(array.getJSONObject(i).getString("scode"));
            }
        }
        return JSONResult.success("");
    }
*/

}
