package com.deepthinking.rest;

import com.deepthinking.core.base.PageInfo;
import com.deepthinking.core.base.PageResult;
import com.deepthinking.mysql.entity.StockInfo;
import com.deepthinking.service.StockInfoService;
import com.deepthinking.service.StockKlineDailyService;
import com.deepthinking.service.StockKlineMinuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "stock", produces = APPLICATION_JSON)
public class StockRest {

    private final StockInfoService stockInfoService;

    private final StockKlineDailyService stockKlineDailyService;

    private final StockKlineMinuteService stockKlineMinuteService;



    /**
     * 同步更新股票基本信息，所属概念
     */
    @PostMapping("")
    public PageResult<StockInfo> stockList(@RequestBody PageInfo<StockInfo> pageInfo) {
        List<StockInfo> list = stockInfoService.queryPage(pageInfo);
        return PageResult.success(list);
    }


}
