package com.hd123.baas.sop.service.api.promotion;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("促销字段控制")
public class PromFieldControl {
  private String field;
  private boolean readonly;
  private String defaultValue;
}
