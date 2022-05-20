package com.hd123.baas.sop.remote.rsmas.cat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("详情描述")
public class Description implements Serializable {
    private static final long serialVersionUID = -3254210282045847408L;
    @ApiModelProperty("排序")
    private int order;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("内容")
    private String content;

    public Description() {
    }

    public int getOrder() {
        return this.order;
    }

    public String getType() {
        return this.type;
    }

    public String getContent() {
        return this.content;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
