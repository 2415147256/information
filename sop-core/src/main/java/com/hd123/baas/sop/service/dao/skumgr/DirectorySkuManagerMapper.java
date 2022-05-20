package com.hd123.baas.sop.service.dao.skumgr;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd123.baas.sop.service.api.skumgr.DirectorySkuManager;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @Author maodapeng
 * @Since
 */
public class DirectorySkuManagerMapper extends PEntity.RowMapper<DirectorySkuManager> {
  @Override
  public DirectorySkuManager mapRow(ResultSet rs, int rowNum) throws SQLException {
    DirectorySkuManager entity = new DirectorySkuManager();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PDirectoryManager.TENANT));
    entity.setUuid(rs.getString(PDirectoryManager.UUID));
    entity.setShop(rs.getString(PDirectoryManager.SHOP));
    entity.setShopCode(rs.getString(PDirectoryManager.SHOP_CODE));
    entity.setShopName(rs.getString(PDirectoryManager.SHOP_NAME));
    entity.setSkuId(rs.getString(PDirectoryManager.SKU_ID));
    entity.setSkuCode(rs.getString(PDirectoryManager.SKU_CODE));
    entity.setSkuName(rs.getString(PDirectoryManager.SKU_NAME));
    entity.setSkuQpc(rs.getBigDecimal(PDirectoryManager.SKU_QPC));
    entity.setSkuGid(rs.getString(PDirectoryManager.SKU_GID));
    entity.setIssueDate(rs.getDate(PDirectoryManager.ISSUE_DATE));
    entity.setChannelRequired(rs.getBoolean(PDirectoryManager.CHANNEL_REQUIRED));
    entity.setDirectoryRequired(rs.getBoolean(PDirectoryManager.DIRECTORY_REQUIRED));
    return entity;
  }
}
