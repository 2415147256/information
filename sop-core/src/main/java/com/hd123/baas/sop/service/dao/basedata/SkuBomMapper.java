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

import com.hd123.baas.sop.service.api.basedata.sku.DSkuBom;
import com.hd123.rumba.commons.jdbc.entity.PEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SkuBomMapper extends PEntity.RowMapper<DSkuBom>  {
  @Override
  public DSkuBom mapRow(ResultSet rs, int rowNum) throws SQLException {
    DSkuBom skuBom = new DSkuBom();
    super.mapFields(rs, rowNum, skuBom);
    skuBom.setOrgType(rs.getString(PSkuBom.ORG_TYPE));
    skuBom.setOrgId(rs.getString(PSkuBom.ORG_ID));
    skuBom.setTenant(rs.getString(PSkuBom.TENANT));
    skuBom.setGoodsGid(rs.getString(PSkuBom.GOODS_GID));
    skuBom.setSkuId(rs.getString(PSkuBom.SKU_ID));
    skuBom.setBom(rs.getString(PSkuBom.BOM));
    return skuBom;
  }
}
