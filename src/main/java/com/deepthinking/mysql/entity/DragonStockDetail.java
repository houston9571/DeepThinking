package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.core.ext.CountLotsWriter;
import com.deepthinking.core.ext.CountUtilWriter;
import com.deepthinking.core.ext.PercentageWriter;
import com.deepthinking.core.ext.StringToDateReader;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("dragon_stock_detail")
public class DragonStockDetail extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(alternateNames = "SECURITY_CODE")
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    @TableField("trade_date")
    @JSONField(alternateNames = "TRADE_DATE", format = "yyyy-MM-dd", deserializeUsing = StringToDateReader.class)
    private LocalDate tradeDate;

    /**
     * 营业部代码
     */
    @JSONField( alternateNames = "OPERATEDEPT_CODE")
    private String deptCode;

    /**
     * 收盘价
     */
    @JSONField(alternateNames = "CLOSE_PRICE"  )
    private BigDecimal close;

    /**
     * 涨幅
     */
    @JSONField(alternateNames = "CHANGE_RATE", serializeUsing = PercentageWriter.class )
    private BigDecimal changePercent;

    /**
     * 净买入
     */
    @JSONField(alternateNames = "NET", serializeUsing = CountUtilWriter.class)
    private Long netBuyAmount;

    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal totalNetBuyRatio;

    /**
     * 买入金额
     */
    @JSONField(alternateNames = "BUY", serializeUsing = CountUtilWriter.class)
    private Long buyAmount;

    /**
     * 买入金额占总成交比例
     */
    @JSONField(alternateNames = "TOTAL_BUYRIO", serializeUsing = PercentageWriter.class)
    private BigDecimal totalBuyRatio;

    /**
     * 卖出金额
     */
    @JSONField(alternateNames = "SELL", serializeUsing = CountUtilWriter.class)
    private Long sellAmount;

    /**
     * 卖出金额占总成交比例
     */
    @JSONField(alternateNames = "TOTAL_SELLRIO", serializeUsing = PercentageWriter.class)
    private BigDecimal totalSellRatio;

    /**
     * 市场总成交量
     */
    @JSONField(alternateNames = "ACCUM_VOLUME", serializeUsing = CountLotsWriter.class)
    private Long accumVolume;

    /**
     * 市场总成交额
     */
    @JSONField(alternateNames = "ACCUM_AMOUNT", serializeUsing = CountUtilWriter.class)
    private Long amount;


    /**
     * 上榜原因
     */
    @JSONField(alternateNames = "EXPLANATION")
    private String explanation;


    @JSONField(alternateNames = "TRADE_ID")
    private Integer tradeId;

    @JSONField(alternateNames = "OPERATEDEPT_CODE_OLD")
    private String deptCodeOld;

    @TableField(exist = false)
    private String partnerCode;

    @TableField(exist = false)
    private String partnerName;

    @TableField(exist = false)
    private List<DragonStockDetail> partners;

}
