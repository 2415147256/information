package com.hd123.baas.sop.service.dao.price.priceadjustment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.baas.sop.service.api.price.PriceSku;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.priceadjustment.*;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.BlobUtil;
import com.hd123.rumba.commons.jdbc.entity.PEntity;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/11.
 */
public class PriceAdjustmentLineMapper extends PEntity.RowMapper<PriceAdjustmentLine> {
  @Override
  public PriceAdjustmentLine mapRow(ResultSet rs, int rowNum) throws SQLException {
    PriceAdjustmentLine entity = new PriceAdjustmentLine();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PPriceAdjustmentLine.TENANT));
    entity.setOwner(rs.getString(PPriceAdjustmentLine.OWNER));

    PriceSku sku = new PriceSku();
    sku.setId(rs.getString(PPriceAdjustmentLine.SKU_ID));
    sku.setGoodsGid(rs.getString(PPriceAdjustmentLine.SKU_GID));
    sku.setCode(rs.getString(PPriceAdjustmentLine.SKU_CODE));
    sku.setName(rs.getString(PPriceAdjustmentLine.SKU_NAME));
    sku.setQpc(rs.getBigDecimal(PPriceAdjustmentLine.SKU_QPC));
    sku.setUnit(rs.getString(PPriceAdjustmentLine.SKU_UNIT));
    entity.setSku(sku);

    entity.setSkuInPrice(rs.getBigDecimal(PPriceAdjustmentLine.SKU_IN_PRICE));
    entity.setSkuInitInPrice(rs.getBigDecimal(PPriceAdjustmentLine.SKU_INIT_IN_PRICE));
    entity.setSkuBasePrice(rs.getBigDecimal(PPriceAdjustmentLine.SKU_BASE_PRICE));

    entity.setSkuToleranceValue(rs.getBigDecimal(PPriceAdjustmentLine.SKU_TOLERANCE_VALUE));
    entity.setSkuKv(rs.getBigDecimal(PPriceAdjustmentLine.SKU_KV));
    entity.setSkuBv(rs.getBigDecimal(PPriceAdjustmentLine.SKU_BV));
    entity.setSkuIncreaseRate(rs.getBigDecimal(PPriceAdjustmentLine.SKU_INCREASE_RATE));
    entity.setRemark(rs.getString(PPriceAdjustmentLine.REMARK));
    // 是否计算尾差
    entity.setCalcTailDiff(rs.getBoolean(PPriceAdjustmentLine.CALC_TAIL_DIFF));

    String skuDefine = rs.getString(PPriceAdjustmentLine.SKU_DEFINE);
    if (skuDefine != null) {
      entity.setSkuDefine(SkuDefine.valueOf(skuDefine));
    }
    entity.setRaw(rs.getString(PPriceAdjustmentLine.RAW));
    entity.setSkuGroup(rs.getString(PPriceAdjustmentLine.SKU_GROUP));
    entity.setSkuGroupName(rs.getString(PPriceAdjustmentLine.SKU_GROUP_NAME));
    entity.setSkuGroupToleranceValue(rs.getBigDecimal(PPriceAdjustmentLine.SKU_GROUP_TOLERANCE_VALUE));

    entity.setSkuPosition(rs.getString(PPriceAdjustmentLine.SKU_POSITION));
    entity.setSkuPositionName(rs.getString(PPriceAdjustmentLine.SKU_POSITION_NAME));
    try {
      entity.setSkuPositionIncreaseRates(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.SKU_POSITION_INCREASE_RATES)),
              new TypeReference<List<PriceIncreaseRate>>() {
              }));
      entity.setPriceRangeIncreaseRates(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.PRICE_RANGE_INCREASE_RATES)),
              new TypeReference<List<PriceIncreaseRate>>() {
              }));
      entity.setSkuGradeIncreaseRates(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.SKU_GRADE_INCREASE_RATES)),
              new TypeReference<List<PriceIncreaseRate>>() {
              }));
      entity
          .setIncreaseRules(BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.INCREASE_RULES)),
              new TypeReference<List<PriceIncreaseRule>>() {
              }));
      entity.setPriceGrades(BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.PRICE_GRADES)),
          new TypeReference<List<PriceGradeSalePrice>>() {
          }));

      entity.setPrePriceRangeIncreaseRates(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.PRE_PRICE_RANGE_INCREASE_RATES)),
              new TypeReference<List<PriceIncreaseRate>>() {
              }));

      entity.setPrePriceGrades(
          BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.PRE_PRICE_GRADES)),
              new TypeReference<List<PriceGradeSalePrice>>() {
              }));
      entity.setExt(BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PPriceAdjustmentLine.EXT)),
          new TypeReference<ObjectNode>() {
          }));
    } catch (BaasException e) {
      throw new SQLException(e);
    }
    String increaseType = rs.getString(PPriceAdjustmentLine.INCREASE_TYPE);
    entity.setIncreaseType(StringUtils.isBlank(increaseType) ? null : PriceIncreaseType.valueOf(increaseType));

    entity.setHighInPrice(rs.getBigDecimal(PPriceAdjustmentLine.HIGH_IN_PRICE));
    entity.setLowInPrice(rs.getBigDecimal(PPriceAdjustmentLine.LOW_IN_PRICE));
    entity.setHighBackGrossRate(rs.getBigDecimal(PPriceAdjustmentLine.HIGH_BACK_GROSS_RATE));
    entity.setLowBackGrossRate(rs.getBigDecimal(PPriceAdjustmentLine.LOW_BACK_GROSS_RATE));
    entity.setHighFrontGrossRate(rs.getBigDecimal(PPriceAdjustmentLine.HIGH_FRONT_GROSS_RATE));
    entity.setLowFrontGrossRate(rs.getBigDecimal(PPriceAdjustmentLine.LOW_FRONT_GROSS_RATE));
    entity.setHighMarketDiffRate(rs.getBigDecimal(PPriceAdjustmentLine.HIGH_MARKET_DIFF_RATE));
    entity.setLowMarketDiffRate(rs.getBigDecimal(PPriceAdjustmentLine.LOW_MARKET_DIFF_RATE));
    entity.setHighPriceFloatRate(rs.getBigDecimal(PPriceAdjustmentLine.HIGH_PRICE_FLOAT_RATE));
    entity.setLowPriceFloatRate(rs.getBigDecimal(PPriceAdjustmentLine.LOW_PRICE_FLOAT_RATE));

    entity.setPreSkuInPrice(rs.getBigDecimal(PPriceAdjustmentLine.PRE_SKU_IN_PRICE));
    entity.setPreSkuIncreaseRate(rs.getBigDecimal(PPriceAdjustmentLine.PRE_SKU_INCREASE_RATE));

    entity.setAveWeekQty(rs.getBigDecimal(PPriceAdjustmentLine.AVE_WEEK_QTY));

    return entity;
  }
}
