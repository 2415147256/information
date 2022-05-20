package com.hd123.baas.sop.service.dao.sku.publishplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

/**
 * Ext-> ObjectNode 转换器
 *
 * @author liuhaoxin
 * @date 2021-12-1
 */
public class ExtToObjectNodeConver implements Converter<String, ObjectNode> {
  @SneakyThrows
  @Override
  public ObjectNode convert(String source) throws ConversionException {
    if (StringUtils.isBlank(source)) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    ObjectNode ext = (ObjectNode) mapper.readTree(source);
    return ext;
  }
}
