package com.deepthinking.mysql.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.deepthinking.ext.serialize.CountUtilWriter;
import com.deepthinking.ext.serialize.PercentageWriter;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 游资榜页面
 */
@Data
public class DragonDeptDto  {


    /**************************
     ******** 机构买入 *********
     **************************/


    private String deptCode;

    private String deptName;

    private LocalDate tradeDate;

    private String week;

    private BigDecimal latestPrice;

    private BigDecimal changePercent;

    /**
     * 净买入
     */
    @JSONField( serializeUsing = CountUtilWriter.class)
    private Long deptNetBuyAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalNetBuyRatio;
    /**
     * 买入金额
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long deptBuyAmount;

    /**
     * 买入金额占总成交比例
     */
    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalBuyRatio;

    /**
     * 卖出金额
     */
    @JSONField(  serializeUsing = CountUtilWriter.class)
    private Long deptSellAmount;

    /**
     * 卖出金额占总成交比例
     */
    @JSONField(  serializeUsing = PercentageWriter.class)
    private BigDecimal deptTotalSellRatio;

    private String partners;

}
