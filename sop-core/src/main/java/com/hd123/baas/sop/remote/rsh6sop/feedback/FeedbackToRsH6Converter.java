package com.hd123.baas.sop.remote.rsh6sop.feedback;

import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author maodapeng
 * @Since
 */
public class FeedbackToRsH6Converter implements Converter<Feedback, RsH6Feedback> {
  @Override
  public RsH6Feedback convert(Feedback feedback) throws ConversionException {
    RsH6Feedback rsH6Feedback = new RsH6Feedback();

    rsH6Feedback.setUuid(feedback.getUuid());
    rsH6Feedback.setBillId(feedback.getBillId());
    rsH6Feedback.setAppId(feedback.getAppId());
    rsH6Feedback.setTenant(feedback.getTenant());

    rsH6Feedback.setShop(feedback.getShop());
    rsH6Feedback.setShopNo(feedback.getShopNo());
    rsH6Feedback.setShopName(feedback.getShopName());

    rsH6Feedback.setGrade(new RsH6FeedbackGrade(feedback.getGradeId(),
        feedback.getGradeName()));

    rsH6Feedback.setReceiptNum(feedback.getReceiptNum());
    rsH6Feedback.setReceiptLineId(feedback.getReceiptLineId());
    rsH6Feedback.setReceiptQty(feedback.getReceiptQty());

    rsH6Feedback.setSinglePrice(feedback.getSinglePrice());

    rsH6Feedback.setGdUuid(feedback.getGdUuid());
    rsH6Feedback.setGdInputCode(feedback.getGdInputCode());
    rsH6Feedback.setGdCode(feedback.getGdCode());
    rsH6Feedback.setGdName(feedback.getGdName());
    rsH6Feedback.setGdTypeCode(feedback.getGdTypeCode());
    rsH6Feedback.setGdTypeName(feedback.getGdTypeName());

    rsH6Feedback.setMunit(feedback.getMunit());
    rsH6Feedback.setMinMunit(feedback.getMinMunit());


    rsH6Feedback.setType(feedback.getType().name());
    rsH6Feedback.setResult(feedback.getResult().name());
    rsH6Feedback.setState(feedback.getState().name());
    rsH6Feedback.setDeliveryTime(feedback.getDeliveryTime());

    rsH6Feedback.setTotal(feedback.getTotal());
    rsH6Feedback.setQty(feedback.getQty());
    rsH6Feedback.setQpc(feedback.getQpc());


    rsH6Feedback.setApplyReason(feedback.getApplyReason());
    rsH6Feedback.setApplyNote(feedback.getApplyNote());


    rsH6Feedback.setAuditReason(feedback.getAuditReason());
    rsH6Feedback.setAuditNote(feedback.getAuditNote());
    rsH6Feedback.setAuditTime(feedback.getAuditTime());
    rsH6Feedback.setAuditorId(feedback.getAuditorId());
    rsH6Feedback.setAuditorName(feedback.getAuditorName());

    rsH6Feedback.setPayRate(feedback.getPayRate());
    rsH6Feedback.setPayTotal(feedback.getPayTotal());

    rsH6Feedback.setSubmitterId(feedback.getSubmitterId());
    rsH6Feedback.setSubmitTime(feedback.getSubmitTime());
    rsH6Feedback.setSubmitterName(feedback.getSubmitterName());


    rsH6Feedback.setChannel(feedback.getChannel());
    rsH6Feedback.setCategoryPath(feedback.getCategoryPath());


    rsH6Feedback.setCreateInfo(feedback.getCreateInfo());
    rsH6Feedback.setLastModifyInfo(feedback.getLastModifyInfo());

    rsH6Feedback.setVersion(feedback.getVersion());
    rsH6Feedback.setVersionTime(feedback.getVersionTime());
    if (CollectionUtils.isNotEmpty(feedback.getImages())) {
      List<RsH6FeedbackImage> rsH6FeedbackImages = feedback.getImages().stream().map(s -> new RsH6FeedbackImage(s.getUuid(), s.getId(), s.getUrl())).collect(Collectors.toList());
      rsH6Feedback.setImages(rsH6FeedbackImages);
    }

    if (CollectionUtils.isNotEmpty(feedback.getDepLines())) {
      List<RsH6FeedbackDepLine> depLines = feedback.getDepLines().stream().map(s -> {
        RsH6FeedbackDepLine line = new RsH6FeedbackDepLine();
        line.setUuid(s.getUuid());
        line.setDepCode(s.getDepCode());
        line.setDepName(s.getDepName());
        line.setRate(s.getRate());
        line.setTotal(s.getTotal());
        return line;
      }).collect(Collectors.toList());
      rsH6Feedback.setDepLines(depLines);
    }

    return rsH6Feedback;
  }
}
