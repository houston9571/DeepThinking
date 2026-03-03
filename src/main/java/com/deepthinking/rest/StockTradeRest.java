package com.deepthinking.rest;

import com.deepthinking.service.StockKlineDailyService;
import com.deepthinking.service.StockKlineMinuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "stock/trade", produces = APPLICATION_JSON)
public class StockTradeRest {



    private final StockKlineDailyService stockKlineDailyService;

    private final StockKlineMinuteService stockKlineMinuteService;




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
