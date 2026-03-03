package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private BigDecimal price;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;
    private Long volume;
    private BigDecimal volumeRatio;
    private short signalType;
    private short signalLevel;
    private String signalResult;

    private BigDecimal ema3;
    private BigDecimal ema5;
    private BigDecimal ema10;
    private Short emaGolden;
    private BigDecimal bias;       // 乖离率


    private BigDecimal macdDif;
    private BigDecimal macdDea;
    private BigDecimal macdBar;
    private Short macdGolden;

    //    private BigDecimal rsi3;
    private BigDecimal rsi6;
//    private BigDecimal rsi9;

    private BigDecimal kdjK;
    private BigDecimal kdjD;
    private BigDecimal kdjJ;
    private Short kdjGolden;

    private BigDecimal wr6;

    private BigDecimal bollMid;
    private BigDecimal bollUpper;
    private BigDecimal bollLower;
    private Short bollExpandStatus;

    private BigDecimal vmacdDif;
    private BigDecimal vmacdDea;
    private BigDecimal vmacdBar;
    private Short vmacdGolden;

    private Long obv;
    private Long obvMa5;
    private Short obvGolden;

    // 背离类型：0=无背离,1=MACD顶背离,2=MACD底背离
    private Short divergenceType;
    // 背离强度：0~5（值越大背离越明显）
    private Short divergenceStrength;

    private Short buyScore;
    private String buyReason;
    private Short sellScore;
    private String sellReason;


}
