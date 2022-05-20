package com.hd123.baas.sop.service.dao.price.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRule;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseType;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.BlobUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/10.
 */
public class PriceSkuConfigMapper extends PStandardEntity.RowMapper<PriceSkuConfig> {
  @Override
  public PriceSkuConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceSkuConfig config = new PriceSkuConfig();
    super.mapFields(rs, rowNum, config);
    config.setOrgId(rs.getString(PPriceSkuConfig.ORG_ID));
    config.setTenant(rs.getString(PPriceSkuConfig.TENANT));
    config.setSku(new PriceSku());
    config.getSku().setId(rs.getString(PPriceSkuConfig.SKU_ID));
    config.setToleranceValue(rs.getBigDecimal(PPriceSkuConfig.TOLERANCE_VALUE));
    config.setKv(rs.getBigDecimal(PPriceSkuConfig.KV));
    config.setBv(rs.getBigDecimal(PPriceSkuConfig.BV));
    config.setIncreaseRate(rs.getBigDecimal(PPriceSkuConfig.INCREASE_RATE));
    config.setCalcTailDiff(rs.getBoolean(PPriceSkuConfig.CALC_TAIL_DIFF));
    config.setSkuPosition(rs.getString(PPriceSkuConfig.SKU_POSITION));
    config.setHighInPrice(rs.getBigDecimal(PPriceSkuConfig.HIGH_IN_PRICE));
    config.setLowInPrice(rs.getBigDecimal(PPriceSkuConfig.LOW_IN_PRICE));
    config.setHighBackGrossRate(rs.getBigDecimal(PPriceSkuConfig.HIGH_BACK_GROSS_RATE));
    config.setLowBackGrossRate(rs.getBigDecimal(PPriceSkuConfig.LOW_BACK_GROSS_RATE));
    config.setHighFrontGrossRate(rs.getBigDecimal(PPriceSkuConfig.HIGH_FRONT_GROSS_RATE));
    config.setLowFrontGrossRate(rs.getBigDecimal(PPriceSkuConfig.LOW_FRONT_GROSS_RATE));
    config.setHighMarketDiffRate(rs.getBigDecimal(PPriceSkuConfig.HIGH_MARKET_DIFF_RATE));
    config.setLowMarketDiffRate(rs.getBigDecimal(PPriceSkuConfig.LOW_MARKET_DIFF_RATE));
    config.setHighPriceFloatRate(rs.getBigDecimal(PPriceSkuConfig.HIGH_PRICE_FLOAT_RATE));
    config.setLowPriceFloatRate(rs.getBigDecimal(PPriceSkuConfig.LOW_PRICE_FLOAT_RATE));
    String increaseType = rs.getString(PPriceSkuConfig.INCREASE_TYPE);
    if (increaseType != null) {
      config.setIncreaseType(PriceIncreaseType.valueOf(increaseType));
    }
    try {
      config.setIncreaseRules(BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceSkuConfig.INCREASE_RULES)),
          new TypeReference<List<PriceIncreaseRule>>() {
          }));
      config.setExt(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceSkuConfig.EXT)), new TypeReference<ObjectNode>() {
          }));
    } catch (BaasException e) {
      throw new SQLException(e);
    }
    return config;
  }
}
