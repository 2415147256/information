package com.hd123.baas.sop.remote.rsmas.store;

import com.hd123.baas.sop.remote.rsmas.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lina
 */
@Getter
@Setter
public class RsStore extends RsMasEntity {
  private static final long serialVersionUID = 6082218113409958126L;

  @ApiModelProperty("组织类型")
  private String orgType;
  @ApiModelProperty(value = "所属组织ID")
  private String orgId;
  @ApiModelProperty(value = "代码", required = true)
  private String code;
  @ApiModelProperty(value = "名称", required = true)
  private String name;
  @ApiModelProperty(value = "别名")
  private String alias;
  @ApiModelProperty(value = "门店类型", required = true)
  private String type;
  @ApiModelProperty(value = "联系信息")
  private RsContact contact;
  @ApiModelProperty(value = "地址信息")
  private RsAddress address;
  @ApiModelProperty(value = "标签")
  private List<RsTag> tags = new ArrayList<>();

  @ApiModelProperty(value = "主图")
  private String image;
  @ApiModelProperty(value = "店头照")
  private String facadeImage;
  @ApiModelProperty(value = "二维码")
  private String QRCode;
  @ApiModelProperty(value = "副图")
  private List<String> images;
  @ApiModelProperty(value = "业态")
  private String shopBusinessType;

  @ApiModelProperty(value = "详情信息")
  private List<RsDescription> descriptions = new ArrayList<>();
  @ApiModelProperty(value = "营业证照")
  private List<RsDescription> licenses = new ArrayList<>();
  @ApiModelProperty(value = "商圈")
  private List<String> businessCircles;
  @ApiModelProperty(value = "等级")
  private String level;
  @ApiModelProperty(value = "营业时间")
  private String businessHour;
  @ApiModelProperty(value = "服务电话")
  private String servicePhoneNumber;
  @ApiModelProperty(value = "催单电话")
  private String remaindPhoneNumber;
  @ApiModelProperty(value = "客诉电话")
  private String complainPhoneNumber;
  @ApiModelProperty(value = "客诉服务时间")
  private String complainPhoneHour;

  @ApiModelProperty("门店配置")
  private Map<String, String> configs;
  @ApiModelProperty(value = "启禁用")
  private Boolean enabled = true;

  @ApiModelProperty(value = "区域")
  private String area;

  @ApiModelProperty("门店状态")
  private RsShopState state;
  @ApiModelProperty("门店营业状态")
  private RsShopBusinessState businessState;
  @ApiModelProperty("门店营业时间")
  private List<String> businessHours;
}
