package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.core.ext.StringToDateReader;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票筹码分布表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_chip")
public class StockChip extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(ordinal = 2, alternateNames = "f12")
    private String stockCode;


    @TableField("trade_date")
    @JSONField(alternateNames = "f297", deserializeUsing = StringToDateReader.class, format = "yyyy-MM-dd")
    private LocalDate tradeDate;

    /**
     * 筹码集中度（%，越小越集中）
     */
    private BigDecimal chipConcentration;

    /**
     * '获利盘比例（%）'
     */
    private BigDecimal profitRatio;
    /**
     * '90%筹码成本区间价差（%）'
     */
    private BigDecimal costRange90;
    /**
     * '平均持仓成本（元）'
     */
    private BigDecimal avgCost;
    /**
     * '筹码峰最高价（元）'
     */
    private BigDecimal topChipPrice;
    /**
     * '筹码峰最低价（元）'
     */
    private BigDecimal bottomChipPrice;




}
