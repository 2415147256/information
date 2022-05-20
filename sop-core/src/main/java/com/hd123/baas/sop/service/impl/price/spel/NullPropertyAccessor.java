package com.hd123.baas.sop.service.impl.price.spel;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;

/**
 * @author zhengzewang on 2020/2/13.
 */
public class NullPropertyAccessor implements PropertyAccessor {

  @Override
  public Class<?>[] getSpecificTargetClasses() {
    return null;
  }

  @Override
  public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
    return true;
  }

  @Override
  public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
    return TypedValue.NULL;
  }

  @Override
  public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
    return false;
  }

  @Override
  public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {

  }
}
