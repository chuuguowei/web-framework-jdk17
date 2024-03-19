package org.example.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页结果返回格式的数据定义
 */
@Data
@ApiModel(description = "通用分页请求体对象")
public class PageReq {

    @ApiModelProperty(value = "第几页", position = 99)
    private Integer pageNo;

    @ApiModelProperty(value = "分页大小", position = 100)
    private Integer pageSize;

    @ApiModelProperty(value = "最大分页大小", position = 101)
    private Integer maxPageSize = 1000;

    public PageReq() {

    }

    public PageReq(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) {
            pageSize = 20;
        }
        if (pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }
        return pageSize;
    }
}