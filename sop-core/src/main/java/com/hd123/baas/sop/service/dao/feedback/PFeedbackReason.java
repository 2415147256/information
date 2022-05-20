package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * 原因
 * @author yu lilin on 2020/11/20
 */
public class PFeedbackReason extends PStandardEntity {
    private static final long serialVersionUID = 8372160056811598184L;

    public static final String TABLE_NAME = "sop_feedback_reason";

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_TENANT = "tenant";
    public static final String ORG_ID = "orgId";


    public static String[] bizColumns() {
        return new String[] {
            FIELD_TYPE,
            FIELD_CONTENT,
            FIELD_TENANT,
            ORG_ID
        };
    }

    public static String[] allColumns() {
        return toColumnArray(PEntity.allColumns(), bizColumns());
    }


}
