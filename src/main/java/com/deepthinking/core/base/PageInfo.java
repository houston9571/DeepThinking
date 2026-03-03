package com.deepthinking.core.base;

import com.github.pagehelper.PageHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import static com.deepthinking.common.constant.Constants.MAX_PAGE_SIZE;

@Data
@Builder
@ApiModel(value = "PageInfo", description = "分页请求对象")
public class PageInfo<T> {

    @ApiModelProperty(value = "页码", example = "1")
    private int pageIndex;

    @ApiModelProperty(value = "每页条数", example = "10")
    private int pageSize;

    @ApiModelProperty(value = "请求参数", name = "data")
    private T data;

    public void startPage() {
        if(getPageSize() > 0 && getPageSize() <= MAX_PAGE_SIZE) {
            PageHelper.startPage(getPageIndex() > 0 ? getPageIndex() : 1, getPageSize());
        }
    }
}
