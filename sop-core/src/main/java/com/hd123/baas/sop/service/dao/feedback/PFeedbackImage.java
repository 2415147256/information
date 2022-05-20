package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * 质量反馈图片明细
 * @author yu lilin
 * @since 1.0
 */
public class PFeedbackImage extends PEntity {
    private static final long serialVersionUID = 5743801888372346320L;

    public static final String TABLE_NAME = "sop_feedback_image";

    public static final String FIELD_BILL_ID = "billId";
    public static final String FIELD_LINE_NO = "lineNo";
    public static final String FIELD_ID = "id";
    public static final String FIELD_URL = "url";
    public static final String FIELD_TENANT = "tenant";
    public static final String FIELD_SHOP = "shop";

    public static String[] bizColumns() {
        return new String[] {
            FIELD_BILL_ID,
            FIELD_LINE_NO,
            FIELD_ID,
            FIELD_URL,
            FIELD_TENANT,
            FIELD_SHOP
        };
    }

    public static String[] allColumns() {
        return toColumnArray(PEntity.allColumns(), bizColumns());
    }
}
