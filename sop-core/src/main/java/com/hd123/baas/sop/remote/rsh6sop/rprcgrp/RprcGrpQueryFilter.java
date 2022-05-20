/**
 * 版权所有(C)，上海海鼎有限公司，2021，所有权利保留。
 * <p>
 * 项目名： h6-sop
 * 文件名： RprcGrpQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2021年01月06日 - liuguilin - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.rprcgrp;

import com.hd123.baas.sop.remote.rsmas.RsFilter;

import com.hd123.baas.sop.remote.rsmas.RsSort;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author huanghanyao
 */
@ApiModel(description = "价格组查询过滤器")
@Getter
@Setter
public class RprcGrpQueryFilter implements Serializable {
    private static final long serialVersionUID = 8043481667970304359L;

    // 排序条件定义
    public static final String ORDER_BY_CODE = "code";
    public static final String ORDER_BY_NAME = "name";

    @ApiModelProperty(value = "代码等于", required = false)
    private String codeEquals;
    @ApiModelProperty(value = "代码类似于", required = false)
    private String codeLike;
    @ApiModelProperty(value = "名称等于", required = false)
    private String nameEquals;
    @ApiModelProperty(value = "名称类似于", required = false)
    private String nameLike;
    @ApiModelProperty(value = "门店标识等于", example = "1000000", required = false)
    private String storeUuidEquals;
    @ApiModelProperty(value = "所属组织标识等于", example = "1000000", required = false)
    private String orgUuidEquals;
    @ApiModelProperty(value = "所属组织标识在...范围", example = "1000000", required = false)
    private List<String> orgUuidIn;

    @ApiModelProperty(value = "页号")
    private int page;
    @ApiModelProperty(value = "页记录数")
    private int pageSize;
    @ApiModelProperty(value = "排序字段集合")
    private List<RprcGrpQuerySort> sorts;
}
