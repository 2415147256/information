/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * 
 * 项目名：	sop-service
 * 文件名：	RsPlatformShop.java
 * 模块说明：	
 * 修改历史：
 * 2021年1月7日 - hezhenhui - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.platformshop;

import com.hd123.baas.sop.remote.rsmas.RsMasEntity;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.spms.commons.bean.IdName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hezhenhui
 *
 */
@Getter
@Setter
@ApiModel("平台门店")
public class RsPlatformShop extends RsMasEntity {
  private static final long serialVersionUID = 4010046835115592312L;

  @ApiModelProperty("来源组织ID")
  private String orgId;
  @ApiModelProperty("来源组织类型")
  private String orgType;
  @ApiModelProperty("平台")
  private IdName platform;
  @ApiModelProperty("平台商家")
  private IdName merchant;
  @ApiModelProperty("关联门店")
  private RsStore associateShop;
  @ApiModelProperty("平台门店代码, 对应平台上的门店id")
  private String code;
  @ApiModelProperty("平台门店名称")
  private String name;
  @ApiModelProperty("平台门店状态")
  private String state;
  @ApiModelProperty("平台门店营业状态")
  private String businessState;
  @ApiModelProperty("平台门店营业时间")
  private List<String> businessHour;
  @ApiModelProperty("平台门店配置")
  private Map<String, String> configs;
  @ApiModelProperty("是否启用")
  private Boolean enabled;
  @ApiModelProperty("平台门店休息起始时间")
  private Date restingTimeBegin;
  @ApiModelProperty("平台门店休息截止时间")
  private Date restingTimeEnd;
}
