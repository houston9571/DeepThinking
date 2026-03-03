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
import java.time.LocalDateTime;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 资金流向
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_capital")
public class StockCapital extends BaseEntity {


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

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tradeDatetime;


    /**************************
     ******** 资金流向 *********
     **************************/

    /**
     * f64 超大单流入 > 100万元	机构、顶级大户
     */
    @JSONField(alternateNames = "f64", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeIn;

    /**
     * f65 超大单流出
     */
    @JSONField(alternateNames = "f65", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeOut;

    /**
     * f66 超大单净流入
     */
    @JSONField(alternateNames = "f66", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeNetIn;

    /**
     * f69 超大单净比
     */
    @JSONField(alternateNames = "f69", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal superLargeNetRatio;

    /**
     * f70 大单流入 20万 - 100万元	大户、部分机构
     */
    @JSONField(alternateNames = "f70", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeIn;

    /**
     * f71 大单流出
     */
    @JSONField(alternateNames = "f71", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeOut;

    /**
     * f72 大单净流入
     */
    @JSONField(alternateNames = "f72", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeNetIn;

    /**
     * f75 大单净比
     */
    @JSONField(alternateNames = "f75", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal largeNetRatio;

    /**
     * f76 中单流入 4万 - 20万元	中户
     */
    @JSONField(alternateNames = "f76", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumIn;

    /**
     * f77 中单流出
     */
    @JSONField(alternateNames = "f77", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumOut;

    /**
     * f78 中单净流入
     */
    @JSONField(alternateNames = "f78", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumNetIn;

    /**
     * f81 中单净比
     */
    @JSONField(alternateNames = "f81", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal mediumNetRatio;

    /**
     * f82 小单流入 < 4万元	散户
     */
    @JSONField(alternateNames = "f82", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallIn;

    /**
     * f83 小单流出
     */
    @JSONField(alternateNames = "f83", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallOut;

    /**
     * f84 小单净流入
     */
    @JSONField(alternateNames = "f84", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallNetIn;

    /**
     * f87 小单净比
     */
    @JSONField(alternateNames = "f87", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal smallNetRatio;

    /**
     * 主力流入 = 超大单流入 + 大单流入
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long mainIn;

    /**
     * 主力流出
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long mainOut;

    /**
     * 主力净流入
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long mainNetIn;

    /**
     * 主力净比
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal mainNetRatio;

    /**
     * 散户流入 = 中单流入 + 小单流入
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long retailIn;

    /**
     * 散户流出
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long retailOut;

    /**
     * 散户净流入
     */
    @JSONField(serializeUsing = CountUtilWriter.class)
    private Long retailNetIn;

    /**
     * 散户净比
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal retailNetRatio;


}
