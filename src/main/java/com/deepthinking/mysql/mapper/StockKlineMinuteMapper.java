package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.StockKlineMinute;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface StockKlineMinuteMapper extends BaseMapper<StockKlineMinute> {

    @Select(" SELECT * FROM ( " +
            "   SELECT * FROM stock_kline_minute WHERE stock_code= #{stockCode} ORDER BY trade_date DESC, trade_time DESC LIMIT 10 ) a " +
            " ORDER BY trade_date, trade_time ")
    List<StockKlineMinute> queryPrevious10Minute(String stockCode);

}
