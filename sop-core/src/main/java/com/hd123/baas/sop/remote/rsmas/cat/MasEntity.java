package com.hd123.baas.sop.remote.rsmas.cat;

import com.hd123.rumba.commons.biz.entity.StandardEntity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
@ApiModel
public class MasEntity extends StandardEntity {
  private static final long serialVersionUID = -5517914881723609872L;
  @ApiModelProperty("租户")
  private String tenant;
  @ApiModelProperty("ID")
  private String id;
  @ApiModelProperty("已删除")
  private Boolean deleted = false;
  @ApiModelProperty("traceId")
  private String traceId;

  public MasEntity() {
  }

  public String getTenant() {
    return this.tenant;
  }

  public String getId() {
    return this.id;
  }

  public Boolean getDeleted() {
    return this.deleted;
  }

  public String getTraceId() {
    return this.traceId;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }
}
