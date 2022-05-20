package com.hd123.baas.sop.service.api.skutag;

import java.util.List;

import com.hd123.rumba.commons.biz.entity.StandardEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class SkuShopTag extends StandardEntity {
  private String tenant;
  private String orgId;
  private String skuId;
  private String shop;
  private String shopCode;
  private String shopName;
  // 标签ID json集合
  private List<Tag> tags;
}
