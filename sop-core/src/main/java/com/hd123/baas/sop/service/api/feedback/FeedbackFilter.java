package com.hd123.baas.sop.service.api.feedback;

import com.hd123.baas.sop.service.api.basedata.Filter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 质量反馈单定义
 *
 * @author yu lilin on 2020/11/13
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "质量反馈单定义")
public class FeedbackFilter extends Filter {
  private static final long serialVersionUID = -3626193980147908764L;

  //查询条件定义
  public static final String CONDITION_KEYWORD_LIKE = "keyword:%=%";
  public static final String CONDITION_TYPENAME_LIKE = "typeName:%=%";
  public static final String CONDITION_SHOPNAME_LIKE = "shopName:%=%";
  public static final String CONDITION_TYPE_EQ = "type:=";
  public static final String CONDITION_APPLYREASON_EQ = "applyReason:=";
  public static final String CONDITION_APPLYREASON_LIKE = "applyReason:%=%";
  public static final String CONDITION_RESULT_EQ = "result:=";
  public static final String CONDITION_STATE_EQ = "state:=";
  public static final String CONDITION_DELIVERYTIME_BETWEEN = "deliveryTime:[,]";
  public static final String CONDITION_AUDITTIME_BETWEEN = "auditTime:[,]";
  public static final String CONDITION_FEEDBACKMARKED_EQ = "keyword:%=%";
  public static final String CONDITION_RECEIPTNUM_EQ = "keyword:%=%";
  public static final String CONDITION_GOODSCODE_EQ = "goodsCode:=";
  public static final String CONDITION_GOODSCODE_IN = "goodsCode:in";
  public static final String CONDITION_DELIVERYTIME_IN = "deliveryTime:in";
  public static final String CHANNEL_EQ = "channel:=";
  public static final String SP_NO = "spNo:=";
  @ApiModelProperty(value = "组织等于", required = false)
  private String orgIdEq;
  @ApiModelProperty(value = "组织范围in", required = false)
  private List<String> orgIdIn;
  @ApiModelProperty(value = "关键词类似", required = false)
  private String keywordLike;
  @ApiModelProperty(value = "所属类别名称类似", required = false)
  private String typeNameLike;
  @ApiModelProperty(value = "申请门店名称类似", required = false)
  private String shopNameLike;
  @ApiModelProperty(value = "申请类型等于", required = false)
  private FeedbackType typeEq;
  @ApiModelProperty(value = "申请原因等于", required = false)
  private String applyReasonEq;
  @ApiModelProperty(value = "申请原因LIKE", required = false)
  private String applyReasonLike;
  @ApiModelProperty(value = "处理结果等于", required = false)
  private FeedbackResult resultEq;
  @ApiModelProperty(value = "状态等于", required = false)
  private FeedbackState stateEq;
  @ApiModelProperty(value = "到货日期介于", required = false)
  private List<Date> deliveryTimeIn;
  @ApiModelProperty(value = "处理日期介于", required = false)
  private List<Date> auditTimeIn;
  @ApiModelProperty(value = "是否标记质量反馈", required = false)
  private Boolean feedbackMarkedEq;
  @ApiModelProperty(value = "收货单单号等于", required = false)
  private String receiptNumEq;
  @ApiModelProperty(value = "商品代码，输入码在...之中", required = false)
  private List<String> goodsCodeIn;
  @ApiModelProperty(value = "商品代码,输入码等于", required = false)
  private String goodsCodeEq;
  @ApiModelProperty(value = "到货日期在...范围内", required = false)
  private List<Date> deliveryTimeRealIn = new ArrayList<>();
  @ApiModelProperty(value = "渠道等于", required = false)
  private String channelEq;
  @ApiModelProperty(value = "审批单号等于", required = false)
  private String spNoEq;
}
