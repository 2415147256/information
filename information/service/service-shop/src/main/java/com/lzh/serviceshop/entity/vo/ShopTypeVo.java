package com.lzh.serviceshop.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lzh.commonutils.T;
import com.lzh.serviceshop.entity.ShopType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Data
public class ShopTypeVo {

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    @ApiModelProperty(value = "类别描述表")
    private String describe;

    @ApiModelProperty(value = "上级的id")
    private String parentId;

    private List<ShopTwoTypeVo> children;
}
