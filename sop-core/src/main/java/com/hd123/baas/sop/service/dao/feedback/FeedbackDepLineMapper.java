package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackDepLine;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yu lilin on 2020/11/20
 */
public class FeedbackDepLineMapper extends PEntity.RowMapper<FeedbackDepLine> {
    @Override
    public FeedbackDepLine mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedbackDepLine entity = new FeedbackDepLine();
        super.mapFields(rs, rowNum, entity);
        entity.setDepCode(rs.getString(PFeedbackDepLine.FIELD_DEP_CODE));
        entity.setDepName(rs.getString(PFeedbackDepLine.FIELD_DEP_NAME));
        entity.setRate(rs.getBigDecimal(PFeedbackDepLine.FIELD_RATE));
        entity.setTotal(rs.getBigDecimal(PFeedbackDepLine.FIELD_TOTAL));
        return entity;
    }
}
