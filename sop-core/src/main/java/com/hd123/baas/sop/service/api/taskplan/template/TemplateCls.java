package com.hd123.baas.sop.service.api.taskplan.template;

/**
 * @author zhengzewang on 2020/11/3.
 */
public enum TemplateCls {

  RECEIVE_CHECK(ReceiveCheckTemplateClsTaskPlan.class), // 当日收货检查
  POS_DAY_END(PosDayEndTemplateClsTaskPlan.class), // 收银机日结
  TURNOVER_BOX_CHECK(TurnoverBoxCheckTemplateClsTaskPlan.class), // 周转箱登记
  TRANSFER_ORDER_CHECK(TransferOrderCheckTemplateClsTaskPlan.class), // 调拨单确认检查
  CHECK_SKU_PRICE(CheckSkuPriceTemplateClsTaskPlan.class), // 检查商品主档已下发价格已传秤
  MKH_ORDER_CHECK_ALL(MKHOrderCheckAllTemplateClsTaskPlan.class),// 核销所有订单
  SHOP_ATTENDANCE_CHECK(ShopAttendanceCheckTaskPlan.class);// 门店考勤情况确认

  private Class<? extends TemplateClsTaskPlan> templateClsTaskPlanClass;

  TemplateCls(Class<? extends TemplateClsTaskPlan> templateClsTaskPlanClass) {
    this.templateClsTaskPlanClass = templateClsTaskPlanClass;
  }

  public static TemplateClsTaskPlan getTemplateClsTaskPlanByTem(String name)
      throws IllegalAccessException, InstantiationException {
    TemplateCls[] templateClss = values();
    for (TemplateCls templateCls : templateClss) {
      if (name.equals(templateCls.name())) {
        return templateCls.templateClsTaskPlanClass.newInstance();
      }
    }
    return null;
  }

}
