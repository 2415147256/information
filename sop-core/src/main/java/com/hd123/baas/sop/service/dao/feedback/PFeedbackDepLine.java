package com.hd123.baas.sop.service.dao.feedback;


import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * 质量反馈单承担明细
 * @author yu lilin
 * @since 1.0
 */
public class PFeedbackDepLine extends PEntity {
    private static final long serialVersionUID = -2952033501678897015L;

    public static final String TABLE_NAME = "sop_feedback_depline";

    public static final String FIELD_BILL_ID = "billId";
    public static final String FIELD_LINE_NO = "lineNo";
    public static final String FIELD_DEP_CODE = "depCode";
    public static final String FIELD_DEP_NAME = "depName";
    public static final String FIELD_RATE = "rate";
    public static final String FIELD_TOTAL = "total";
    public static final String FIELD_TENANT = "tenant";
    public static final String FIELD_SHOP = "shop";


    public static String[] bizColumns() {
        return new String[] {
                FIELD_BILL_ID,
                FIELD_LINE_NO,
                FIELD_DEP_CODE,
                FIELD_DEP_NAME,
                FIELD_RATE,
                FIELD_TOTAL,
                FIELD_TENANT,
                FIELD_SHOP
        };
    }

    public static String[] allColumns() {
        return toColumnArray(PEntity.allColumns(), bizColumns());
    }

}
