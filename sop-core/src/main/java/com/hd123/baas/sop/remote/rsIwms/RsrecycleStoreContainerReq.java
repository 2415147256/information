package com.hd123.baas.sop.remote.rsIwms;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 1.0.0
 **/
@Getter
@Setter
public class RsrecycleStoreContainerReq extends RsIwmsBaseReq {
  @ApiModelProperty(value = "回收的容器类型信息,qty -→ 回收的数量, checkUrl -→ 拍照确认的地址", required = true)
  private List<Line> containerTypeReturnInfos;

  @Getter
  @Setter
  public static class Line {
    private RsContainerType containerType;
    private BigDecimal qty;
    private String checkUrl;
  }
}
