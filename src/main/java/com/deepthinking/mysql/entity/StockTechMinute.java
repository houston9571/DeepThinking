package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.strategy.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.deepthinking.common.constant.Constants.ID;

/**
 * 股票分钟线行情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("stock_tech_minute")
public class StockTechMinute extends BaseEntity {

    @TableId(value = ID, type = IdType.AUTO)
    private Integer id;

    private String stockCode;
    private String stockName;

    private LocalDate tradeDate;
    private LocalTime tradeTime;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;
    private Long volume;
    private Long amount;
    private Long totalVolume;
    private Long totalAmount;
    private BigDecimal volumeRatio;
    private StrategyUtils.SignalType signalType;
    private StrategyUtils.SignalLevel signalLevel;
    private String signalResult;

    private BigDecimal ema3;
    private BigDecimal ema5;
    private BigDecimal ema10;
    private Short emaGolden;
    private BigDecimal bias;       // 乖离率


    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private DtMACDIndicator.CrossStatus macdStatus;

    //    private BigDecimal rsi3;
    private BigDecimal rsi6;
//    private BigDecimal rsi9;

    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;
    private DtKDJIndicator.CrossStatus kdjStatus;

    private BigDecimal wr6;

    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;
    private DtBOLLIndicator.MouthStatus bollMouthStatus;
    private DtBOLLIndicator.MidTrend bollMidTrend;

    private BigDecimal vmacdDif;
    private BigDecimal vmacdDea;
    private BigDecimal vmacdBar;
    private DtVMACDIndicator.CrossStatus vmacdStatus;

    private Long obv;
    private Long obvMa5;
    private DtOBVMAIndicator.CrossStatus obvStatus;

    // 背离类型：0=无背离,1=MACD顶背离,2=MACD底背离
    private StrategyUtils.DivergenceType divergenceType;
    // 背离强度：0~5（值越大背离越明显）
    private Short divergenceStrength;

    private Short buyScore;
    private String buyReason;
    private Short sellScore;
    private String sellReason;

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
