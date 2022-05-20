package com.hd123.baas.sop.service.dao.voice;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.voice.Voice;
import com.hd123.baas.sop.service.api.voice.VoiceTemplateCode;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author W.J.H.7
 */
public class VoiceMapper extends PEntity.RowMapper<Voice> {
  @Override
  public Voice mapRow(ResultSet rs, int rowNum) throws SQLException {
    Voice target = new Voice();
    super.mapFields(rs, rowNum, target);

    target.setTenant(rs.getString(PVoice.TENANT));
    target.setRequestId(rs.getString(PVoice.REQUEST_ID));

    target.setTitle(rs.getString(PVoice.TITLE));

    target.setTemplateId(rs.getString(PVoice.TEMPLATE_ID));
    target.setTemplateCode(VoiceTemplateCode.valueOf(rs.getString(PVoice.TEMPLATE_CODE)));
    target.setTemplateContent(rs.getString(PVoice.TEMPLATE_CONTENT));

    target.setCreated(rs.getTimestamp(PVoice.CREATED));

    return target;
  }
}
