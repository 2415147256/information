package com.hd123.baas.sop.config;

import com.hd123.baas.sop.utils.JsonUtil;
import com.qianfan123.baas.config.api.annotation.BcGroup;
import com.qianfan123.baas.config.api.annotation.BcKey;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author W.J.H.7
 */
@Getter
@Setter
@BcGroup(name = "h6skuBomDefine")
public class H6SkuBomDefineConfig {

  @BcKey(name = "商品默认原料关系，[{\"finish\":\"1\",\"raw\":\"2\"}]", editor = "JSON")
  private String bomDefine;

  public Map<String, String> getRawMap() {
    if(StringUtils.isNotEmpty(bomDefine)) {
      List<N> list = JsonUtil.jsonToList(bomDefine, N.class);
      if (CollectionUtils.isNotEmpty(list)) {
        return list.stream().collect(Collectors.toMap(N::getFinish, N::getRaw));
      }
    }
    return new HashMap<>();
  }

  @Getter
  @Setter
  public static class N {
    private String finish;
    private String raw;
  }
}
