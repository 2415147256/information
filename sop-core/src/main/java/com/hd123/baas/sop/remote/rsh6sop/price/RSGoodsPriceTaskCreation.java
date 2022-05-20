/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： GoodsPriceTaskPublisher.java
 * 模块说明：
 * 修改历史：
 * 2020年11月14日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Data
@ApiModel(description = "商品价格任务")
public class RSGoodsPriceTaskCreation implements Serializable {
    private static final long serialVersionUID = 7995895042961906241L;

    @NotBlank
    @Length(max = 38)
    @ApiModelProperty(value = "任务标识，标识唯一一次任务", example = "03b1617053db4619b693842debfb32c5", required = true)
    private String taskId;
    @NotNull
    @ApiModelProperty(value = "任务类型：price-价格（到店价、促销到店价、售价）下发任务，prom-促销数据下发任务", example = "price", required = true)
    private GoodsPriceTaskType type;
    @NotBlank
    @Length(max = 512)
    @ApiModelProperty(value = "下发文件下载地址", example = "https://www.baidu.com/priceTask.zip", required = true)
    private String fileUrl;
    @NotNull
    @ApiModelProperty(value = "任务生效时间", example = "2020-11-14 10:00:00", required = true)
    private Date occurredTime;
}
