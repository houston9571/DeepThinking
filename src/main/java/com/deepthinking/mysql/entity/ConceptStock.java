package com.deepthinking.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.deepthinking.common.constant.Constants;
import lombok.*;

/**
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("concept_stock")
public class ConceptStock extends BaseEntity {

    @TableId(value = Constants.ID, type = IdType.AUTO)
    private Integer id;

    private String stockCode;

    private String conceptCode;

}
