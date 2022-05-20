package com.hd123.baas.sop.service.impl.price.priceadjustment;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 
 项目名：	com.hd123.baas.sop.service.impl.price.priceadjustment
 文件名：	ChangeInPrice.java
 模块说明：	
 修改历史：
 2021年03月17日 - wangdanhua - 创建。
 */

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.hd123.baas.sop.service.api.entity.SkuGroup;
import com.hd123.baas.sop.service.api.formula.PriceSkuFormula;
import com.hd123.baas.sop.service.api.formula.PriceSkuFormulaService;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.price.SkuDefine;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfig;
import com.hd123.baas.sop.service.api.price.config.PriceSkuConfigService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustment;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentLine;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentState;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PriceAdjustmentDaoBof;
import com.hd123.baas.sop.service.dao.price.priceadjustment.PriceAdjustmentLineDaoBof;
import com.hd123.baas.sop.service.impl.price.config.PriceSkuTemplateBom;
import com.hd123.baas.sop.service.impl.price.spel.SpelMgr;
import com.hd123.baas.sop.service.impl.price.spel.param.PriceSkuBasePriceFormulaParam;
import com.hd123.baas.sop.utils.CommonUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrder;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wangdanhua
 **/
@Slf4j
public class ChangeInPrice {

  @Autowired
  private PriceSkuConfigService priceSkuConfigService;
  @Autowired
  private PriceAdjustmentLineDaoBof priceAdjustmentLineDao;
  @Autowired
  private PriceSkuFormulaService priceSkuFormulaService;
  @Autowired
  private PriceAdjustmentDaoBof priceAdjustmentDao;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private SpelMgr spelMgr;

  private List<PriceAdjustmentLine> modifiedLines = new ArrayList<>();

  public List<PriceAdjustmentLine> change(String tenant, String orgId, PriceAdjustmentLine line, BigDecimal inPrice)
      throws BaasException {
    SkuDefine skuDefine = line.getSkuDefine();
    if (skuDefine != SkuDefine.SPLITBYPART_FINISH) {
      if (!judgeChangeByTV(tenant, orgId, line.getSku().getId(), line.getSkuGroup(), inPrice)) {
        throw new BaasException("商品目标采购价修改前后差距超过容差值");
      }
    }
    inPrice = inPrice.setScale(2, RoundingMode.HALF_UP);
    // 赋值
    setSkuInPrice(line, inPrice);
    // 修改到店价
    PriceSkuConfig config = priceSkuConfigService.getBySkuId(tenant, orgId, line.getSku().getId());
    if (skuDefine == SkuDefine.SPLITBYPART_FINISH) {

    } else if (skuDefine == SkuDefine.SPLITBYPART_RAW) {
      // 找到对应的分割品的行id
      PriceSkuTemplateBom bom = JsonUtil.jsonToObject(line.getRaw(), PriceSkuTemplateBom.class);
      if (bom != null && CollectionUtils.isNotEmpty(bom.getFinish())) {
        List<PriceAdjustmentLine> lines = priceAdjustmentLineDao.list(tenant, line.getOwner());

        BigDecimal rawBasePrice = BigDecimal.ZERO;
        for (PriceAdjustmentLine item : lines) {
          for (PriceSkuTemplateBom.PriceSkuTemplateBomLine bomLine : bom.getFinish()) {
            // GID相同
            if (item.getSku().getQpc().compareTo(BigDecimal.ONE) == 0
                && item.getSku().getGoodsGid().equalsIgnoreCase(bomLine.getGdGid())) {

              // 分割品
              PriceSkuConfig itemConfig = priceSkuConfigService.getBySkuId(tenant, orgId,
                  item.getSku().getId());
              if (item.getSkuKv() == null || item.getSkuBv() == null) {
                if (itemConfig == null) {
                  throw new BaasException("商品未维护k值和B值");
                }
                if (itemConfig.getKv() == null) {
                  throw new BaasException("商品未维护k值");
                }
                if (itemConfig.getBv() == null) {
                  throw new BaasException("商品未维护B值");
                }
              }
              item.setSkuKv(itemConfig.getKv());
              item.setSkuBv(itemConfig.getBv());
              setSkuInPrice(item, inPrice);
              // 价格计算的服务
              item.setSkuBasePrice(calcBasePrice(inPrice, item.getSkuKv(), item.getSkuBv()));
              // 更新分割品的价格
              modifiedLines.add(item);
              // 累计
              rawBasePrice = rawBasePrice.add(item.getSkuBasePrice().multiply(bomLine.getRate()));
            }
          }
        }
        PriceSkuFormula formula = priceSkuFormulaService.getBySkuId(tenant, orgId,
            line.getSku().getId());
        // 商品的到店价根据配置的公式计算
        if (formula != null) {
          BigDecimal formulaBasePrice = calRawSkuBasePrice(tenant, orgId, line, lines);
          line.setSkuBasePrice(formulaBasePrice.setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
          // 商品的到店价根据bom计算
          rawBasePrice = rawBasePrice.setScale(2, BigDecimal.ROUND_HALF_UP);
          line.setSkuBasePrice(rawBasePrice);
        }
        // 更新依赖该原料品的商品到店价
        changeRawSkuBasePrice(tenant, orgId, line, lines);
      }
    } else {
      // 非分割品
      if (line.getSkuIncreaseRate() == null || line.getSkuIncreaseRate().compareTo(BigDecimal.ZERO) == 0) {
        if (config == null || config.getIncreaseRate() == null) {
          throw new BaasException("商品未设置后台加价率");
        }
        line.setSkuIncreaseRate(config.getIncreaseRate());
      }
      line.setSkuBasePrice(calcBasePrice(inPrice, line.getSkuIncreaseRate()));
    }
    if (line.getSkuToleranceValue() == null) {
      // 去配置的容差值
      line.setSkuToleranceValue(config.getToleranceValue());
    }

    modifiedLines.add(line);
    return modifiedLines;
  }

  private void setSkuInPrice(PriceAdjustmentLine line, BigDecimal inPrice) {
    BigDecimal oldSkuInPrice = line.getSkuInPrice();
    if (!CommonUtils.valueEquals(inPrice, oldSkuInPrice)) {
      line.setPreSkuInPrice(oldSkuInPrice);
      line.setSkuInPrice(inPrice);
    }
  }

  private boolean judgeChangeByTV(String tenant, String orgId, String skuId, String skuGroupUuid, BigDecimal inPrice)
      throws BaasException {

    PriceSkuConfig skuConfig = priceSkuConfigService.getBySkuId(tenant, orgId, skuId);
    // 没有容差值
    BigDecimal toleranceValue = skuConfig != null ? skuConfig.getToleranceValue() : null;
    if (toleranceValue == null) {
      SkuGroup skuGroup = skuGroupService.get(tenant, skuGroupUuid);
      toleranceValue = skuGroup.getToleranceValue();
    }
    if (toleranceValue == null) {
      log.info("商品{}未找到容差值", skuId);
      return true;
    }
    if (toleranceValue.compareTo(BigDecimal.ZERO) < 0) {
      log.info("商品{}的容差值小于0", skuId);
      return false;
    }
    if (inPrice == null) {
      throw new BaasException("目标采购价不允许为空");
    }
    // 查询最新已发布的试算单
    QueryDefinition qd = new QueryDefinition();
    qd.setPageSize(1);
    qd.setPage(0);
    qd.addByField(PriceAdjustment.Queries.STATE, Cop.IN, PriceAdjustmentState.PUBLISHED.name(),
        PriceAdjustmentState.AUDITED.name());
    qd.addOrder(PriceAdjustment.Queries.EFFECTIVE_START_DATE, QueryOrder.DESC);
    List<PriceAdjustment> result = priceAdjustmentDao.query(tenant, qd).getRecords();
    if (CollectionUtils.isEmpty(result)) {
      log.info("不存在已发布的试算单");
      return true;
    }
    PriceAdjustment lastAdjustment = result.get(0);
    List<PriceAdjustmentLine> priceAdjustmentLines = priceAdjustmentLineDao.list(tenant, lastAdjustment.getUuid());
    if (CollectionUtils.isEmpty(priceAdjustmentLines)) {
      log.info("最近发布试算单中不存在行信息");
      return true;
    }
    PriceAdjustmentLine priceAdjustmentLine = priceAdjustmentLines.stream()
        .filter(i -> i.getSku().getId().equals(skuId))
        .findAny()
        .orElse(null);
    if (priceAdjustmentLine == null) {
      log.info("最近生效试算单中未包含商品:{}", skuId);
      return true;
    }
    BigDecimal oldPrice = priceAdjustmentLine.getSkuInPrice();
    if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) == 0) {
      log.info("最近生效试算单中该商品目标采购价为0");
      return true;
    }

    BigDecimal diffRatio = (inPrice.subtract(oldPrice)).abs().divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP);
    if (diffRatio.compareTo(toleranceValue) > 0) {
      return false;
    }
    return true;
  }

  /**
   * 根据配置的原料商品到店价计算公式 计算到店价 返回null 表示不存在原料商品的到店价计算公式
   */
  private BigDecimal calRawSkuBasePrice(String tenant, String orgId, PriceAdjustmentLine line,
      List<PriceAdjustmentLine> lines)
      throws BaasException {
    if (line.getSkuDefine() != SkuDefine.SPLITBYPART_RAW) {
      throw new BaasException("商品不是原料商品，无法计算");
    }
    String skuId = line.getSku().getId();
    // 1.查询原料商品到店店价计算公式
    PriceSkuFormula formula = priceSkuFormulaService.getBySkuId(tenant, orgId, skuId);
    if (formula == null) {
      throw new BaasException("不存在商品到店价计算公式");
    }
    String dependOnSkuId = formula.getDependOnSkuId();
    PriceAdjustmentLine dependOnSkuLine = lines.stream()
        .filter(l -> l.getSku().getId().equals(dependOnSkuId))
        .findFirst()
        .orElse(null);
    if (dependOnSkuLine == null) {
      throw new BaasException("不存在商品{0}", dependOnSkuId);
    }
    // 如果依赖的商品未设置到店价，则默认为0
    if (dependOnSkuLine.getSkuBasePrice() == null
        || dependOnSkuLine.getSkuBasePrice().compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }
    PriceSkuBasePriceFormulaParam param = new PriceSkuBasePriceFormulaParam();
    param.setSkuBasePrice(dependOnSkuLine.getSkuBasePrice());
    BigDecimal basePrice = spelMgr.calculate(formula.getFormula(), param);
    return basePrice;
  }

  /**
   * 更新原料商品的到店价格
   */
  private void changeRawSkuBasePrice(String tenant, String orgId, PriceAdjustmentLine line,
      List<PriceAdjustmentLine> lines)
      throws BaasException {
    if (line.getSkuDefine() != SkuDefine.SPLITBYPART_RAW) {
      throw new BaasException("商品不是原料品，无法更新商品的的价格");
    }
    List<PriceSkuFormula> formulaList = priceSkuFormulaService.getByDependOnSkuId(tenant, orgId, line.getSku().getId());
    if (CollectionUtils.isEmpty(formulaList)) {
      log.info("不存在依赖商品{}到店价的原料品", line.getSku().getName());
      return;
    }
    for (PriceSkuFormula formula : formulaList) {
      PriceAdjustmentLine item = lines.stream()
          .filter(l -> l.getSku().getId().equals(formula.getSkuId()))
          .findFirst()
          .orElse(null);
      if (item == null) {
        continue;
      }
      PriceSkuBasePriceFormulaParam param = new PriceSkuBasePriceFormulaParam();
      param.setSkuBasePrice(line.getSkuBasePrice());
      BigDecimal basePrice = spelMgr.calculate(formula.getFormula(), param);
      item.setSkuBasePrice(basePrice);
      modifiedLines.add(item);
    }
  }

  private BigDecimal calcBasePrice(BigDecimal inPrice, BigDecimal kv, BigDecimal bv) {
    return inPrice.multiply(kv).add(bv).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  private BigDecimal calcBasePrice(BigDecimal inPrice, BigDecimal increaseRate) {
    if (increaseRate.compareTo(BigDecimal.ONE) == 0) {
      return BigDecimal.ZERO;
    }
    return inPrice.divide(BigDecimal.ONE.subtract(increaseRate), 2, BigDecimal.ROUND_HALF_UP);
  }

}
