package com.hd123.baas.sop.service.api.feedback;

import com.hd123.baas.sop.remote.rssos.feedback.RsFeedback;
import com.hd123.baas.sop.remote.rssos.feedback.RsFeedbackImage;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yu lilin on 2020/11/19
 */
public class RsToFeedback implements Converter<RsFeedback, Feedback> {
    private RsToFeedback() {

    }
    private static RsToFeedback instance = new RsToFeedback();

    public static RsToFeedback getInstance() {
        return instance;
    }

    @Override
    public Feedback convert(RsFeedback source) throws ConversionException {
        if (source == null) {
            return null;
        }
        Feedback target = new Feedback();
        target.setBillId(source.getBillId());
        target.setAppId(source.getAppId());
        target.setTenant(source.getTenantId());
        target.setShop(source.getShopId());
        target.setShopNo(source.getShopNo());
        target.setShopName(source.getShopName());
        target.setReceiptNum(source.getReceiptNum());
        target.setReceiptLineId(source.getReceiptLineId());
        target.setGdUuid(source.getGdUuid());
        target.setGdName(source.getGdName());
        target.setGdInputCode(source.getGdInputCode());
        target.setGdCode(source.getGdCode());
        target.setMunit(source.getMunit());
        target.setMinMunit(source.getMinMunit());
        target.setQpc(source.getQpc());
        target.setType(source.getType());
        target.setDeliveryTime(source.getDeliveryTime());
        target.setSinglePrice(source.getSinglePrice());
        target.setReceiptQty(source.getReceiptQty());
        target.setQty(source.getQty());
        target.setWeightQty(source.getSubQty());
        target.setTotal(source.getTotal());
        target.setApplyReason(source.getApplyReason());
        target.setApplyNote(source.getApplyNote());
        target.setResult(source.getResult());
        target.setAuditReason(source.getAuditReason());
        target.setAuditNote(source.getAuditNote());
        target.setPayRate(source.getPayRate());
        target.setPayTotal(source.getPayTotal());
        //设置创建信息
        OperateInfo operateInfo = new OperateInfo();
        operateInfo.setTime(source.getCreated());
        Operator operator = new Operator();
        operator.setId(source.getCreatorId());
        operator.setFullName(source.getCreatorName());
        operateInfo.setOperator(operator);
        target.setCreateInfo(operateInfo);
        //设置最后修改信息
        OperateInfo lastModifyInfo = new OperateInfo();
        Operator lastModifyOperator = new Operator();
        lastModifyInfo.setTime(source.getLastModified());
        lastModifyOperator.setId(source.getLastModifierId());
        lastModifyOperator.setFullName(source.getLastModifierName());
        lastModifyInfo.setOperator(lastModifyOperator);
        target.setLastModifyInfo(lastModifyInfo);

        target.setSubmitTime(source.getSubmitTime());
        target.setSubmitterName(source.getSubmitterName());
        target.setSubmitterId(source.getSubmitterId());
        target.setAuditTime(source.getAuditTime());
        target.setAuditorId(source.getAuditorId());
        target.setAuditorName(source.getAuditorName());
        target.setState(source.getState());
        target.setChannel(source.getChannel());
        //设置图片明细
        List<FeedbackImage> images = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(source.getImages())) {
            for (RsFeedbackImage sourceImage : source.getImages()) {
                images.add(convert(sourceImage));
            }
        }
        target.setImages(images);

        if (source.getGrade() != null){
            target.setGradeId(source.getGrade().getId());
            target.setGradeName(source.getGrade().getName());
        }
        return target;
    }

    public FeedbackImage convert(RsFeedbackImage source) {
        if (source == null) {
            return null;
        }
        FeedbackImage target = new FeedbackImage();
        target.setId(source.getId());
        target.setUrl(source.getUrl());
        return target;
    }
}
