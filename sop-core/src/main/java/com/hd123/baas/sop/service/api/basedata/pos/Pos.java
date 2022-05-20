package com.hd123.baas.sop.service.api.basedata.pos;

import com.hd123.baas.sop.service.api.basedata.stall.Stall;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Silent
 **/
@Getter
@Setter
@ApiModel(description = "收银机")
public class Pos {
  public static final String PART_PROMDATA_DOWNLOAD_STATE = "promDataDownloadState";
  public static final String PART_STALL = "stalls";


  @ApiModelProperty(value = "组织类型")
  private String orgType;
  @ApiModelProperty(value = "组织id")
  private String orgId;
  @ApiModelProperty(value = "门店")
  public UCN store;
  @ApiModelProperty(value = "ID")
  public String id;
  @ApiModelProperty(value = "代码")
  public String code;
  @ApiModelProperty(value = "名称")
  public String name;
  @ApiModelProperty(value = "起禁用状态")
  public boolean enabled;
  @ApiModelProperty(value = "促销数据下发任务")
  public PromDataDownloadTask promDataDownloadTask;
  @ApiModelProperty(value = "出品部门列表")
  public List<Stall> stalls;
  @ApiModelProperty(value = "仓位信息")
  public UCN warehouse;

  @ApiModelProperty(value = "是否默认", required = true)
  private Boolean isDefault = Boolean.FALSE;

  @ApiModelProperty(value = "收银机序列号")
  private String posSerialNum;

  @QueryEntity(Pos.class)
  public static class Queries extends QueryFactors.Entity {

    private static final String PREFIX = Pos.class.getName() + "::";
    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String ID = PREFIX + "id";
    @QueryField
    public static final String CODE = PREFIX + "code";
    @QueryField
    public static final String NAME = PREFIX + "name";
  }
}
