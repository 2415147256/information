package com.hd123.baas.sop.service.impl.faq;

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
public class FaqBillNumberMgr {

  @Autowired
  OffsetService offsetService;

  /**
   * 四位顺序
   *
   *
   * @param tenant
   * @return
   */
  @Tx
  public String generateCategoryFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.FAQ_CATEGORY);
  }

  @Tx
  public String generateArticleFlowNo(String tenant) {
    return generateWithLock(tenant, OffsetType.FAQ_ARTICLE);
  }

  /**
   * 注意pre的唯一性，否则会重复
   * 
   * @param tenant
   * @param type
   * @return
   */
  private String generateWithLock(String tenant, OffsetType type) {
    Offset offset = offsetService.getWithLock(tenant, type);
    String seq = "";
    if (offset == null) {
      seq = "1";
    } else {
      String num = offset.getSeq().toString();
      seq = "" + (Integer.valueOf(num) + 1);
    }
    offsetService.save(tenant, OffsetType.FAQ_CATEGORY, Long.valueOf(seq));
    offsetService.save(tenant, type, Long.valueOf(seq));
    return seq;
  }

}
