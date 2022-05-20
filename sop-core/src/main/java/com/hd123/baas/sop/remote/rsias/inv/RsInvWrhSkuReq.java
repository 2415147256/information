package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 渠道履约方库存查询请求
 *
 * @author liyan
 * @date 2021/8/3
 */
@Getter
@Setter
public class RsInvWrhSkuReq {
  /** 默认渠道 */
  public static final String DEF_APP_ID = "*";

  /** 渠道 */
  private String appId = DEF_APP_ID;
  /** 履约方ID */
  private String wrhId;
  /** 商品ID列表 */
  private List<String> skuIds;

  public RsInvWrhSkuReq() {
  }

  public RsInvWrhSkuReq(String wrhId, List<String> skuIds) {
    this.wrhId = wrhId;
    this.skuIds = skuIds;
  }

  public RsInvWrhSkuReq(String appId, String wrhId, List<String> skuIds) {
    this.appId = appId;
    this.wrhId = wrhId;
    this.skuIds = skuIds;
  }
}
