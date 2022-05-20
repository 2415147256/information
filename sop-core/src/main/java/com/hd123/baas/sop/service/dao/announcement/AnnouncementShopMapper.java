package com.hd123.baas.sop.service.dao.announcement;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.announcement.AnnouncementShop;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author zhengzewang on 2020/11/20.
 */
public class AnnouncementShopMapper extends PEntity.RowMapper<AnnouncementShop> {
  @Override
  public AnnouncementShop mapRow(ResultSet rs, int rowNum) throws SQLException {
    AnnouncementShop entity = new AnnouncementShop();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PAnnouncementShop.TENANT));
    entity.setOwner(rs.getString(PAnnouncementShop.OWNER));
    entity.setShop(rs.getString(PAnnouncementShop.SHOP));
    entity.setShopCode(rs.getString(PAnnouncementShop.SHOP_CODE));
    entity.setShopName(rs.getString(PAnnouncementShop.SHOP_NAME));
    return entity;
  }
}
