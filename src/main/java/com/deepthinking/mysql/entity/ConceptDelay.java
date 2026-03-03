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
 * 板块每日行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("concept_daily")
public class ConceptDelay extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 板块代码
     */
    @JSONField(ordinal = 2, alternateNames = "f12")
    private String conceptCode;

    /**
     * 板块名称
     */
    @JSONField(ordinal = 3, alternateNames = "f14")
    private String conceptName;

    /**
     *
     */
    @JSONField(alternateNames = "f13")
    private String market;

    @TableField("trade_date")
    @JSONField(alternateNames = "f297", deserializeUsing = StringToDateReader.class, format = "yyyy-MM-dd")
    private LocalDate tradeDate;

    @TableField(exist = false)
    private String week;


    /**
     * 股票数量
     */
    private int stockNum;

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
     * f4 涨跌额/100
     */
    @JSONField(alternateNames = "f4", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal changeAmount;

    /**
     * f6 成交额
     */
    @JSONField(alternateNames = "f6", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long amount;

    /**
     * f8 换手率/100 %
     */
    @JSONField(alternateNames = "f8", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal turnover;

    /**
     * f20 总市值
     */
    @JSONField(alternateNames = "f20", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long marketCap;

    /**
     * f104 上涨家数
     */
    @JSONField(alternateNames = "f104", deserializeUsing = NumberCheckReader.class)
    private Long upNum;

    /**
     * f105 下跌家数
     */
    @JSONField(alternateNames = "f105", deserializeUsing = NumberCheckReader.class)
    private Long downNum;


    /**************************
     ******** 资金流向 *********
     **************************/

    /**
     * f138 超大单流入
     */
    @JSONField(alternateNames = "f138", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeIn;

    /**
     * f139 超大单流出
     */
    @JSONField(alternateNames = "f139", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeOut;

    /**
     * f140 超大单净流入
     */
    @JSONField(alternateNames = "f140", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long superLargeNetIn;

    /**
     * 超大单净比
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal superLargeNetRatio;

    /**
     * f141 大单流入
     */
    @JSONField(alternateNames = "f141", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeIn;

    /**
     * f142 大单流出
     */
    @JSONField(alternateNames = "f142", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeOut;

    /**
     * f143 大单净流入
     */
    @JSONField(alternateNames = "f143", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long largeNetIn;

    /**
     * 大单净比
     */
    @JSONField(  serializeUsing = PercentageWriter.class)
    private BigDecimal largeNetRatio;

    /**
     * f144 中单流入
     */
    @JSONField(alternateNames = "f144", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumIn;

    /**
     * f145 中单流出
     */
    @JSONField(alternateNames = "f145", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumOut;

    /**
     * f146 中单净流入
     */
    @JSONField(alternateNames = "f146", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long mediumNetIn;

    /**
     * 中单净比
     */
    @JSONField(serializeUsing = PercentageWriter.class)
    private BigDecimal mediumNetRatio;

    /**
     * f147 小单流入
     */
    @JSONField(alternateNames = "f147", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallIn;

    /**
     * f148 小单流出
     */
    @JSONField(alternateNames = "f148", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallOut;

    /**
     * f149 小单净流入
     */
    @JSONField(alternateNames = "f149", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long smallNetIn;

    /**
     * 小单净比
     */
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
