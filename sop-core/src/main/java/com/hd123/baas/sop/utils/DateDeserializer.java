/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	swallows-web
 * 文件名：	DateDeserializer.java
 * 模块说明：	
 * 修改历史：
 * 2018年5月8日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * @author huangjunxian
 *
 */
public class DateDeserializer extends DateDeserializers.DateDeserializer {

  @Override
  public Date deserialize(JsonParser jp, DeserializationContext ctxt)
      throws IOException, JsonProcessingException {
    if (_customFormat != null) {
      return _parseDate(jp, ctxt);
    }
    ObjectCodec codec = jp.getCodec();
    JsonNode tree = codec.readTree(jp);
    if (tree instanceof TextNode) {
      String dateString = tree.textValue();
      if (StringUtils.isBlank(dateString)) {
        return null;
      }
      try {
        return DateUtils.parseDate(dateString.replaceAll("Z$", "+0000"), new String[] {
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd" });
      } catch (ParseException e) {
        throw new IOException(e);
      }
    } else if (tree instanceof LongNode) {
      return new Date(tree.longValue());
    } else {
      throw new IllegalArgumentException("Unknow Tree Node.");
    }
  }

}
