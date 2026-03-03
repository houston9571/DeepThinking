package com.deepthinking.service.impl;

import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.ConceptStock;
import com.deepthinking.mysql.mapper.ConceptStockMapper;
import com.deepthinking.service.ConceptStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptStockServiceImpl extends MybatisBaseServiceImpl<ConceptStockMapper, ConceptStock> implements ConceptStockService {

    private final ConceptStockMapper conceptStockMapper;


}
