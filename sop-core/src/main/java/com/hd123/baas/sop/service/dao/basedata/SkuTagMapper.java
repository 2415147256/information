/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： SkuBomMapper.java
 * 模块说明：
 * 修改历史：
 * 2020年11月17日 - XLT - 创建。
 */
package
    com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.service.api.basedata.sku.DSkuTag;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SkuTagMapper extends PEntity.RowMapper<DSkuTag>  {
  @Override
  public DSkuTag mapRow(ResultSet rs, int rowNum) throws SQLException {
    DSkuTag skuTag = new DSkuTag();
    super.mapFields(rs, rowNum, skuTag);
    skuTag.setOrgType(rs.getString(PSkuTag.ORG_TYPE));
    skuTag.setOrgId(rs.getString(PSkuTag.ORG_ID));
    skuTag.setTenant(rs.getString(PSkuTag.TENANT));
    skuTag.setGoodsGid(rs.getString(PSkuTag.GOODS_GID));
    skuTag.setSkuId(rs.getString(PSkuTag.SKU_ID));
    skuTag.setCode(rs.getString(PSkuTag.CODE));
    skuTag.setName(rs.getString(PSkuTag.NAME));
    return skuTag;
  }
}
