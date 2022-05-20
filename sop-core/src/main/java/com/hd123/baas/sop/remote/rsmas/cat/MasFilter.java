package com.hd123.baas.sop.remote.rsmas.cat;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
public class MasFilter extends Filter {
  private static final long serialVersionUID = -8726379425152114879L;
  @ApiModelProperty("UUID等于")
  private String uuidEq;
  @ApiModelProperty("UUID在范围")
  private List<String> uuidIn;
  @ApiModelProperty("ID等于")
  private String idEq;
  @ApiModelProperty("是否已删除等于")
  private Boolean deletedEq;
  @ApiModelProperty("ID在...范围内")
  private List<String> idIn;
  @ApiModelProperty("创建时间起始于")
  private Date createdGt;
  @ApiModelProperty("创建时间截止于")
  private Date createdLt;
  @ApiModelProperty("最后修改时间起始于")
  private Date modifiedGt;
  @ApiModelProperty("最后修改时间截止于")
  private Date modifiedLt;

  public MasFilter() {
    this.deletedEq = Boolean.FALSE;
  }

  public String getUuidEq() {
    return this.uuidEq;
  }

  public List<String> getUuidIn() {
    return this.uuidIn;
  }

  public String getIdEq() {
    return this.idEq;
  }

  public Boolean getDeletedEq() {
    return this.deletedEq;
  }

  public List<String> getIdIn() {
    return this.idIn;
  }

  public Date getCreatedGt() {
    return this.createdGt;
  }

  public Date getCreatedLt() {
    return this.createdLt;
  }

  public Date getModifiedGt() {
    return this.modifiedGt;
  }

  public Date getModifiedLt() {
    return this.modifiedLt;
  }

  public void setUuidEq(String uuidEq) {
    this.uuidEq = uuidEq;
  }

  public void setUuidIn(List<String> uuidIn) {
    this.uuidIn = uuidIn;
  }

  public void setIdEq(String idEq) {
    this.idEq = idEq;
  }

  public void setDeletedEq(Boolean deletedEq) {
    this.deletedEq = deletedEq;
  }

  public void setIdIn(List<String> idIn) {
    this.idIn = idIn;
  }

  public void setCreatedGt(Date createdGt) {
    this.createdGt = createdGt;
  }

  public void setCreatedLt(Date createdLt) {
    this.createdLt = createdLt;
  }

  public void setModifiedGt(Date modifiedGt) {
    this.modifiedGt = modifiedGt;
  }

  public void setModifiedLt(Date modifiedLt) {
    this.modifiedLt = modifiedLt;
  }
}
