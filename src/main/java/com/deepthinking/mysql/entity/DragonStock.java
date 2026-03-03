package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.core.ext.CountUtilWriter;
import com.deepthinking.core.ext.PercentageWriter;
import com.deepthinking.core.ext.StringToDateReader;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("dragon_stock")
public class DragonStock extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(ordinal = 2, alternateNames = "SECURITY_CODE")
    private String stockCode;

    /**
     * 股票名称
     */
    @JSONField(ordinal = 3, alternateNames = "SECURITY_NAME_ABBR")
    private String stockName;


    @TableField("trade_date")
    @JSONField(alternateNames = "TRADE_DATE", format = "yyyy-MM-dd", deserializeUsing = StringToDateReader.class)
    private LocalDate tradeDate;


    /**
     * 收盘价
     */
    @JSONField(alternateNames = "CLOSE_PRICE")
    private BigDecimal close;

    /**
     * 涨跌幅
     */
    @JSONField(alternateNames = "CHANGE_RATE", serializeUsing = PercentageWriter.class)
    private BigDecimal changePercent;

    /**
     * 龙虎榜 净买额
     */
    @JSONField(alternateNames = "BILLBOARD_NET_AMT", serializeUsing = CountUtilWriter.class)
    private Long netBuyAmount;

    /**
     * 龙虎榜 净买额/市场总成交额
     */
    @JSONField(alternateNames = "DEAL_NET_RATIO", serializeUsing = PercentageWriter.class)
    private BigDecimal netBuyAmountRatio;

    /**
     * 龙虎榜 买入额
     */
    @JSONField(alternateNames = "BILLBOARD_BUY_AMT", serializeUsing = CountUtilWriter.class)
    private Long buyAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal buyAmountRatio;

    /**
     * 龙虎榜 卖出额
     */
    @JSONField(alternateNames = "BILLBOARD_SELL_AMT", serializeUsing = CountUtilWriter.class)
    private Long sellAmount;

    @JSONField( serializeUsing = PercentageWriter.class)
    private BigDecimal sellAmountRatio;

    /**
     * 龙虎榜 成交额
     */
    @JSONField(alternateNames = "BILLBOARD_DEAL_AMT", serializeUsing = CountUtilWriter.class)
    private Long dealAmount;

    /**
     * 龙虎榜 成交额/市场总成交额
     */
    @JSONField(alternateNames = "DEAL_AMOUNT_RATIO", serializeUsing = PercentageWriter.class)
    private BigDecimal dealAmountRatio;

    /**
     * 市场总成交额
     */
    @JSONField(alternateNames = "ACCUM_AMOUNT", serializeUsing = CountUtilWriter.class)
    private Long amount;


    /**
     * 流通市值
     */
    @JSONField(alternateNames = "FREE_MARKET_CAP", serializeUsing = CountUtilWriter.class)
    private Long freeMarketCap;

    /**
     * 解读
     */
    @JSONField(alternateNames = "EXPLAIN")
    private String explains;

    /**
     * 上榜原因
     */
    @JSONField(alternateNames = "EXPLANATION")
    private String explanation;


    @JSONField(alternateNames = "D1_CLOSE_ADJCHRATE")
    private BigDecimal d1CloseAdjchrate;

    @JSONField(alternateNames = "D2_CLOSE_ADJCHRATE")
    private BigDecimal d2CloseAdjchrate;

    @JSONField(alternateNames = "D5_CLOSE_ADJCHRATE")
    private BigDecimal d5CloseAdjchrate;

    @JSONField(alternateNames = "D10_CLOSE_ADJCHRATE")
    private BigDecimal d10CloseAdjchrate;

    @JSONField(alternateNames = "SECURITY_TYPE_CODE")
    private String securityTypeCode;


}
