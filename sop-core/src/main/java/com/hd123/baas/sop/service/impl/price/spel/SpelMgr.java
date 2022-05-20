package com.hd123.baas.sop.service.impl.price.spel;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.json.JsonPropertyAccessor;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.impl.price.spel.param.PriceFormulaParam;

/**
 * @author zhengzewang on 2020/11/23.
 */
@Component
@Slf4j
public class SpelMgr {

  public static final String MULTIPLY = "×";
  public static final String DIVIDE = "÷";

  private class ExpressionObject {
    private ExpressionParser parser;
    private StandardEvaluationContext ctx;
  }

  private ExpressionObject parser(Map<String, Object> map) {
    SpelParserConfiguration config = new SpelParserConfiguration(true, true);
    ExpressionParser parser = new SpelExpressionParser(config);
    StandardEvaluationContext ctx = new StandardEvaluationContext();
    ctx.setVariables(map);
    ctx.addPropertyAccessor(new JsonPropertyAccessor());
    ctx.getPropertyAccessors().add(new NullPropertyAccessor()); // after default
    // ctx.addPropertyAccessor(new NullPropertyAccessor());
    ctx.setOperatorOverloader(new JsonNodeOperatorOverloader());

    ExpressionObject object = new ExpressionObject();
    object.parser = parser;
    object.ctx = ctx;
    return object;
  }

  /**
   * 公式
   * 
   * @param formula
   *          公式
   * @param param
   *          参数
   * @return 结果
   */
  public BigDecimal calculate(String formula, Object param) {
    Map<String, Object> map = new HashMap<>();
    Class cls = param.getClass();
    Field[] fields = cls.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      Object value;
      try {
        value = field.get(param);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
      String fieldName = field.getName();

      map.put(fieldName, value);
      formula = formula.replace(fieldName, "#" + fieldName);

      String fieldAlias = null;
      FormulaValue formulaValue = field.getAnnotation(FormulaValue.class);
      if (formulaValue != null) {
        fieldAlias = formulaValue.value();
      }
      if (StringUtils.isNotBlank(fieldAlias) && !fieldAlias.equals(fieldName)) {
        formula = formula.replace(fieldAlias, "#" + fieldName);
      }
    }
    formula = formula.replace(MULTIPLY, "*");
    formula = formula.replace(DIVIDE, "/");
    try {
      ExpressionObject object = parser(map);
      Object value = object.parser.parseExpression(formula).getValue(object.ctx);
      return new BigDecimal(value.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
    } catch (Exception ex) {
      log.info("参数，formula={},param={}", formula, param);
      throw ex;
    }
  }

  public static void main(String[] args) throws IllegalAccessException {
    SpelMgr spel = new SpelMgr();
    PriceFormulaParam param = new PriceFormulaParam();
    param.setShopPrice(new BigDecimal(10));
    param.setInPrice(new BigDecimal(2));
    param.setSkuPositionIncreaseRate(new BigDecimal(0.1));
    param.setPriceRangeIncreaseRate(new BigDecimal(0.11));
    String formula = " 到店价÷ (1-商品定位加价率)";
    BigDecimal result = spel.calculate(formula, param);
    System.out.println(result);

    SpelMgr spelMgr = new SpelMgr();
    Map<String,Object> map = new HashMap<>();
    map.put("skuId",new BigDecimal(1));
    ExpressionObject expressionObject = spelMgr.parser(map);
    Object value = expressionObject.parser.parseExpression("#skuId+1").getValue(expressionObject.ctx);
    System.out.println(value);

  }

}
