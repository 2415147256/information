package com.hd123.baas.sop.service.impl.price.spel;

import java.math.BigDecimal;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.spel.support.StandardOperatorOverloader;
import org.springframework.integration.json.JsonPropertyAccessor;

/**
 * @author zhengzewang on 2019/12/18.
 */
public class JsonNodeOperatorOverloader extends StandardOperatorOverloader {
  @Override
  public boolean overridesOperation(Operation operation, Object o, Object o1) throws EvaluationException {
    if (o instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode
        && o1 instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode) {
      return true;
    }
    if (SpelFunction.isNumber(o) && SpelFunction.isNumber(o1)) {
      return true;
    }
    return false;
  }

  @Override
  public Object operate(Operation operation, Object o, Object o1) throws EvaluationException {
    BigDecimal b1 = new BigDecimal(o.toString());
    BigDecimal b2 = new BigDecimal(o1.toString());
    if (operation == Operation.ADD) {
      return b1.add(b2);
    }
    if (operation == Operation.SUBTRACT) {
      return b1.subtract(b2);
    }
    if (operation == Operation.MULTIPLY) {
      return b1.multiply(b2);
    }
    if (operation == Operation.DIVIDE) {
      return b1.divide(b2);
    }
    if (operation == Operation.POWER) {
      if (b2.compareTo(b2.setScale(0, BigDecimal.ROUND_DOWN)) == 0) {
        return b1.pow(b2.intValue());
      }
    }
    return super.operate(operation, o, o1);
  }
}
