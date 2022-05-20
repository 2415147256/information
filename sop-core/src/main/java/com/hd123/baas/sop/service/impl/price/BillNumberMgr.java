package com.hd123.baas.sop.service.impl.price;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.offset.Offset;
import com.hd123.baas.sop.service.api.offset.OffsetService;
import com.hd123.baas.sop.service.api.offset.OffsetType;

/**
 * @author zhengzewang on 2020/11/22.
 */
@Service
public class BillNumberMgr {

  @Autowired
  OffsetService offsetService;

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd");

  /**
   * 9 + 年月日 + 四位顺序
   * <p>
   * 年是两位
   *
   * @param tenant
   * @return
   */
  @Tx
  public String generatePriceAdjustmentFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.PRICE_ADJUSTMENT, '9');
  }

  /**
   * 1 + 年月日 + 四位顺序
   * <p>
   * 年是两位
   *
   * @param tenant
   * @return
   */
  @Tx
  public String generateGradeAdjustmentFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.GRADE_ADJUSTMENT, '1');
  }

  /**
   * 2 + 年月日 + 四位顺序
   * <p>
   * 年是两位
   *
   * @param tenant
   * @return
   */
  @Tx
  public String generatePricePromotionFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.PRICE_PROMOTION, '2');
  }

  @Tx
  public String generateTempPriceAdjustmentFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.TEMP_PRICE_ADJUSTMENT, '3');
  }

  /**
   * 6 + 年月日 + 四位顺序
   * <p>
   * 年是两位
   */
  @Tx
  public String generateH6TaskFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.H6TASK, '6');
  }

  @Tx
  public String generateTaskGroupCode(String tenant) {
    return generateWithLock(tenant, OffsetType.TASK_GROUP, '7');
  }

  @Tx
  public String generateTaskPlanCode(String tenant) {
    return generateWithLock(tenant, OffsetType.TASK_PLAN, '8');
  }

  @Tx
  public String generateAssignableTaskPlanCode(String tenant) {
    return generateWithLock(tenant, OffsetType.ASSIGNABLE_TASK_PLAN, '4');
  }

  @Tx
  public String generateExplosivePlanFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.EXPLOSIVE_PLAN, '1', 3);
  }

  @Tx
  public String generateExplosiveFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.EXPLOSIVE, '2', 3);
  }
  
  /**
   * 注意pre的唯一性，否则会重复
   *
   * @param tenant
   * @param type
   * @param pre
   * @return
   */
  private String generateWithLock(String tenant, OffsetType type, char pre) {
    return generateWithLock(tenant, type, pre, 4);
  }

  private String generateWithLock(String tenant, OffsetType type, char pre, int length) {
    String result;
    Offset offset = offsetService.getWithLock(tenant, type);
    String prefix = pre + FORMAT.format(new Date());

    String seq = "";
    StringBuilder sb = new StringBuilder();
    if (offset == null || !offset.getSeq().toString().startsWith(prefix)) {
      for (int i = 0; i < length - 1; i++) {
        sb.append("0");
      }
      sb.append("1");
      seq = sb.toString();
    } else {
      String num = offset.getSeq().toString().substring(7);
      for (int i = 0; i < length; i++) {
        sb.append("0");
      }
      seq = sb.toString() + (Integer.valueOf(num) + 1);
      seq = seq.substring(seq.length() - length, seq.length());
    }

    result = prefix + seq;
    offsetService.save(tenant, type, Long.valueOf(result));
    return result;
  }

}
