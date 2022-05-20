package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录更新对象")
public class CatMod implements Serializable {
  private static final long serialVersionUID = 1L;
  @ApiModelProperty("名称")
  private String name;
  @ApiModelProperty("简称")
  private String title;
  @ApiModelProperty("所属门店ID")
  private String ownerId;
  @ApiModelProperty("所属门店名称")
  private String ownerName;

  public CatMod() {
  }

  public String getName() {
    return this.name;
  }

  public String getTitle() {
    return this.title;
  }

  public String getOwnerId() {
    return this.ownerId;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }
}
