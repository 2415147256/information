package com.hd123.baas.sop.remote.rsmas.groupTag;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RsSKUGroupTagItem
 * @Description:
 * @Author zhanzhijie
 * @Date 2022/4/18
 * @Version
 **/
@Getter
@Setter
@ApiModel("批量更新商品分组标签请求")
public class RsSKUGroupTagItem implements Serializable {

  private static final long serialVersionUID = 16L;

  @ApiModelProperty(value = "批量更新商品分组标签请求")
  private List<RsSKUGroupTagLine> items = new ArrayList<>();
}
