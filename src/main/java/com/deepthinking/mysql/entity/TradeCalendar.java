package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("trade_calendar")
public class TradeCalendar extends BaseEntity {

    @TableId(value = "date", type = IdType.INPUT)
    private LocalDate date;

    private String week;

    private Short isTrade;

    private String holiday;

    private String sh;

    private String shName;

    private BigDecimal shIndex;

    private BigDecimal shRange;

    private BigDecimal shRate;

}
