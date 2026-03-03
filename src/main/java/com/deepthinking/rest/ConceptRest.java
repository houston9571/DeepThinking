package com.deepthinking.rest;

import com.deepthinking.ext.base.PageResult;
import com.deepthinking.ext.base.Result;
import com.deepthinking.mysql.entity.ConceptDelay;
import com.deepthinking.service.ConceptDelayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dtflys.forest.backend.ContentType.APPLICATION_JSON;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "concept", produces = APPLICATION_JSON)
public class ConceptRest {

    private final ConceptDelayService conceptDelayService;



    /**
     * 查询每日排名前10的板块，竖型列表，第一行是表头信息
     */
    @GetMapping("list")
    public Result<List<ConceptDelay>> conceptList() {
        int days = 10, top = 10;
        List<List<ConceptDelay>> grid = conceptDelayService.queryConceptTradeList(days, top);
        return PageResult.success(grid);
    }


}
