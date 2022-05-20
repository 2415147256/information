package com.hd123.baas.sop.service.impl.group;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Component
@Slf4j
public class PriceGradeRateCalculateMgr {

  public List<PriceGradeRate> calAndGetPriceGradeRate(PriceGradeRate firstGrade, PriceGradeRate secondGrade,
      int gradeCount) throws BaasException {
    Assert.notNull(firstGrade);
    Assert.notNull(secondGrade);
    if (secondGrade.getSeq() <= firstGrade.getSeq()) {
      throw new BaasException("价格级位置错误");
    }
    BigDecimal diffValue = calEqualDiff(firstGrade, secondGrade);
    firstGrade.setMValue(calMValue(firstGrade.getIncreaseRate()));
    List<PriceGradeRate> rates = new ArrayList<>();
    int count = firstGrade.getSeq();
    for (int i = 0; i < firstGrade.getSeq(); i++) {
      PriceGradeRate rate = new PriceGradeRate();
      rate.setSeq(i);
      rate.setMValue(firstGrade.getMValue().subtract(diffValue.multiply(new BigDecimal(count--))));
      rate.setIncreaseRate(calRateByM(rate.getMValue()));
      rates.add(rate);
    }
    count = 0;
    for (int i = firstGrade.getSeq(); i < gradeCount; i++) {
      PriceGradeRate rate = new PriceGradeRate();
      rate.setSeq(i);
      rate.setMValue(firstGrade.getMValue().add(diffValue.multiply(new BigDecimal(count++))));
      rate.setIncreaseRate(calRateByM(rate.getMValue()));
      rates.add(rate);
    }
    return rates;
  }

  // 计算M值的等差
  private BigDecimal calEqualDiff(PriceGradeRate firstGrade, PriceGradeRate secondGrade) throws BaasException {
    BigDecimal firstM = calMValue(firstGrade.getIncreaseRate());
    BigDecimal secondM = calMValue(secondGrade.getIncreaseRate());
    BigDecimal diffSeq = new BigDecimal(secondGrade.getSeq() - firstGrade.getSeq());
    return secondM.subtract(firstM).divide(diffSeq, 4, RoundingMode.HALF_UP);
  }

  private BigDecimal calMValue(BigDecimal rate) throws BaasException {
    if (rate.compareTo(BigDecimal.ONE) == 0) {
      throw new BaasException("加价率不能为1");
    }
    return BigDecimal.ONE.divide(BigDecimal.ONE.subtract(rate), 4, RoundingMode.HALF_UP)
        .subtract(BigDecimal.ONE)
        .setScale(4, RoundingMode.HALF_UP);
  }

  private BigDecimal calRateByM(BigDecimal m) throws BaasException {
    if (BigDecimal.ONE.negate().compareTo(m) == 0) {
      throw new BaasException("m值不能为-1");
    }
    return BigDecimal.ONE.subtract(BigDecimal.ONE.divide(BigDecimal.ONE.add(m), 4, RoundingMode.HALF_UP))
        .setScale(4, RoundingMode.HALF_UP);
  }

  public static void main(String[] args) throws BaasException {
    PriceGradeRateCalculateMgr mgr = new PriceGradeRateCalculateMgr();
    PriceGradeRate firstGrade = new PriceGradeRate();
    firstGrade.setIncreaseRate(new BigDecimal(0));
    firstGrade.setSeq(1);
    PriceGradeRate secondGrade = new PriceGradeRate();
    secondGrade.setSeq(3);
    secondGrade.setIncreaseRate(new BigDecimal(0.5));
    List<PriceGradeRate> rates = mgr.calAndGetPriceGradeRate(firstGrade, secondGrade, 5);
    System.out.println(JsonUtil.objectToJson(rates));
  }
}
