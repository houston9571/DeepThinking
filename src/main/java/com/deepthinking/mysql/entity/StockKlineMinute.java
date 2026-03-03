package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.ext.serialize.CountLotsWriter;
import com.deepthinking.ext.serialize.CountUtilWriter;
import com.deepthinking.ext.serialize.DivideBy100Reader;
import com.deepthinking.ext.serialize.PercentageWriter;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票分钟线行情+技术指标表（15/30/60min）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_kline_minute")
public class StockKlineMinute extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码
     */
    @JSONField(alternateNames = "f57")
    private String stockCode;

    /**
     * 股票名称
     */
    @JSONField(alternateNames = "f58")
    private String stockName;

    @JSONField(format = "yyyy-MM-dd")
    private LocalDate tradeDate;

    @JSONField(format = "HH:mm:ss")
    private LocalTime tradeTime;

    /**
     * f43 最新价/100
     */
    @JSONField(alternateNames = "f43", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal price;

    /**
     * f44 最高价/100
     */
    @JSONField(alternateNames = "f44", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal high;

    /**
     * f45 最低价/100
     */
    @JSONField(alternateNames = "f45", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal low;

    /**
     * f46 今开/100
     */
    @JSONField(alternateNames = "f46", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal open;

    /**
     * f60 昨收/100
     */
    @JSONField(alternateNames = "f60", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal close;

    /**
     * f51 涨停价/100
     */
    @JSONField(alternateNames = "f51", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal limitUp;

    /**
     * f52 跌停价/100
     */
    @JSONField(alternateNames = "f52", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal limitDown;

    /**
     * f47 成交量（手）
     */
    @JSONField(alternateNames = "f47", serializeUsing = CountLotsWriter.class)
    private Long volume;

    /**
     * f50 量比/100
     */
    @JSONField(alternateNames = "f50", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal volumeRatio;

    /**
     * f48 成交额
     */
    @JSONField(alternateNames = "f48", serializeUsing = CountUtilWriter.class)
    private Long amount;

    /**
     * f169 涨跌额/100
     */
    @JSONField(alternateNames = "f169", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal changeAmount;

    /**
     * f170 涨跌幅/100 %
     */
    @JSONField(alternateNames = "f170", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal changePercent;

    /**
     * f168 换手率/100 %
     */
    @JSONField(alternateNames = "f168", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal turnover;

    /**
     * f171 振幅/100 %
     */
    @JSONField(alternateNames = "f171", deserializeUsing = DivideBy100Reader.class, serializeUsing = PercentageWriter.class)
    private BigDecimal amplitude;

    /**
     * f49 外盘（主动性买盘,买方主动"扫货"，按卖方报价成交）
     */
    @JSONField(alternateNames = "f49", serializeUsing = CountUtilWriter.class)
    private Long buyVolume;

    /**
     * f161 内盘（主动性卖盘,卖方主动"砸盘"，按买方报价成交）
     */
    @JSONField(alternateNames = "f161", serializeUsing = CountUtilWriter.class)
    private Long sellVolume;

    /**
     * 委比（%）
     */
    private BigDecimal commission_ratio;

    /**
     * 委差（手）
     */
    private Integer commission_diff;

    /**
     * f116 总市值/100 %
     */
    @JSONField(alternateNames = "f116", serializeUsing = CountUtilWriter.class)
    private Long marketCap;

    /**
     * f117 流通市值/100 %
     */
    @JSONField(alternateNames = "f117", serializeUsing = CountUtilWriter.class)
    private Long freeMarketCap;

    /**
     * f162 市盈(动)/100 % 未来12个月预期盈利 前瞻性
     */
    @JSONField(alternateNames = "f162", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pef;

    /**
     * f163 市盈(静)/100 % 上一个完整财年盈利 最"静态"
     */
    @JSONField(alternateNames = "f163", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pet;

    /**
     * f164 市盈(TTM)/100 % 过去12个月滚动盈利	更及时
     */
    @JSONField(alternateNames = "f164", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pettm;

    /**
     * f167 市净PB/100 %
     */
    @JSONField(alternateNames = "f167", deserializeUsing = DivideBy100Reader.class)
    private BigDecimal pb;


    /**
     * 主力净流入 = 超大单净流入 + 大单净流入
     * 散户净流入 = 中单净流入 + 小单净流入
     */
    private String mainNetIn;

    /**
     * 超大单净流入 Super Large Net Inflow	> 100万元	机构、顶级大户
     */
    private String superLargeNetIn;

    /**
     * 大单净流入	Large Net Inflow	20万 - 100万元	大户、部分机构
     */
    private String largeNetIn;

    /**
     * 中单净流入	Medium Net Inflow	4万 - 20万元	中户
     */
    private String mediumNetIn;

    /**
     * 小单净流入	Small Net Inflow	< 4万元	散户
     */
    private String smallNetIn;

}
