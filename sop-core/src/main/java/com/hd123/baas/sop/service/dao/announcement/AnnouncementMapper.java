package com.hd123.baas.sop.service.dao.announcement;

import com.hd123.baas.sop.service.api.announcement.Announcement;
import com.hd123.baas.sop.service.api.announcement.AnnouncementProgress;
import com.hd123.baas.sop.service.api.announcement.AnnouncementState;
import com.hd123.baas.sop.service.api.announcement.AnnouncementTargetType;
import com.hd123.baas.sop.utils.BlobUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhengzewang on 2020/11/20.
 */
public class AnnouncementMapper extends PStandardEntity.RowMapper<Announcement> {
  @SneakyThrows
  @Override
  public Announcement mapRow(ResultSet rs, int rowNum) throws SQLException {
    Announcement entity = new Announcement();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PAnnouncement.TENANT));
    entity.setAllShops(rs.getBoolean(PAnnouncement.ALL_SHOPS));
    entity.setSendPos(rs.getBoolean(PAnnouncement.SEND_POS));
    entity.setTitle(rs.getString(PAnnouncement.TITLE));
    entity.setContent(BlobUtil.decode(rs.getBlob(PAnnouncement.CONTENT)));
    entity.setImage(rs.getString(PAnnouncement.IMAGE));
    entity.setUrl(rs.getString(PAnnouncement.URL));
    entity.setState(AnnouncementState.valueOf(rs.getString(PAnnouncement.STATE)));
    entity.setProgress(AnnouncementProgress.valueOf(rs.getString(PAnnouncement.PROGRESS)));
    entity.setOrgId(rs.getString(PAnnouncement.ORG_ID));
    entity.setTargetType(AnnouncementTargetType.valueOf(rs.getString(PAnnouncement.TARGET_TYPE)));
    return entity;
  }
}
