package com.hd123.baas.sop.service.api.group;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.entity.SkuGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Getter
@Setter
public class SkuGroupCategory {
  private SkuGroup skuGroup;
  private Category category;
}
