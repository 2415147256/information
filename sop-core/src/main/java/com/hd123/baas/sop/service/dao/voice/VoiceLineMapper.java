package com.hd123.baas.sop.service.dao.voice;

import com.hd123.baas.sop.service.api.voice.VoiceLine;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class VoiceLineMapper extends PEntity.RowMapper<VoiceLine>{
  @SneakyThrows
  @Override
  public VoiceLine mapRow(ResultSet rs, int rowNum) throws SQLException {
    final VoiceLine voiceLine = new VoiceLine();
    super.mapFields(rs, rowNum, voiceLine);
    voiceLine.setCallee(rs.getString(PVoiceLine.CALLEE));
    voiceLine.setOwner(rs.getString(PVoiceLine.OWNER));
    voiceLine.setTemplateParams(JSONUtil.safeToObject(rs.getString(PVoiceLine.TEMPLATE_PARAS), HashMap.class));
    voiceLine.setUuid(rs.getString(PVoiceLine.UUID));
    voiceLine.setTenant(rs.getString(PVoiceLine.TENANT));
    final UCN shop = new UCN();
    shop.setCode(rs.getString(PVoiceLine.SHOP_CODE));
    shop.setUuid(rs.getString(PVoiceLine.SHOP_ID));
    shop.setName(rs.getString(PVoiceLine.SHOP_NAME));
    voiceLine.setShop(shop);
    return voiceLine;
  }
}
