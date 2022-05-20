/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsOrderQueryFilter.java
 * 模块说明：
 * 修改历史：
 * 2020年11月14日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.price;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "商品订货查询过滤器")
public class RSGoodsOrderQueryFilter implements Serializable {
    private static final long serialVersionUID = -1452260175698908523L;

    @ApiModelProperty(value = "商品标识in", required = true)
    private List<String> gdUuidsIn;
    @ApiModelProperty(value = "开始时间", example = "2020-11-14 00:00:00", required = false)
    private Date beginTimeGreaterOrEquals;
    @ApiModelProperty(value = "结束时间", example = "2020-11-21 00:00:00", required = false)
    private Date endTimeLessOrEquals;
    @ApiModelProperty(value = "组织id", example = "1000000", required = false)
    private String orgUuidEquals;

    public Set<Integer> entityGids() {
        if (CollectionUtils.isEmpty(gdUuidsIn)) {
            return new HashSet<>();
        }
        Set<Integer> set = new HashSet<>();
        gdUuidsIn.stream().forEach(item -> {
            set.add(Integer.valueOf(item));
        });
        return set;
    }
}
