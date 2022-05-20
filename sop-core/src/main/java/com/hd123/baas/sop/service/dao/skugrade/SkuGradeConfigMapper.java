package com.hd123.baas.sop.service.dao.skugrade;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.dao.skugrade
 文件名：	SkuGradeConfigMapper.java
 模块说明：	
 修改历史：
 2021年02月26日 - wangdanhua - 创建。
 */

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.service.api.entity.SkuGradeConfig;

/**
 * @author wangdanhua
 */
public class SkuGradeConfigMapper implements RowMapper<SkuGradeConfig> {
  @Override
  public SkuGradeConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
    SkuGradeConfig entity = new SkuGradeConfig();
    mapFields(rs, rowNum, entity);
    return entity;
  }

  protected void mapFields(ResultSet rs, int rowNum, SkuGradeConfig entity) throws SQLException {
    entity.setUuid(rs.getInt(PSkuGradeConfig.UUID));
    entity.setOrgId(rs.getString(PSkuGradeConfig.ORG_ID));
    entity.setSkuId(rs.getString(PSkuGradeConfig.SKU_ID));
    entity.setPriceGradeJson(rs.getString(PSkuGradeConfig.PRICE_GRADE_JSON));
  }

}

