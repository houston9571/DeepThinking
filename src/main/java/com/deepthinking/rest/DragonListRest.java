package com.deepthinking.rest;

import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.entity.DragonStockDetail;
import com.deepthinking.mysql.vo.DragonDetailPartner;
import com.deepthinking.mysql.vo.DragonDetailStockKline;
import com.deepthinking.service.DragonStockDetailService;
import com.deepthinking.service.DragonStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "dragon", produces = APPLICATION_JSON)
public class DragonListRest {

    private final DragonStockService dragonStockService;

    private final DragonStockDetailService dragonStockDetailService;

    /**
     * 按游资席位查询最近龙虎榜，竖型列表，第一行表头日期
     */
    @GetMapping("partner/{tradeDate}")
    public Result<List<List<DragonStockDetail>>> conceptList(@PathVariable String tradeDate) {
        return Result.success(dragonStockDetailService.queryDragonStockDetailWithPartner(tradeDate));
    }


    /**
     * 查询当天龙虎榜列表，按游资分类
     */
    @GetMapping("stock/{tradeDate}")
    public Result<List<DragonDetailStockKline>> queryDragonStockList(@PathVariable String tradeDate) {
        return Result.success(dragonStockService.queryDragonStockList(tradeDate));
    }



    /**
     * 查询股票龙虎榜详细信息
     */
    @GetMapping("detail/stock/{stockCode}")
    public Result<List<DragonDetailStockKline>> queryDragonStockDetail(@PathVariable String stockCode) {
        return Result.success(dragonStockService.queryDragonStockDetail(stockCode));
    }

    /**
     * 查询游资龙虎榜详细信息
     */
    @GetMapping("detail/partner/{partnerCode}")
    public Result<List<DragonDetailPartner>> queryDragonPartnerDetail(@PathVariable String partnerCode) {
        return Result.success(dragonStockService.queryDragonPartnerDetail(partnerCode));
    }




}
