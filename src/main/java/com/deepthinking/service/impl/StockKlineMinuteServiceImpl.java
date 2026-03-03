package com.deepthinking.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.deepthinking.client.EastMoneyH5Api;
import com.deepthinking.client.EastMoneyStockApi;
import com.deepthinking.common.enums.DateFormatEnum;
import com.deepthinking.common.utils.DateUtils;
import com.deepthinking.ext.base.Result;
import com.deepthinking.common.constant.MarketType;
import com.deepthinking.common.constant.StockCodeUtils;
import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.StockKlineMinute;
import com.deepthinking.mysql.mapper.StockKlineMinuteMapper;
import com.deepthinking.service.StockKlineMinuteService;
import com.deepthinking.service.StockTechMinuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.text.StrPool.COMMA;
import static com.deepthinking.common.constant.Constants.LABEL_DATA;
import static com.deepthinking.common.enums.ErrorCode.NOT_GET_PAGE_ERROR;
import static com.deepthinking.common.constant.StockConstants.KLINE_1MIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockKlineMinuteServiceImpl extends MybatisBaseServiceImpl<StockKlineMinuteMapper, StockKlineMinute> implements StockKlineMinuteService {

    private final StockKlineMinuteMapper stockKlineMinuteMapper;

    private final EastMoneyStockApi eastMoneyStockApi;

    private final EastMoneyH5Api eastMoneyH5Api;

    private final StockTechMinuteService stockTechMinuteService;

    /**
     * 根据股票池更新个股分时数据 1分钟
     */
    public void syncStockKlineMinutePools() {
        syncStockKlineMinute("000547");
    }



    /**
     * 股票实时交易行情和资金流向 每1分钟
     * 获取最后10条，容错及指标计算使用
     */
    public Result<StockKlineMinute> syncStockKlineMinute(String stockCode) {
        String fields = "f80,f43,f44,f45,f46,f47,f48,f49,f50,f51,f52,f57,f58,f60,f116,f117,f161,f162,f163,f164,f167,f168,f169,f170,f171,f178";
        JSONObject kline = eastMoneyStockApi.getStockTradeRealtime(stockCode, MarketType.getMarketCode(stockCode), fields);
        StockKlineMinute stockKlineMinute = JSONObject.parseObject(kline.getString(LABEL_DATA), StockKlineMinute.class);
//        String transactionDate = kline.getJSONObject(LABEL_DATA).getJSONArray("f80").getJSONObject(1).getString("e");

        JSONObject flow = eastMoneyStockApi.getFundsFlowLines(stockCode, MarketType.getMarketCode(stockCode), KLINE_1MIN, 240);
        JSONObject data = flow.getJSONObject(LABEL_DATA);
        if (ObjectUtil.isEmpty(data) || !data.containsKey("klines")) {
            return Result.fail(NOT_GET_PAGE_ERROR, "");
        }

        JSONArray lines = data.getJSONArray("klines");
        List<StockKlineMinute> minuteList = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            String[] line = lines.getString(0).split(COMMA);
            String[] t = line[0].split("\\s+");
            stockKlineMinute.setTradeDate(DateUtils.parseLocalDate(t[0], DateFormatEnum.DATE));
            stockKlineMinute.setTradeTime(DateUtils.parseLocalTime(t[1] + ":00", DateFormatEnum.TIME));
            stockKlineMinute.setMainNetIn(line[1]);
            stockKlineMinute.setSmallNetIn(line[2]);
            stockKlineMinute.setMediumNetIn(line[3]);
            stockKlineMinute.setLargeNetIn(line[4]);
            stockKlineMinute.setSuperLargeNetIn(line[5]);

        }

        saveOrUpdate(stockKlineMinute, new String[]{"stock_code", "trade_date", "trade_time"});

        List<StockKlineMinute> prev10MinuteList = stockKlineMinuteMapper.queryPrevious10Minute(stockCode);
        stockTechMinuteService.calculateMinuteIndicatorAndSave(prev10MinuteList);

        return Result.success(stockKlineMinute);
    }


    public Result<JSONObject> getFirstRequest2Data(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("fc", StockCodeUtils.buildSecId(code));
        eastMoneyH5Api.getFirstRequest2Data(params);
        return Result.success();
    }

}
