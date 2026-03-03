package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.core.ext.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票每日行情
 * 存储交易所原始日线数据，作为所有技术指标的计算基础。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_kline_daily")
public class StockKlineDaily extends BaseEntity {


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

    /**************************
     ******** 交易数据 *********
     **************************/

    /**
     * f2 最新价/100
     */
    @JSONField(alternateNames = "f2", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal price;

    /**
     * f15 最高价/100
     */
    @JSONField(alternateNames = "f15", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal high;

    /**
     * f16 最低价/100
     */
    @JSONField(alternateNames = "f16", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal low;

    /**
     * f17 今开/100
     */
    @JSONField(alternateNames = "f17", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal open;

    /**
     * f18 昨收/100
     */
    @JSONField(alternateNames = "f18", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal close;

    /**
     * 涨停价 = 今开 * 110%
     */
    private BigDecimal limitUp;

    /**
     * 跌停价 = 今开 * 90%
     */
    private BigDecimal limitDown;

    /**
     * f5 成交量（手）
     */
    @JSONField(alternateNames = "f5", deserializeUsing = NumberCheckReader.class, serializeUsing = CountLotsWriter.class)
    private Long volume;

    /**
     * f10 量比/100
     */
    @JSONField(alternateNames = "f10", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal volumeRatio;

    /**
     * f6 成交额
     */
    @JSONField(alternateNames = "f6", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long amount;

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
     * f34 外盘（主动性买盘,买方主动"扫货"，按卖方报价成交）
     */
    @JSONField(alternateNames = "f34", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long buyVolume;

    /**
     * f35 内盘（主动性卖盘,卖方主动"砸盘"，按买方报价成交）
     */
    @JSONField(alternateNames = "f35", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long sellVolume;

    /**
     * 委比（%）
     */
    private BigDecimal commission_ratio;

    /**
     * 委差（手）
     */
    private Integer commission_diff;

    /**************************
     ******** 动态数据 *********
     **************************/
    /**
     * f20 总市值
     */
    @JSONField(alternateNames = "f20", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long marketCap;

    /**
     * f21 流通市值/100
     */
    @JSONField(alternateNames = "f21", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long freeMarketCap;

    /**
     * f9 市盈率(动)/100 未来12个月预期盈利 前瞻性
     */
    @JSONField(alternateNames = "f9", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pef;

    /**
     * f23 市净率PB/100
     */
    @JSONField(alternateNames = "f23", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pb;

    /**
     * f109 5日涨跌幅/100
     */
//    @JSONField(alternateNames = "f109", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
//    private BigDecimal changePercent5;

    /**
     * f24 60日涨跌幅/100
     */
//    @JSONField(alternateNames = "f24", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
//    private BigDecimal changePercent60;

    /**
     * f37 ROE
     */
    @JSONField(alternateNames = "f37", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal roe;

    /**
     * f40 总营收
     */
    @JSONField(alternateNames = "f40", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long totalRevenue;

    /**
     * f41 总营收同比
     */
    @JSONField(alternateNames = "f41", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal totalRevenueRate;

    /**
     * f45 净利润
     */
    @JSONField(alternateNames = "f45", deserializeUsing = NumberCheckReader.class, serializeUsing = CountUtilWriter.class)
    private Long netProfit;

    /**
     * f46 净利润同比
     */
    @JSONField(alternateNames = "f46", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal netProfitRate;

    /**
     * "f49 毛利率
     */
    @JSONField(alternateNames = "f49", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal grossMargin;

    /**
     * f129 净利率
     */
    @JSONField(alternateNames = "f129", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal netMargin;

    /**
     * f57 负债率
     */
    @JSONField(alternateNames = "f57", deserializeUsing = NumberCheckReader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal debtRatio;


//    /**
//     * f163 市盈率(静)/100 % 上一个完整财年盈利 最"静态"
//     */
//    @JSONField(alternateNames = "f163", deserializeUsing = DivideBy100Reader.class)
//    private BigDecimal pet;
//
//    /**
//     * f164 市盈率(TTM)/100 % 过去12个月滚动盈利	更及时
//     */
//    @JSONField(alternateNames = "f164", deserializeUsing = DivideBy100Reader.class)
//    private BigDecimal pettm;


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
