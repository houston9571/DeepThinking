package com.optimus.mongo;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CREATE_TIME = "createTime";

    public static final String UPDATE_TIME = "updateTime";

    @CreatedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss", ordinal = 98)
    @ApiModelProperty(value = "创建时间", name = CREATE_TIME, example = "2024-01-01 12:00:00", position = 98)
    protected LocalDateTime createTime = LocalDateTime.now();

    @LastModifiedDate
    @JSONField(format="yyyy-MM-dd HH:mm:ss", ordinal = 98)
    @ApiModelProperty(value = "修改时间", name = UPDATE_TIME, example = "2024-01-01 12:00:00", position = 99)
    protected LocalDateTime updateTime;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "创建开始时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String createTimeStart;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "创建结束时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String createTimeEnd;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "修改开始时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String updateTimeStart;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "修改结束时间", example = "2024-01-01 12:00:00", hidden = true)
    protected String updateTimeEnd;


    @JSONField(serialize = false)
    @ApiModelProperty(value = "排序字段", example = "name1,name2", hidden = true)
    protected String orderColumn;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "排序方向，默认ASC", example = "1,1", hidden = true)
    protected String isAsc;

}
