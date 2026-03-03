package com.deepthinking.mysql.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 超类实体用于分页排序等属性
 **/
@Data
@ApiModel(value = "BaseEntity", description = "基础信息")
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";


    @TableField(CREATE_TIME)
    @JSONField(format="yyyy-MM-dd HH:mm:ss", ordinal = 98)
    @ApiModelProperty(value = "创建时间", name = "createTime", example = "2024-01-01 12:00:00", position = 98)
    protected LocalDateTime createTime;

    @TableField(UPDATE_TIME)
    @JSONField(format="yyyy-MM-dd HH:mm:ss", ordinal = 99)
    @ApiModelProperty(value = "修改时间", name = "updateTime", example = "2024-01-01 12:00:00", position = 99)
    protected LocalDateTime updateTime;


    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "排序字段", example = "name1,name2", hidden = true)
    protected String orderColumn;

    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "排序方向，默认ASC", example = "1,1", hidden = true)
    protected String isAsc;


    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "创建开始时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String createTimeStart;

    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "创建结束时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String createTimeEnd;

    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "更新开始时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String updateTimeStart;

    @TableField(exist = false, select = false)
    @JSONField(serialize = false)
    @ApiModelProperty(value = "更新结束时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String updateTimeEnd;

}
