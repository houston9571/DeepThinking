package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.ext.serialize.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票池
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_pool")
public class StockPool extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(ordinal = 2, alternateNames = "f12")
    private String stockCode;

    /**
     * 股票名称
     */
    @JSONField(ordinal = 3, alternateNames = "f14")
    private String stockName;


    @TableField("trade_date")
    @JSONField(alternateNames = "f297", deserializeUsing = StringToDateReader.class, format = "yyyy-MM-dd")
    private LocalDate tradeDate;

    /**
     * f2 最新价/100
     */
    @JSONField(alternateNames = "f2", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal price;

    /**
     * f3 涨跌幅/100 %
     */
    @JSONField(alternateNames = "f3", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal changePercent;


    /**
     * f8 换手率/100 %
     */
    @JSONField(alternateNames = "f8", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal turnover;

    /**
     * f7 振幅/100 %
     */
    @JSONField(alternateNames = "f7", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal amplitude;

    /**
     * 板块代码
     */
    private String conceptCode;

    /**
     * 板块名称
     */
    private String conceptName;

    private String addReason;
}
