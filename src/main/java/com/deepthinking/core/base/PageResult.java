package com.deepthinking.core.base;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.annotation.JSONField;
import com.deepthinking.common.enums.ErrorCode;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.Transient;
import java.util.List;

import static com.deepthinking.common.constant.Constants.OK;

@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel(value = "PageResult", description = "分页查询结果")
public class PageResult<T> extends Result<T> {

    @JSONField(ordinal = 3)
    @ApiModelProperty("页码")
    private int pageIndex;

    @JSONField(ordinal = 4)
    @ApiModelProperty("每页条数")
    private int pageSize;

    @JSONField(ordinal = 5)
    @ApiModelProperty("总条数")
    private long count;

    @JSONField(ordinal = 6)
    @ApiModelProperty("总页数")
    private int pages;

    @JSONField(name = "totals", ordinal = 9)
    private List<? extends T> totals;

    @ApiModelProperty(hidden = true)
    @JSONField(name = "data", ordinal = 10)
    private List<? extends T> list;


    @Transient
    public boolean hasData() {
        return isSuccess() && CollectionUtil.isNotEmpty(list);
    }

    private PageResult(int code, String msg, int pageIndex, int pageSize, long count, int pages, List<? extends T> data, List<? extends T> totals) {
        super();
        super.code = code;
        super.msg = msg;
        this.list = data;
        this.totals = totals;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.count = count;
        this.pages = pages;
    }

    public static <T> PageResult<T> success(List<? extends T> data, List<? extends T> totals) {
        PageInfo<T> pageInfo = new PageInfo<>(data);
        return new PageResult<>(0, OK, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList(), totals);
    }

    public static <T> PageResult<T> success(List<? extends T> data) {
        PageInfo<T> pageInfo = new PageInfo<>(data);
        return new PageResult<>(0, OK, pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList(), Lists.newArrayList());
    }

    public static <T> PageResult<T> success(int pageIndex, int pageSize, long count, List<? extends T> data) {
        int pages = pageSize == 0 ? 0 : Long.valueOf(count % pageSize > 0 ? count / pageSize + 1 : count / pageSize).intValue();
        return new PageResult<>(0, OK, pageIndex, pageSize, count, pages, data, Lists.newArrayList());
    }

    public static <T> PageResult<T> fail(ErrorCode code, Object... args) {
        String msg = args != null ? String.format(code.getMsg(), args) : code.getMsg();
        return new PageResult<>(code.getCode(), msg, 0, 0, 0, 0, null, null);
    }
}
