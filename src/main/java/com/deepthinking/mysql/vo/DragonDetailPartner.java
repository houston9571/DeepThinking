package com.deepthinking.mysql.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.deepthinking.core.ext.CountUtilWriter;
import com.deepthinking.core.ext.PercentageWriter;
import com.deepthinking.mysql.entity.StockKlineDaily;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 龙虎榜页面
 */
@Data
@Builder
public class DragonDetailPartner  {


    private String partnerCode;

    private String partnerName;

    private String deptCode;

    private String deptName;

    /**
     * 净买入
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long partnerNetBuyAmount;

    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal partnerNetBuyRatio;

    /**
     * 买入金额
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long partnerBuyAmount;

    /**
     * 买入金额占总成交比例
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal partnerBuyRatio;

    /**
     * 卖出金额
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long partnerSellAmount;

    /**
     * 卖出金额占总成交比例
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal partnerSellRatio;

    private List<StockKlineDaily> stocks;

}
