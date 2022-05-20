package com.hd123.baas.sop.service.impl.sku.publishplan;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.offset.Offset;
import com.hd123.baas.sop.service.api.offset.OffsetService;
import com.hd123.baas.sop.service.api.offset.OffsetType;

/**
 * @author liuhaoxin on 2020/11/22.
 */
@Service
public class SkuBillNumberMgr {

  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyMMdd");

  @Autowired
  OffsetService offsetService;

  /**
   * 商品上下架方案单号生成 三位顺序
   *
   * @param tenant
   *          租户
   * @return String 上下架单号
   */
  @Tx
  public String generateSkuPublishPlan(String tenant) {
    return generateWithLock(tenant, OffsetType.SKU_PUBLISH_PLAN);
  }

  /**
   * 生成单号
   * 
   * @param tenant
   *          租户
   * @param type
   *          订单号类型
   * @return String 订单号
   */
  private String generateWithLock(String tenant, OffsetType type) {
    String result;
    Offset offset = offsetService.getWithLock(tenant, type);
    String prefix = FORMAT.format(new Date());

    String seq;
    if (offset == null || !offset.getSeq().toString().startsWith(prefix)) {
      seq = "001";
    } else {
      String num = offset.getSeq().toString().substring(7);
      seq = "000" + (Integer.parseInt(num) + 1);
      seq = seq.substring(seq.length() - 3);
    }

    result = prefix + seq;
    offsetService.save(tenant, type, Long.valueOf(result));
    return result;
  }

}
