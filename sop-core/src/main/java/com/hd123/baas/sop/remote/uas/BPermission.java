package com.hd123.baas.sop.remote.uas;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author W.J.H.7
 * @since 1.1.0
 */
@Getter
@Setter
public class BPermission {
  @ApiModelProperty(value = "权限的uuid", example = "SAS:ORDER_QUERY")
  private String uuid;
  @ApiModelProperty(value = "应用程序id", required = true, example = "sas")
  private String appId;
  @ApiModelProperty(value = "序号", example = "1")
  private int sequence = 0;
  @ApiModelProperty(value = "分组Id", example = "应用程序解析，比如100100、订单管理，应用程序可用来做多级分组显示等")
  private String groupId;

  public static void main(String[] args) {

    // List<BPermission> list = new ArrayList<>();
    // list.add(test1("SAS:ORDER_QUERY", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_CREATE_BY_OTHERS", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_GET", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_PAY", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_AGREE", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_REFUSE", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_REPURCHASE", "SAS_ORDER"));
    // list.add(test1("SAS:ORDER_FINISH", "SAS_ORDER"));
    //
    // list.add(test1("SAS:RTN_QUERY", "SAS_RTN"));
    // list.add(test1("SAS:RTN_GET", "SAS_RTN"));
    // list.add(test1("SAS:RTN_AGREE", "SAS_RTN"));
    // list.add(test1("SAS:RTN_REFUSE", "SAS_RTN"));
    // list.add(test1("SAS:DIFF_QUERY", "SAS_RTN"));
    // list.add(test1("SAS:DIFF_GET", "SAS_RTN"));
    // list.add(test1("SAS:DIFF_AGREE", "SAS_RTN"));
    // list.add(test1("SAS:DIFF_REFUSE", "SAS_RTN"));
    //
    // list.add(test1("MAS:SKU_QUERY", "MAS_SKU"));
    // list.add(test1("MAS:SKU_GET", "MAS_SKU"));
    // list.add(test1("MAS:SKU_SAVE_MODIFY", "MAS_SKU"));
    //
    // list.add(test1("GPAS:CUSTOMER_QUERY", "GPAS_CUSTOMER"));
    // list.add(test1("GPAS:CUSTOMER_USER_*", "GPAS_CUSTOMER"));
    // list.add(test1("GPAS:CUSTOMER_ADDRESS_*", "GPAS_CUSTOMER"));
    //
    // list.add(test1("AAS:ACCOUNT_QUERY", "AAS_ACCOUNT"));
    // list.add(test1("AAS:ACCOUNT_RECHARGE", "AAS_ACCOUNT"));
    // list.add(test1("AAS:ACCOUNT_FLOW_QUERY", "AAS_ACCOUNT"));
    // list.add(test1("AAS:ACCOUNT_ENABLE", "AAS_ACCOUNT"));
    // list.add(test1("AAS:ACCOUNT_DISABLE", "AAS_ACCOUNT"));
    //
    // list.add(test1("MAS.ACTIVITYLIST.VIEW", "CMS_ACTIVITYLIST"));
    // list.add(test1("MAS.ACTIVITYLIST.MAINTAIN", "CMS_ACTIVITYLIST"));
    //
    // list.add(test1("MAS.DELIVERYCREATE.VIEW", "CMS_DELIVERYCREATE"));
    // list.add(test1("MAS.DELIVERYCREATE.MAINTAIN", "CMS_DELIVERYCREATE"));
    //
    // list.add(test1("MAS.DELIVERYLIST.VIEW", "CMS_DELIVERYLIST"));
    // list.add(test1("MAS.DELIVERYLIST.MAINTAIN", "CMS_DELIVERYLIST"));
    //
    // list.add(test1("UAS:USER_QUERY", "UAS_USER"));
    // list.add(test1("UAS:USER_SAVE_NEW", "UAS_USER"));
    // list.add(test1("UAS:USER_SAVE_MODIFY", "UAS_USER"));
    // list.add(test1("UAS:USER_RESET_PWD", "UAS_USER"));
    //
    // list.add(test1("UAS:ROLE_QUERY", "UAS_ROLE", false));
    // list.add(test1("UAS:ROLE_GET", "UAS_ROLE", false));
    // list.add(test1("UAS:ROLE_SAVE_NEW", "UAS_ROLE", false));
    // list.add(test1("UAS:ROLE_SAVE_MODIFY", "UAS_ROLE", false));
    // list.add(test1("UAS:ROLE_ADD_OR_REMOVE_USER", "UAS_ROLE", false));
    // list.add(test1("UAS:ROLE_QUERY_USER", "UAS_ROLE", false));
    //
    // System.out.println(JSON.toJSONString(list));

  }

}
