/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * <p>
 * 项目名：	mas-commons-api
 * 文件名：	TaskFilter.java
 * 模块说明：
 * 修改历史：
 * <p>
 * 2019年10月9日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.task;

import com.hd123.baas.sop.remote.rsmas.RsMasFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author lsz
 */
@Getter
@Setter
@ApiModel
public class RsTaskFilter extends RsMasFilter {
  private static final long serialVersionUID = -436633849172681740L;

  @ApiModelProperty(value = "组织类型等于...")
  private String orgTypeEq;
  @ApiModelProperty(value = "组织ID等于")
  private String orgIdEq;
  @ApiModelProperty(value = "ID等于")
  private String idEq;
  @ApiModelProperty(value = "ID类似于")
  private String idLike;
  @ApiModelProperty(value = "分组等于")
  private String groupIdEq;
  @ApiModelProperty(value = "类型等于")
  private String typeEq;
  @ApiModelProperty(value = "类型在...范围")
  private List<String> typeIn;
  @ApiModelProperty(value = "名称类似于")
  private String nameLike;
  @ApiModelProperty(value = "创建时间起始于")
  private Date createGt;
  @ApiModelProperty(value = "创建时间截至于")
  private Date createLt;
  @ApiModelProperty(value = "结束时间起始于")
  private Date endTimeGt;
  @ApiModelProperty(value = "结束时间截至于")
  private Date endTimeLt;
  @ApiModelProperty(value = "创建人类似于")
  private String creatorLike;
  @ApiModelProperty(value = "创建人等于")
  private String creatorEq;
  @ApiModelProperty(value = "状态等于")
  private RsTaskStatus statusEq;
  @ApiModelProperty(value = "状态在之中")
  private List<RsTaskStatus> statusIn;
  @ApiModelProperty(value = "周期类型等于")
  private RsTaskPeriodType periodTypeEq;


}
