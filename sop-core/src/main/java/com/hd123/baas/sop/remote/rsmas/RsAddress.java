/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * 
 * 项目名：	mas-company-api
 * 文件名：	RsAddress.java
 * 模块说明：	
 * 修改历史：
 * 2019年9月3日 - sulin - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author sulin
 *
 */
@Getter
@Setter
@ApiModel("地址")
public class RsAddress {
  @ApiModelProperty("国家")
  private String country;
  @ApiModelProperty("省份id")
  private String provinceId;
  @ApiModelProperty("身份名称")
  private String provinceName;
  @ApiModelProperty("城市id")
  private String cityId;
  @ApiModelProperty("城市名称")
  private String cityName;
  @ApiModelProperty("地区id")
  private String districtId;
  @ApiModelProperty("地区名称")
  private String districtName;
  @ApiModelProperty("街道id")
  private String streetId;
  @ApiModelProperty("街道名称")
  private String streetName;
  @ApiModelProperty("详细地址")
  private String detailAddress;
  @ApiModelProperty("邮编")
  private String postcode;
  @ApiModelProperty("经度")
  private String longitude;
  @ApiModelProperty("纬度")
  private String latitude;
}
