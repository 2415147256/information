package com.hd123.baas.sop.remote.rsmas.cat;

import java.util.ArrayList;
import java.util.List;

import com.hd123.rumba.commons.biz.entity.StandardEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录商品特征")
public class CatSKUFeat extends StandardEntity {
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("候选值")
  private List<String> candidates = new ArrayList();

  public CatSKUFeat() {
  }

  public String getName() {
    return this.name;
  }

  public List<String> getCandidates() {
    return this.candidates;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCandidates(List<String> candidates) {
    this.candidates = candidates;
  }
}
