package org.example.model;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页结果返回格式的数据定义
 */
@Data
@Builder
@ApiModel(description = "通用分页结果响应返回对象")
public class Page<T>  {

    private Integer pageNo;

    private Integer pageSize;

    private Integer total;

    private List<T> dataList;

}