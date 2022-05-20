package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackExt;
import com.hd123.baas.sop.service.api.feedback.FeedbackResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.baas.sop.service.api.feedback.FeedbackType;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.StringUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yu lilin on 2020/11/13
 */
public class FeedbackMapper extends PStandardEntity.RowMapper<Feedback> {
    @Override
    public Feedback mapRow(ResultSet rs, int rowNum) throws SQLException {
        Feedback entity = new Feedback();
        super.mapFields(rs, rowNum, entity);
        entity.setOrgId(rs.getString(PFeedback.ORG_ID));
        entity.setBillId(rs.getString(PFeedback.FIELD_BILLID));
        entity.setAppId(rs.getString(PFeedback.FIELD_APP_ID));
        entity.setTenant(rs.getString(PFeedback.FIELD_TENANT));
        entity.setShop(rs.getString(PFeedback.FIELD_SHOP));
        entity.setShopNo(rs.getString(PFeedback.FIELD_SHOP_NO));
        entity.setShopName(rs.getString(PFeedback.FIELD_SHOP_NAME));
        entity.setReceiptNum(rs.getString(PFeedback.FIELD_RECEIPT_NUM));
        entity.setReceiptLineId(rs.getString(PFeedback.FIELD_RECEIPT_LINE_ID));
        entity.setGdUuid(rs.getString(PFeedback.FIELD_GDUUID));
        entity.setGdCode(rs.getString(PFeedback.FIELD_GDCODE));
        entity.setGdInputCode(rs.getString(PFeedback.FIELD_GDINPUTCODE));
        entity.setGdName(rs.getString(PFeedback.FIELD_GDNAME));
        entity.setMunit(rs.getString(PFeedback.FIELD_MUNIT));
        entity.setMinMunit(rs.getString(PFeedback.FIELD_MIN_MUNIT));
        entity.setQpc(rs.getBigDecimal(PFeedback.FIELD_QPC));
        entity.setGdTypeCode(rs.getString(PFeedback.FIELD_GDTYPE_CODE));
        entity.setGdTypeName(rs.getString(PFeedback.FIELD_GDTYPE_NAME));
        entity.setType(StringUtil.toEnum(rs.getString(PFeedback.FIELD_TYPE), FeedbackType.class));
        entity.setDeliveryTime(rs.getTimestamp(PFeedback.FIELD_DELIVERY_TIME));
        entity.setSinglePrice(rs.getBigDecimal(PFeedback.FIELD_SINGLE_PRICE));
        entity.setReceiptQty(rs.getBigDecimal(PFeedback.FIELD_RECEIPT_QTY));
        entity.setQty(rs.getBigDecimal(PFeedback.FIELD_QTY));
        entity.setWeightQty(rs.getBigDecimal(PFeedback.FIELD_WEIGHT_QTY));
        entity.setTotal(rs.getBigDecimal(PFeedback.FIELD_TOTAL));
        entity.setSpNo(rs.getString(PFeedback.FIELD_SP_NO));
        entity.setApplyReason(rs.getString(PFeedback.FIELD_APPLY_REASON));
        entity.setApplyNote(rs.getString(PFeedback.FIELD_APPLY_NOTE));
        entity.setResult(StringUtil.toEnum(rs.getString(PFeedback.FIELD_RESULT), FeedbackResult.class));
        entity.setAuditReason(rs.getString(PFeedback.FIELD_AUDIT_REASON));
        entity.setAuditNote(rs.getString(PFeedback.FIELD_AUDIT_NOTE));
        entity.setPayRate(rs.getBigDecimal(PFeedback.FIELD_PAY_RATE));
        entity.setPayTotal(rs.getBigDecimal(PFeedback.FIELD_PAY_TOTAL));
        entity.setSubmitterId(rs.getString(PFeedback.FIELD_SUBMITTER_ID));
        entity.setSubmitterName(rs.getString(PFeedback.FIELD_SUBMITTER_NAME));
        entity.setSubmitTime(rs.getTimestamp(PFeedback.FIELD_SUBMIT_TIME));
        entity.setAuditorId(rs.getString(PFeedback.FIELD_AUDITOR_ID));
        entity.setAuditorName(rs.getString(PFeedback.FIELD_AUDITOR_NAME));
        entity.setAuditTime(rs.getTimestamp(PFeedback.FIELD_AUDIT_TIME));
        entity.setState(StringUtil.toEnum(rs.getString(PFeedback.FIELD_STATE), FeedbackState.class));
        entity.setChannel(rs.getString(PFeedback.FIELD_CHANNEL));
        entity.setGradeId(rs.getString(PFeedback.FIELD_GRADE_ID));
        entity.setGradeName(rs.getString(PFeedback.FIELD_GRADE_NAME));
        entity.setExt(JsonUtil.jsonToObject(rs.getString(PFeedback.FIELD_EXT), FeedbackExt.class));
        entity.setCheckPolicy(rs.getString(PFeedback.FIELD_CHECK_POLICY));
        return entity;
    }
}
