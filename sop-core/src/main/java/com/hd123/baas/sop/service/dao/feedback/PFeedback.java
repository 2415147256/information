package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 质量反馈单
 * @author yu lilin
 * @since 1.0
 */

public class PFeedback extends PStandardEntity {
    private static final long serialVersionUID = 547733174399379307L;

    public static final String TABLE_NAME = "sop_feedback";
    public static final String DEFAULT_CHANNEL="offline";
    public static final String ORG_ID="orgId";

    public static final String FIELD_BILLID = "billId";
    public static final String FIELD_APP_ID = "appId";
    public static final String FIELD_TENANT = "tenant";
    public static final String FIELD_SHOP = "shop";
    public static final String FIELD_SHOP_NO = "shopNo";
    public static final String FIELD_SHOP_NAME = "shopName";
    public static final String FIELD_RECEIPT_NUM = "receiptNum";
    public static final String FIELD_RECEIPT_LINE_ID = "receiptLineId";
    public static final String FIELD_GDUUID = "gdUuid";
    public static final String FIELD_GDINPUTCODE = "gdInputCode";
    public static final String FIELD_GDNAME = "gdName";
    public static final String FIELD_GDCODE = "gdCode";
    public static final String FIELD_MUNIT = "munit";
    public static final String FIELD_MIN_MUNIT = "minMunit";
    public static final String FIELD_QPC = "qpc";
    public static final String FIELD_GDTYPE_CODE = "gdTypeCode";
    public static final String FIELD_GDTYPE_NAME = "gdTypeName";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_DELIVERY_TIME = "deliveryTime";
    public static final String FIELD_SINGLE_PRICE = "singlePrice";
    public static final String FIELD_RECEIPT_QTY = "receiptQty";
    public static final String FIELD_QTY = "qty";
    public static final String FIELD_WEIGHT_QTY = "weightQty";
    public static final String FIELD_TOTAL = "total";
    public static final String FIELD_APPLY_REASON = "applyReason";
    public static final String FIELD_APPLY_NOTE = "applyNote";
    public static final String FIELD_AUDIT_REASON = "auditReason";
    public static final String FIELD_AUDIT_NOTE = "auditNote";
    public static final String FIELD_RESULT = "result";
    public static final String FIELD_PAY_RATE = "payRate";
    public static final String FIELD_PAY_TOTAL = "payTotal";
    public static final String FIELD_SUBMIT_TIME = "submitTime";
    public static final String FIELD_SUBMITTER_ID = "submitterId";
    public static final String FIELD_SUBMITTER_NAME = "submitterName";
    public static final String FIELD_AUDIT_TIME = "auditTime";
    public static final String FIELD_AUDITOR_ID = "auditorId";
    public static final String FIELD_AUDITOR_NAME = "auditorName";
    public static final String FIELD_STATE = "state";
    public static final String FIELD_CHANNEL="channel";
    public static final String FIELD_GRADE_ID="gradeId";
    public static final String FIELD_GRADE_NAME="gradeName";
    public static final String FIELD_EXT="ext";
    public static final String FIELD_CHECK_POLICY="checkPolicy";
    public static final String FIELD_SP_NO = "spNo";


    public static String[] bizColumns() {
        return new String[] {
            ORG_ID,
            FIELD_BILLID,
            FIELD_APP_ID,
            FIELD_TENANT,
            FIELD_SHOP,
            FIELD_SHOP_NO,
            FIELD_SHOP_NAME,
            FIELD_RECEIPT_NUM,
            FIELD_RECEIPT_LINE_ID,
            FIELD_GDUUID,
            FIELD_GDINPUTCODE,
            FIELD_GDNAME,
            FIELD_GDCODE,
            FIELD_MUNIT,
            FIELD_MIN_MUNIT,
            FIELD_QPC,
            FIELD_GDTYPE_CODE,
            FIELD_GDTYPE_NAME,
            FIELD_TYPE,
            FIELD_DELIVERY_TIME,
            FIELD_SINGLE_PRICE,
            FIELD_RECEIPT_QTY,
            FIELD_QTY,
            FIELD_WEIGHT_QTY,
            FIELD_TOTAL,
            FIELD_APPLY_REASON,
            FIELD_APPLY_NOTE,
            FIELD_SP_NO,
            FIELD_AUDIT_REASON,
            FIELD_AUDIT_NOTE,
            FIELD_RESULT,
            FIELD_PAY_RATE,
            FIELD_PAY_TOTAL,
            FIELD_SUBMIT_TIME,
            FIELD_SUBMITTER_ID,
            FIELD_SUBMITTER_NAME,
            FIELD_AUDIT_TIME,
            FIELD_AUDITOR_ID,
            FIELD_AUDITOR_NAME,
            FIELD_STATE,
            FIELD_CHANNEL,
            FIELD_GRADE_ID,
            FIELD_GRADE_NAME,
            FIELD_EXT,
            FIELD_CHECK_POLICY
        };
    }

    public static String[] allColumns() {
        return toColumnArray(PStandardEntity.allColumns(), bizColumns());
    }

    public static Map<String, Object> toAllFieldValues(Feedback feedback) {
        Map<String, Object> fvm = new HashMap<>();
        fvm.put(FIELD_BILLID, feedback.getBillId());
        fvm.put(ORG_ID, feedback.getOrgId());
        fvm.put(FIELD_APP_ID, feedback.getAppId());
        fvm.put(FIELD_TENANT, feedback.getTenant());
        fvm.put(FIELD_SHOP, feedback.getShop());
        fvm.put(FIELD_SHOP_NO, feedback.getShopNo());
        fvm.put(FIELD_SHOP_NAME, feedback.getShopName());
        fvm.put(FIELD_RECEIPT_NUM, feedback.getReceiptNum());
        fvm.put(FIELD_RECEIPT_LINE_ID, feedback.getReceiptLineId());
        fvm.put(FIELD_GDUUID, feedback.getGdUuid());
        fvm.put(FIELD_GDCODE, feedback.getGdCode());
        fvm.put(FIELD_GDNAME, feedback.getGdName());
        fvm.put(FIELD_GDINPUTCODE, feedback.getGdInputCode());
        fvm.put(FIELD_MIN_MUNIT, feedback.getMinMunit());
        fvm.put(FIELD_MUNIT, feedback.getMunit());
        fvm.put(FIELD_QPC, feedback.getQpc());
        fvm.put(FIELD_GDTYPE_CODE, feedback.getGdTypeCode());
        fvm.put(FIELD_GDTYPE_NAME, feedback.getGdTypeName());
        fvm.put(FIELD_TYPE, feedback.getType() == null? null: feedback.getType().name());
        fvm.put(FIELD_DELIVERY_TIME, feedback.getDeliveryTime());
        fvm.put(FIELD_RESULT, feedback.getResult() == null? null: feedback.getResult().name());
        fvm.put(FIELD_SINGLE_PRICE, feedback.getSinglePrice());
        fvm.put(FIELD_RECEIPT_QTY, feedback.getReceiptQty());
        fvm.put(FIELD_QTY, feedback.getQty());
        fvm.put(FIELD_WEIGHT_QTY, feedback.getWeightQty());
        fvm.put(FIELD_TOTAL, feedback.getTotal());
        fvm.put(FIELD_APPLY_REASON, feedback.getApplyReason());
        fvm.put(FIELD_APPLY_NOTE, feedback.getApplyNote());
        fvm.put(FIELD_SP_NO, feedback.getSpNo());
        fvm.put(FIELD_AUDIT_REASON, feedback.getAuditReason());
        fvm.put(FIELD_AUDIT_NOTE, feedback.getAuditNote());
        fvm.put(FIELD_PAY_RATE, feedback.getPayRate());
        fvm.put(FIELD_PAY_TOTAL, feedback.getPayTotal());
        if (feedback.getCreateInfo() != null) {
            fvm.put(CREATE_INFO_TIME, feedback.getCreateInfo().getTime());
            fvm.put(CREATE_INFO_OPERATOR_ID, feedback.getCreateInfo().getOperator().getId());
            fvm.put(CREATE_INFO_OPERATOR_FULL_NAME, feedback.getCreateInfo().getOperator().getFullName());
            fvm.put(CREATE_INFO_OPERATOR_NAMESPACE, feedback.getCreateInfo().getOperator().getNamespace());
        }
        if (feedback.getLastModifyInfo() != null) {
            fvm.put(LAST_MODIFY_INFO_TIME, feedback.getLastModifyInfo().getTime());
            fvm.put(LAST_MODIFY_INFO_OPERATOR_ID, feedback.getLastModifyInfo().getOperator().getId());
            fvm.put(LAST_MODIFY_INFO_OPERATOR_FULL_NAME, feedback.getLastModifyInfo().getOperator().getFullName());
            fvm.put(LAST_MODIFY_INFO_OPERATOR_NAMESPACE, feedback.getLastModifyInfo().getOperator().getNamespace());
        }
        fvm.put(FIELD_SUBMITTER_ID, feedback.getSubmitterId());
        fvm.put(FIELD_SUBMITTER_NAME, feedback.getSubmitterName());
        fvm.put(FIELD_SUBMIT_TIME, feedback.getSubmitTime());
        fvm.put(FIELD_AUDITOR_ID, feedback.getAuditorId());
        fvm.put(FIELD_AUDIT_TIME, feedback.getAuditTime());
        fvm.put(FIELD_AUDITOR_NAME, feedback.getAuditorName());
        fvm.put(FIELD_STATE, feedback.getState() == null? null : feedback.getState().name());
        fvm.put(FIELD_CHANNEL, StringUtil.isNullOrBlank(feedback.getChannel()) ? PFeedback.DEFAULT_CHANNEL : feedback.getChannel());
        fvm.put(FIELD_GRADE_ID,feedback.getGradeId());
        fvm.put(FIELD_GRADE_NAME,feedback.getGradeName());
        fvm.put(FIELD_EXT, JsonUtil.objectToJson(feedback.getExt()));
        fvm.put(FIELD_CHECK_POLICY, feedback.getCheckPolicy());
        return fvm;
    }
}
