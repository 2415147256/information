package com.hd123.baas.sop.service.api.price.shopprice.bean;

import java.util.Date;

import com.hd123.baas.sop.service.api.TenantStandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Getter
@Setter
public class ShopPriceJob extends TenantStandardEntity {
  private String orgId;
  private String shop;
  private String shopCode;
  private String shopName;
  // 对应的h6Task的id。一个任务包含多个job（不同门店的）
  private String taskId;
  // 日期
  private Date executeDate;
  // 价格调整单
  private String priceAdjustment;
  private ShopPriceJobState state = ShopPriceJobState.CONFIRMED;
  // 错误内容
  private String errMsg;

  @QueryEntity(ShopPriceJob.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = ShopPriceJob.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String STATE = PREFIX + "state";
    @QueryField
    public static final String TASK_ID = PREFIX + "taskId";

  }

}
