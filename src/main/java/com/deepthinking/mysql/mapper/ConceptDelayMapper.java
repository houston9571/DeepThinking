package com.deepthinking.mysql.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.deepthinking.mysql.entity.ConceptDelay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface ConceptDelayMapper extends BaseMapper<ConceptDelay> {

    @Select("SELECT DISTINCT trade_date FROM concept_daily ORDER BY trade_date DESC LIMIT #{days}")
    List<ConceptDelay> queryConceptTradeDate(int days);


    @Select("SELECT * FROM concept_daily WHERE trade_date=#{tradeDate} ORDER BY change_percent DESC LIMIT #{top}")
    List<ConceptDelay> queryConceptTop(@Param("tradeDate") LocalDate tradeDate, @Param("top") int top);
}
