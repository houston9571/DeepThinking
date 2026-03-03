package com.deepthinking.service.impl;

import com.deepthinking.mysql.MybatisBaseServiceImpl;
import com.deepthinking.mysql.entity.ConceptInfo;
import com.deepthinking.mysql.mapper.ConceptInfoMapper;
import com.deepthinking.service.ConceptInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConceptInfoServiceImpl extends MybatisBaseServiceImpl<ConceptInfoMapper, ConceptInfo> implements ConceptInfoService {

    private final ConceptInfoMapper conceptInfoMapper;


}
