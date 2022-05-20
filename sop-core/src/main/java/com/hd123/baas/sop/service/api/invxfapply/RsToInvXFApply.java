/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	RsToInvXFApply.java
 * 模块说明：
 * 修改历史：
 * 2020/11/3 - Leo - 创建。
 */

package com.hd123.baas.sop.service.api.invxfapply;

import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApply;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyReqLine;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leo
 */
public class RsToInvXFApply implements Converter<RsInvXFApply, InvXFApply> {
  private RsToInvXFApply() {

  }

  private static RsToInvXFApply instance = new RsToInvXFApply();

  public static RsToInvXFApply getInstance() {
    return instance;
  }

  @Override public InvXFApply convert(RsInvXFApply source)
    throws ConversionException {
    if (source == null) {
      return null;
    }
    InvXFApply target = new InvXFApply();
    target.setNum(source.getSrcNum());
    target.setStat(source.getStat());
    //调入调出
    if (source.getInitiatorUuid().equals(source.getFromStoreUuid())) {
      target.setType(InvXFType.decrease);
    } else {
      target.setType(InvXFType.increase);
    }
    target.setOrgGid(source.getOrgGid());
    target.setFromStoreUuid(source.getFromStoreUuid());
    target.setFromStoreCode(source.getFromStoreCode());
    target.setFromStoreName(source.getFromStoreName());
    target.setToStoreUuid(source.getToStoreUuid());
    target.setToStoreCode(source.getToStoreCode());
    target.setToStoreName(source.getToStoreName());
    target.setInitiatorUuid(source.getInitiatorUuid());
    target.setInitiatorCode(source.getInitiatorCode());
    target.setInitiatorName(source.getInitiatorName());
    target.setTotal(source.getTotal());
    target.setTax(source.getTax());
    target.setReason(source.getReason());
    target.setNote(source.getNote());
    target.setFilDate(source.getFilDate());
    target.setFiller(source.getFiller());
    target.setLstupdTime(source.getLstupdTime());
    target.setLastModifyOper(source.getLastModifyOper());
    target.setRecCnt(source.getRecCnt());

    List<InvXFApplyLine> lines = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(source.getLines())) {
      for (RsInvXFApplyReqLine sourceLine : source.getLines()) {
        lines.add(convert(sourceLine));
      }
    }
    target.setLines(lines);

    return target;
  }

  public InvXFApplyLine convert(RsInvXFApplyReqLine source) {
    if (source == null) {
      return null;
    }
    InvXFApplyLine target = new InvXFApplyLine();
    target.setLine(source.getLine());
    target.setGdUuid(source.getGdUuid());
    target.setGdCode(source.getGdCode());
    target.setGdName(source.getGdName());
    target.setMunit(source.getMunit());
    target.setQpcStr(source.getQpcStr());
    target.setQpc(source.getQpc());
    target.setQty(source.getQty());
    target.setQtyStr(source.getQtyStr());
    target.setApproveQty(source.getApproveQty());
    target.setApproveQtyStr(source.getApproveQtyStr());
    target.setFromQty(source.getFromQty());
    target.setFromQtyStr(source.getFromQtyStr());
    target.setToQty(source.getToQty());
    target.setToQtyStr(source.getToQtyStr());
    target.setReason(source.getReason());
    target.setPrice(source.getPrice());
    target.setTotal(source.getTotal());
    target.setTax(source.getTax());
    target.setNote(source.getNote());

    return target;
  }

}
