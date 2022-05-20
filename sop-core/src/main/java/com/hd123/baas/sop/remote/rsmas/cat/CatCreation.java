package com.hd123.baas.sop.remote.rsmas.cat;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel("目录新建对象")
public class CatCreation extends CatMod {
  @ApiModelProperty("ID")
  private String id;
  @ApiModelProperty("代码")
  private String code;
  @ApiModelProperty("所属门店ID")
  private String ownerId;
  @ApiModelProperty("所属门店名称")
  private String ownerName;
  @ApiModelProperty("创建人ID")
  private String operatorId;

  public CatCreation() {
  }

  public String getId() {
    return this.id;
  }

  public String getCode() {
    return this.code;
  }

  public String getOwnerId() {
    return this.ownerId;
  }

  public String getOwnerName() {
    return this.ownerName;
  }

  public String getOperatorId() {
    return this.operatorId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public void setOperatorId(String operatorId) {
    this.operatorId = operatorId;
  }
}