package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 板块基本资料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("concept_info")
public class ConceptInfo extends BaseEntity {

    @TableId(value = "concept_code", type = IdType.INPUT)
    private String conceptCode;

    private String conceptName;

    private String type;

    private String level;



}
