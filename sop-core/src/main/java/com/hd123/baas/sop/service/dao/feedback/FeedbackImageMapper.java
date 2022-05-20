package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.baas.sop.service.api.feedback.FeedbackImage;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yu lilin on 2020/11/16
 */
public class FeedbackImageMapper extends PEntity.RowMapper<FeedbackImage> {
    @Override
    public FeedbackImage mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedbackImage entity = new FeedbackImage();
        super.mapFields(rs, rowNum, entity);
        entity.setId(rs.getString(PFeedbackImage.FIELD_ID));
        entity.setUrl(rs.getString(PFeedbackImage.FIELD_URL));
        return entity;
    }
}
