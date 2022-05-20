package com.hd123.baas.sop.service.api.skumgr;

import com.hd123.baas.sop.service.api.basedata.sku.SkuFilter;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
public class DirectorySkuFilter extends SkuFilter {
  private Boolean directorySelect;
}
