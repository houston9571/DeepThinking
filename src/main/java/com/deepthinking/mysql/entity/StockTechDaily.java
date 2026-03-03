package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票日线技术指标表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_tech_daily")
public class StockTechDaily extends BaseEntity {


    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    private String stockCode;
    private String stockName;

    @JSONField(format = "yyyy-MM-dd")
    private LocalDate tradeDate;
    private BigDecimal price;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;

    // 超短线均线：3/5/10/20
    private BigDecimal ma3;
    private BigDecimal ma5;
    private BigDecimal ma10;
    private BigDecimal ma20;

    // MACD(5,13,1)
    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private BigDecimal macdDiff;

    // 超短线RSI(3,9)
    private BigDecimal rsi3;
    private BigDecimal rsi9;

    // KDJ(5)
    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;

    // CCI(8)
    private BigDecimal cci;

    // BOLL(10) 布林带状态：1=收口,2=开口,3=正常
    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;
    private Integer bollStatus;

    // ATR(6)
    private BigDecimal atr;
    private BigDecimal atrRatio;

    // WR(6)
    private BigDecimal wr6;

    // MFI(8)
    private BigDecimal mfi;

    // VMACD(5,13,1)
    private BigDecimal vmacdDif;
    private BigDecimal vmacdDea;
    private BigDecimal vmacdBar;
    private BigDecimal vmacdDiff;

    // OBV + OBV_MA10
    private Long obv;
    private Long obvMa10;
    private Long obvDiff;

    // 筹码平均成本（成交量加权）(30日)
    private BigDecimal avgCost;
    // 筹码集中度（%，值越小越集中）
    private BigDecimal costConcentration;

    // 背离类型：0=无背离,1=MACD顶背离,2=MACD底背离,3=RSI顶背离,4=RSI底背离,5=KDJ顶背离,6=KDJ底背离,7=CCI顶背离,8=CCI底背离
    private Integer divergenceType;
    // 背离强度：0~100（值越大背离越明显）
    private BigDecimal divergenceStrength;

    // 共振信号：0=无信号,1=短线买入,2=短线卖出,3=趋势走强,4=趋势走弱
    private Integer resonanceSignal;
    // 共振评分：0~100（值越高信号越可靠）
    private BigDecimal resonanceScore;


}
