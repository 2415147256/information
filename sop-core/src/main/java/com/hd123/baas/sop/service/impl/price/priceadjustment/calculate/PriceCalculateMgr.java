package com.hd123.baas.sop.service.impl.price.priceadjustment.calculate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.BaasPriceSkuConfig;
import com.hd123.baas.sop.service.api.entity.PUnv;
import com.hd123.baas.sop.service.api.entity.PriceRange;
import com.hd123.baas.sop.service.api.entity.SkuGradeConfig;
import com.hd123.baas.sop.service.api.entity.SkuPosition;
import com.hd123.baas.sop.service.api.grade.PriceGradeService;
import com.hd123.baas.sop.service.api.group.SkuGroupService;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceAdjustmentLine;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceGrade;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceGradeSalePrice;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRate;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseRule;
import com.hd123.baas.sop.service.api.price.priceadjustment.PriceIncreaseType;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.impl.price.PUnvToPriceIncreaseRate;
import com.hd123.baas.sop.service.impl.price.spel.SpelMgr;
import com.hd123.baas.sop.service.impl.price.spel.param.PriceFormulaParam;
import com.hd123.mpa.api.common.ObjectNodeUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Component
@Slf4j
public class PriceCalculateMgr {

  @Autowired
  private PriceGradeService priceGradeService;
  @Autowired
  private SkuGroupService skuGroupService;
  @Autowired
  private SpelMgr spelMgr;
  @Autowired
  private SysConfigService configService;
  @Autowired
  private BaasConfigClient configClient;

  public static final String[] SPECIAL_CALC_TAIL_DIFF = new String[] {
      "千克", "公斤", "KG", "kg", "Kg" };

  private static final ThreadLocal<CalculateContext> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

  public void begin(String tenant, String orgId, OperateInfo operateInfo) {
    CalculateContext context = new CalculateContext();
    context.setOperateInfo(operateInfo);
    List<PriceGrade> grades = priceGradeService.list(tenant, orgId).stream().map(g -> {
      PriceGrade grade = new PriceGrade();
      grade.setId(g.getUuid() + ""); // TODO
      grade.setName(g.getName());
      grade.setSeq(g.getSeq());
      return grade;
    }).collect(Collectors.toList());
    context.setGrades(grades);
    CONTEXT_THREAD_LOCAL.set(context);
  }

  /**
   * 售价计算，此接口仅计算售价，对于因为缺少条件，无法计算，直接忽略，不会抛异常，除非在计算过程中发生的异常
   * 
   * @param tenant
   *          租户id
   * @param line
   *          试算单
   * @param reload
   *          是否重新加载配置值
   * @throws BaasException
   */
  public void calculate(String tenant, String orgId, PriceAdjustmentLine line, boolean reload) throws BaasException {
    checkBegin();

    SysConfig sysConfig = configService.get(tenant, SysConfig.KEY_CHANGE_PRICE_BY_PROMT);
    boolean changePriceByPromt = false;
    if (sysConfig != null) {
      if (StringUtils.isNotEmpty(sysConfig.getCfValue())) {
        changePriceByPromt = Boolean.parseBoolean(sysConfig.getCfValue());
      }
    }

    BigDecimal shopPrice = line.getSkuShopPrice() != null ? line.getSkuShopPrice() : line.getSkuBasePrice();
    // 如果不联动，直接取基础到店价
    if (!changePriceByPromt) {
      shopPrice = line.getSkuBasePrice();
    }

    if (shopPrice == null) {
      return;
    }

    List<PriceGradeSalePrice> gradeSalePrices;
    if (PriceIncreaseType.FIX == line.getIncreaseType()) {
      gradeSalePrices = fixSalePrice(tenant, shopPrice, line);
    } else if (PriceIncreaseType.AMOUNT == line.getIncreaseType()) {
      gradeSalePrices = amountSalePrice(tenant, shopPrice, line);
    } else if (PriceIncreaseType.RATE == line.getIncreaseType()) {
      gradeSalePrices = rateSalePrice(tenant, shopPrice, line);
    } else if (PriceIncreaseType.EXPRESS == line.getIncreaseType()) {
      gradeSalePrices = expressSalePrice(tenant, orgId, shopPrice, line, reload);
    } else {
      gradeSalePrices = null;
    }
    // 计算尾差
    calcTailDiff(tenant, line, gradeSalePrices);

    // 堡垒商品价格限制
    if (line.getExt() != null) {
      String positionGradeId = ObjectNodeUtil.asText(line.getExt().get(PriceAdjustmentLine.Ext.POSITION_GRADE_ID));
      gradeSalePriceLimit(tenant, positionGradeId, gradeSalePrices);
    }

    // 赋值
    List<PriceGradeSalePrice> oldPriceGrades = line.getPriceGrades();
    line.setPriceGrades(gradeSalePrices);
    line.setPrePriceGrades(oldPriceGrades);
  }

  public void calcTailDiff(String tenant, PriceAdjustmentLine line, List<PriceGradeSalePrice> gradeSalePrices) {

    if (PriceIncreaseType.FIX.equals(line.getIncreaseType())) {
      log.info("加价方式为固定价，忽略，行id={}", line.getUuid());
      return;
    }

    if (gradeSalePrices == null) {
      log.info("价格级未空格，忽略，行id={}", line.getUuid());
      return;
    }

    if (line.getCalcTailDiff() != null && !line.getCalcTailDiff()) {
      log.info("未开通计算尾差，忽略，行id={}", line.getUuid());
      return;
    }

    if (Arrays.asList(SPECIAL_CALC_TAIL_DIFF).contains(line.getSku().getUnit())) {
      for (PriceGradeSalePrice gradeSalePrice : gradeSalePrices) {
        gradeSalePrice.setPrice(gradeSalePrice.getPrice().divide(new BigDecimal("2"), 4, BigDecimal.ROUND_HALF_UP));
      }
      calcTailDiffFormula(tenant, gradeSalePrices);
      for (PriceGradeSalePrice gradeSalePrice : gradeSalePrices) {
        gradeSalePrice.setPrice(gradeSalePrice.getPrice().multiply(new BigDecimal("2")));
      }
      return;
    }
    calcTailDiffFormula(tenant, gradeSalePrices);
  }

  /**
   * 将序号大于等于limitGradeId序号的售价改为一致
   * 
   * @param tenant
   * @param limitGradeId
   * @param gradeSalePrices
   */
  private void gradeSalePriceLimit(String tenant, String limitGradeId, List<PriceGradeSalePrice> gradeSalePrices) {
    if (limitGradeId == null || CollectionUtils.isEmpty(gradeSalePrices)) {
      return;
    }
    List<PriceGrade> grades = CONTEXT_THREAD_LOCAL.get().getGrades();
    PriceGrade grade = grades.stream().filter(s -> s.getId().equals(limitGradeId)).findFirst().orElse(null);
    PriceGradeSalePrice priceGradeSalePrice = gradeSalePrices.stream()
        .filter(s -> s.getGrade().getId().equals(limitGradeId))
        .findFirst()
        .orElse(null);
    if (grade == null || priceGradeSalePrice == null) {
      return;
    }
    Set<String> gs = grades.stream()
        .filter(s -> s.getSeq() > grade.getSeq())
        .map(PriceGrade::getId)
        .collect(Collectors.toSet());
    gradeSalePrices.stream().forEach(s -> {
      if (gs.contains(s.getGrade().getId())) {
        s.setPrice(priceGradeSalePrice.getPrice());
      }
    });
  }

  private void calcTailDiffFormula(String tenant, List<PriceGradeSalePrice> gradeSalePrices) {
    // 默认值
    BigDecimal arrears = new BigDecimal("0.09");
    BaasPriceSkuConfig priceSkuConfig = configClient.getConfig(tenant, BaasPriceSkuConfig.class);
    if (priceSkuConfig != null && priceSkuConfig.getArrears() > 0) {
      arrears = new BigDecimal(String.valueOf(priceSkuConfig.getArrears())).divide(new BigDecimal("100"), 2,
          BigDecimal.ROUND_HALF_UP);
    }

    for (PriceGradeSalePrice gradeSalePrice : gradeSalePrices) {
      BigDecimal price = gradeSalePrice.getPrice();
      BigDecimal newPrice = calcArrears(price, arrears);
      // log.info("计算尾差，原值={}，新值={}", price, newPrice);
      gradeSalePrice.setPrice(newPrice);
    }
  }

  public BigDecimal calcArrears(BigDecimal price, BigDecimal arrears) {
    BigDecimal leftPrice = price.setScale(0, BigDecimal.ROUND_DOWN);
    BigDecimal rightPrice = price.subtract(leftPrice);
    BigDecimal newPrice;
    if (leftPrice.compareTo(new BigDecimal("10")) < 0) {
      // 1）如果价格小于10：小数点后数值少于0.2，则小数点后数字替换为19；小数点后数值大于0.2，则将小数点后第二位数字替换为9
      // 例如1.06替换为1.19；5.62替换为5.69
      if (rightPrice.compareTo(new BigDecimal("0.2")) < 0) {
        newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.1"));
      } else {
        newPrice = price.setScale(1, BigDecimal.ROUND_DOWN);
      }
    } else if (leftPrice.compareTo(new BigDecimal("20")) < 0) {
      // 2）如果价格大于等于10，少于20：小数点后数值少于0.5，则小数点后数字替换为69；小数点后数值大于0.5，少于0.88，则将小数点后数字替换为89；小数点后数值大于0.89，少于0.99，则将小数点后数字替换为99
      // 例如10.09替换为10.69，13.76变为13.89
      if (rightPrice.compareTo(new BigDecimal("0.5")) < 0) {
        newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.6"));
      } else if (rightPrice.compareTo(new BigDecimal("0.88")) < 0) {
        newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.8"));
      } else {
        newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.9"));
      }
    } else {
      // 3）如果价格大于等于20：小数点后数值少于0.5，则小数点后数字替换为69；小数点后数值大于0.5，少于0.89，则将小数点后数字替换为89；小数点后数值大于0.89，少于0.99，则将小数点后数字替换为99。但如果小数点前第一位数字为0，则向下变为*9.99
      // 例如：20.07替换为19.99，21.07变为21.69，21.79变为21.89
      if (leftPrice.intValue() % 10 == 0) {
        // 减去1 + 0.99
        newPrice = price.subtract(new BigDecimal("1")).setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.9"));
      } else {
        if (rightPrice.compareTo(new BigDecimal("0.5")) < 0) {
          newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.6"));
        } else if (rightPrice.compareTo(new BigDecimal("0.89")) < 0) {
          newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.8"));
        } else {
          newPrice = price.setScale(0, BigDecimal.ROUND_DOWN).add(new BigDecimal("0.9"));
        }
      }
    }
    newPrice = newPrice.add(arrears);
    return newPrice;
  }

  public void end() {
    CONTEXT_THREAD_LOCAL.remove();
  }

  // 固定价
  private List<PriceGradeSalePrice> fixSalePrice(String tenant, BigDecimal shopPrice, PriceAdjustmentLine line)
      throws BaasException {
    Map<String, String> gradeRules = gradeRules(tenant, line);
    List<PriceGrade> grades = CONTEXT_THREAD_LOCAL.get().getGrades();
    List<PriceGradeSalePrice> gradeSalePrices = new ArrayList<>();
    for (PriceGrade grade : grades) {
      String value = gradeRules.get(grade.getId());
      if (value == null) {
        throw new BaasException("商品<{0}>未设置价格级<{1}>的加价规则", line.getSku().getName(), grade.getName());
      }
      BigDecimal salePrice = isAmt(value);
      if (salePrice == null) {
        throw new BaasException("商品<{0}>价格级<{1}>的固定价<{2}>设置错误", line.getSku().getName(), grade.getName(), value);
      }
      PriceGradeSalePrice gradeSalePrice = new PriceGradeSalePrice();
      gradeSalePrice.setGrade(grade);
      gradeSalePrice.setPrice(salePrice);
      gradeSalePrices.add(gradeSalePrice);
    }
    return gradeSalePrices;
  }

  // 金额加价
  private List<PriceGradeSalePrice> amountSalePrice(String tenant, BigDecimal shopPrice, PriceAdjustmentLine line)
      throws BaasException {
    Map<String, String> gradeRules = gradeRules(tenant, line);
    List<PriceGrade> grades = CONTEXT_THREAD_LOCAL.get().getGrades();
    List<PriceGradeSalePrice> gradeSalePrices = new ArrayList<>();
    for (PriceGrade grade : grades) {
      String value = gradeRules.get(grade.getId());
      if (value == null) {
        throw new BaasException("商品<{0}>未设置价格级<{1}>的加价规则", line.getSku().getName(), grade.getName());
      }
      BigDecimal amountPrice = isAmt(value);
      if (amountPrice == null) {
        throw new BaasException("商品<{0}>价格级<{1}>的金额加价<{2}>设置错误", line.getSku().getName(), grade.getName(), value);
      }
      BigDecimal salePrice = shopPrice.add(amountPrice);
      PriceGradeSalePrice gradeSalePrice = new PriceGradeSalePrice();
      gradeSalePrice.setGrade(grade);
      gradeSalePrice.setPrice(salePrice);
      gradeSalePrices.add(gradeSalePrice);
    }
    return gradeSalePrices;
  }

  // 比例加价
  private List<PriceGradeSalePrice> rateSalePrice(String tenant, BigDecimal shopPrice, PriceAdjustmentLine line)
      throws BaasException {
    Map<String, String> gradeRules = gradeRules(tenant, line);
    List<PriceGrade> grades = CONTEXT_THREAD_LOCAL.get().getGrades();
    List<PriceGradeSalePrice> gradeSalePrices = new ArrayList<>();
    for (PriceGrade grade : grades) {
      String value = gradeRules.get(grade.getId());
      if (value == null) {
        throw new BaasException("商品<{0}>未设置价格级<{1}>的加价规则", line.getSku().getName(), grade.getName());
      }
      BigDecimal rate = isAmt(value);
      if (rate == null) {
        throw new BaasException("商品<{0}>价格级<{1}>的比例加价<{2}>设置错误", line.getSku().getName(), grade.getName(), value);
      }
      BigDecimal salePrice = shopPrice.multiply(BigDecimal.ONE.add(rate)).setScale(2, BigDecimal.ROUND_HALF_UP);
      PriceGradeSalePrice gradeSalePrice = new PriceGradeSalePrice();
      gradeSalePrice.setGrade(grade);
      gradeSalePrice.setPrice(salePrice);
      gradeSalePrices.add(gradeSalePrice);
    }
    return gradeSalePrices;
  }

  // 公式
  private List<PriceGradeSalePrice> expressSalePrice(String tenant, String orgId, BigDecimal shopPrice,
      PriceAdjustmentLine line,
      boolean reload) throws BaasException {
    Map<String, String> gradeRules = gradeRules(tenant, line);
    List<PriceGrade> grades = CONTEXT_THREAD_LOCAL.get().getGrades();
    List<PriceGradeSalePrice> gradeSalePrices = new ArrayList<>();

    // 商品定位价格级加价率
    if (CollectionUtils.isEmpty(line.getSkuPositionIncreaseRates()) || reload) {
      GroupPositionIncreaseRate positionIncreaseRate = positionIncreaseRate(tenant, line.getSkuGroup(),
          line.getSkuPosition());
      if (positionIncreaseRate == null) {
        throw new BaasException("商品<{0}>类目<{1}>及定位<{2}>没有维护加价率", line.getSku().getName(), line.getSkuGroupName(),
            line.getSkuPositionName());
      }
      line.setSkuPositionIncreaseRates(positionIncreaseRate.getIncreaseRates());
    }
    // 商品价格级加价率
    if (CollectionUtils.isEmpty(line.getSkuGradeIncreaseRates()) || reload) {
      SkuIncreaseRate skuGradeIncreaseRate = skuIncreaseRate(tenant, orgId, line.getSku().getId());
      line.setSkuGradeIncreaseRates(skuGradeIncreaseRate == null ? null : skuGradeIncreaseRate.getIncreaseRates());
    }
    // 商品价格带价格级加价率
    if (CollectionUtils.isEmpty(line.getPriceRangeIncreaseRates()) || reload) {
      GroupRangeIncreaseRate rangeIncreaseRate = rangeIncreaseRate(tenant, line.getSkuGroup(), shopPrice);
      if (rangeIncreaseRate == null) {
        throw new BaasException("商品<{0}>类目<{1}>及价格<{2}>对应的价格带没有维护加价率", line.getSku().getName(), line.getSkuGroupName(),
            shopPrice);
      }
      line.setPriceRangeIncreaseRates(rangeIncreaseRate.getIncreaseRates());
    }

    for (PriceGrade grade : grades) {
      String value = gradeRules.get(grade.getId());
      if (value == null) {
        throw new BaasException("商品<{0}>价格级<{1}>未设置加价规则", line.getSku().getName(), grade.getName());
      }

      PriceIncreaseRate positionCurrGradeIncreaseRate = line.getSkuPositionIncreaseRates()
          .stream()
          .filter(r -> grade.equals(r.getGrade()))
          .findFirst()
          .orElse(null);
      if (positionCurrGradeIncreaseRate == null) {
        throw new BaasException("商品<{0}>类目<{1}>及定位<{2}>对应的价格级<{3}>没有维护加价率", line.getSku().getName(),
            line.getSkuGroupName(), line.getSkuPositionName(), grade.getName());
      }
      PriceIncreaseRate rangeCurrGradeIncreaseRate = line.getPriceRangeIncreaseRates()
          .stream()
          .filter(r -> grade.equals(r.getGrade()))
          .findFirst()
          .orElse(null);
      if (rangeCurrGradeIncreaseRate == null) {
        throw new BaasException("商品<{0}>类目<{1}>及价格<{2}>所属价格带对应的价格级<{3}>没有维护价格级加价率", line.getSku().getName(),
            line.getSkuGroupName(), shopPrice, grade.getName());
      }

      BigDecimal skuGradeIncreaseRateValue = BigDecimal.ZERO;
      if (line.getSkuGradeIncreaseRates() != null) {
        PriceIncreaseRate skuGradeIncreaseRate = line.getSkuGradeIncreaseRates()
            .stream()
            .filter(r -> grade.equals(r.getGrade()))
            .findFirst()
            .orElse(null);
        skuGradeIncreaseRateValue = skuGradeIncreaseRate != null ? skuGradeIncreaseRate.getRate() : BigDecimal.ZERO;
      }

      PriceFormulaParam param = new PriceFormulaParam();
      param.setShopPrice(shopPrice);
      param.setSkuPositionIncreaseRate(positionCurrGradeIncreaseRate.getRate());
      param.setPriceRangeIncreaseRate(rangeCurrGradeIncreaseRate.getRate());
      param.setSkuIncreaseRate(skuGradeIncreaseRateValue);
      BigDecimal salePrice = spelMgr.calculate(value, param);

      PriceGradeSalePrice gradeSalePrice = new PriceGradeSalePrice();
      gradeSalePrice.setGrade(grade);
      gradeSalePrice.setPrice(salePrice);
      gradeSalePrices.add(gradeSalePrice);
    }
    return gradeSalePrices;
  }

  private void checkBegin() {
    Assert.notNull(CONTEXT_THREAD_LOCAL.get(), "context");
    Assert.notNull(CONTEXT_THREAD_LOCAL.get().getGrades(), "context.grades");
  }

  private GroupPositionIncreaseRate positionIncreaseRate(String tenant, String skuGroup, String position) {
    List<GroupPositionIncreaseRate> positionIncreaseRates = CONTEXT_THREAD_LOCAL.get()
        .getPositionIncreaseRateMap()
        .get(skuGroup);
    if (positionIncreaseRates == null) {
      List<SkuPosition> positions = skuGroupService.gradeByAllPositionList(tenant, skuGroup);
      if (positions != null) {
        positionIncreaseRates = positions.stream().map(p -> {
          GroupPositionIncreaseRate rate = new GroupPositionIncreaseRate();
          rate.setSkuPosition(p.getUuid() + ""); // TODO
          rate.setIncreaseRates(p.getPriceGrades().stream().map(this::convert).collect(Collectors.toList()));
          return rate;
        }).collect(Collectors.toList());
      }
    }

    if (positionIncreaseRates == null) {
      return null;
    }
    CONTEXT_THREAD_LOCAL.get().getPositionIncreaseRateMap().put(skuGroup, positionIncreaseRates);
    return positionIncreaseRates.stream().filter(p -> p.getSkuPosition().equals(position)).findFirst().orElse(null);
  }

  private PriceIncreaseRate convert(PUnv pUnv) {
    return new PUnvToPriceIncreaseRate().convert(pUnv);
  }

  private SkuIncreaseRate skuIncreaseRate(String tenant, String orgId, String skuId) {
    List<SkuIncreaseRate> skuIncreaseRates = CONTEXT_THREAD_LOCAL.get().getSkuIncreaseRateList();
    if (skuIncreaseRates == null) {
      List<SkuGradeConfig> skuGradeConfigs = skuGroupService.skuGradeConfigList(tenant, orgId);
      if (skuGradeConfigs != null) {
        skuIncreaseRates = skuGradeConfigs.stream().map(p -> {
          SkuIncreaseRate rate = new SkuIncreaseRate();
          rate.setSkuId(skuId);
          List<PUnv> priceGrades = JSON.parseArray(p.getPriceGradeJson(), PUnv.class);
          if (priceGrades != null) {
            List<PriceIncreaseRate> gradeRates = priceGrades.stream().map(this::convert).collect(Collectors.toList());

            rate.setIncreaseRates(gradeRates);
          }
          return rate;
        }).collect(Collectors.toList());
      }
    }
    if (skuIncreaseRates == null) {
      return null;
    }
    CONTEXT_THREAD_LOCAL.get().setSkuIncreaseRateList(skuIncreaseRates);
    return skuIncreaseRates.stream().filter(s -> s.getSkuId().equals(skuId)).findFirst().orElse(null);
  }

  private GroupRangeIncreaseRate rangeIncreaseRate(String tenant, String skuGroup, BigDecimal shopPrice) {
    List<GroupRangeIncreaseRate> rangeIncreaseRates = CONTEXT_THREAD_LOCAL.get()
        .getRangeIncreaseRateMap()
        .get(skuGroup);
    if (rangeIncreaseRates == null) {
      List<PriceRange> priceRanges = skuGroupService.gradeByAllRangeList(tenant, skuGroup);
      if (priceRanges != null) {
        rangeIncreaseRates = priceRanges.stream().map(p -> {
          GroupRangeIncreaseRate rate = new GroupRangeIncreaseRate();
          rate.setPriceRange(p.getUuid() + ""); // TODO
          rate.setAmount(new BigDecimal(p.getName()));
          rate.setIncreaseRates(p.getPriceGrades().stream().map(this::convert).collect(Collectors.toList()));
          return rate;
        }).collect(Collectors.toList());
      }
    }

    if (rangeIncreaseRates == null) {
      return null;
    }
    CONTEXT_THREAD_LOCAL.get().getRangeIncreaseRateMap().put(skuGroup, rangeIncreaseRates);
    if (rangeIncreaseRates.isEmpty()) {
      return null;
    }
    GroupRangeIncreaseRate result = null;
    for (GroupRangeIncreaseRate increaseRate : rangeIncreaseRates) {
      if (increaseRate.getAmount().compareTo(shopPrice) > 0) {
        break;
      }
      result = increaseRate;
    }
    return result;
  }

  private Map<String, String> gradeRules(String tenant, PriceAdjustmentLine line) {
    Map<String, String> gradeRules = new HashMap<>();
    for (PriceIncreaseRule rule : line.getIncreaseRules()) {
      for (PriceGrade grade : rule.getGrades()) {
        gradeRules.put(grade.getId(), rule.getValue());
      }
    }
    return gradeRules;
  }

  private BigDecimal isAmt(String value) {
    try {
      return new BigDecimal(value);
    } catch (Throwable e) {
      return null;
    }
  }

  public static void main(String[] args) {
    BigDecimal a = new BigDecimal("0.09");
    BigDecimal a1 = new BigDecimal("1.06");
    BigDecimal a2 = new BigDecimal("5.62");
    BigDecimal a3 = new BigDecimal("10.09");
    BigDecimal a4 = new BigDecimal("13.76");
    BigDecimal a5 = new BigDecimal("20.07");
    BigDecimal a6 = new BigDecimal("21.07");
    BigDecimal a7 = new BigDecimal("21.79");
    // System.out.println(a1 + "==>" + calcArrears(a1, a));
    // System.out.println(a2 + "==>" + calcArrears(a2, a));
    // System.out.println(a3 + "==>" + calcArrears(a3, a));
    // System.out.println(a4 + "==>" + calcArrears(a4, a));
    // System.out.println(a5 + "==>" + calcArrears(a5, a));
    // System.out.println(a6 + "==>" + calcArrears(a6, a));
    // System.out.println(a7 + "==>" + calcArrears(a7, a));
  }
}
