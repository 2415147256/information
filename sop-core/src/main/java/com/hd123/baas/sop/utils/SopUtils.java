package com.hd123.baas.sop.utils;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;

/**
 * @author zhengzewang on 2020/11/25.
 */
public class SopUtils {

  public static OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }

  public static String toString(BigDecimal bigDecimal) {
    if (bigDecimal == null) {
      return null;
    } else {
      return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }
  }

  public static String convert(Blob blob) {
    if (blob != null) {
      try {
        InputStream is = blob.getBinaryStream();
        byte[] b = new byte[is.available()];
        is.read(b, 0, b.length);
        return new String(b, "UTF-8");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }
}
