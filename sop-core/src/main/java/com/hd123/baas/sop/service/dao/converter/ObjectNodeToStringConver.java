package com.hd123.baas.sop.service.dao.converter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

import java.util.Objects;

/**
 * ObjectNode -> Ext 转换器
 *
 * @author liuhaoxin
 * @date 2021-12-1
 */
public class ObjectNodeToStringConver implements Converter<ObjectNode, String> {

  @Override
  public String convert(ObjectNode source) throws ConversionException {
    if (Objects.isNull(source)) {
      return null;
    }
    return source.toString();
  }
}
