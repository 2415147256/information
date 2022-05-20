package com.hd123.baas.sop.service.impl.price.spel;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.integration.json.JsonPropertyAccessor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author zhengzewang on 2019/12/18.
 */
public class SpelFunction {

  private static final int DEFAULT_SCALE = 2;

  public static BigDecimal neg(Object value) {
    if (isNull(value)) {
      return null;
    }
    return new BigDecimal(value.toString()).negate();
  }

  public static BigDecimal abs(Object value) {
    if (isNull(value)) {
      return null;
    }
    return new BigDecimal(value.toString()).abs();
  }

  public static Object con(Object condition, Object value1, Object value2) {
    boolean b = false;
    if (isBoolean(condition)) {
      b = new Boolean(condition.toString());
    }
    if (b) {
      return value1;
    } else {
      return value2;
    }
  }

  public static boolean or(Object... values) {
    if (values == null || values.length == 0) {
      return false;
    }
    for (Object value : values) {
      if (isBoolean(value) && new Boolean(value.toString())) {
        // 有一个为true即返回true
        return true;
      }
    }
    return false;
  }

  public static boolean and(Object... values) {
    if (values == null || values.length == 0) {
      return false;
    }
    for (Object value : values) {
      if (!isBoolean(value)) {
        return false;
      }
      if (!new Boolean(value.toString())) {
        return false;
      }
    }
    return true;
  }

  public static String concat(Object... values) {
    if (values == null || values.length == 0) {
      return null;
    }
    StringBuilder sb = new StringBuilder();
    for (Object value : values) {
      if (isNull(value)) {
        sb.append("null");
      } else {
        sb.append(value.toString());
      }
    }
    return sb.toString();
  }

  public static boolean isTrue(Object value) {
    if (isBoolean(value)) {
      return new Boolean(value.toString());
    }
    return false;
  }

  public static boolean equals(Object value1, Object value2) {
    if (isNull(value1)) {
      return isNull(value2);
    }
    if (isNull(value2)) {
      return false;
    }

    boolean b1 = isNumber(value1);
    boolean b2 = isNumber(value2);
    if (b1 || b2) { // 其中有一个是数字
      if (!b1 || !b2) {
        // 其中有一个不是数字
        return false;
      }
      return new BigDecimal(value1.toString()).compareTo(new BigDecimal(value2.toString())) == 0;
    }

    b1 = isBoolean(value1);
    b2 = isBoolean(value2);
    if (b1 || b2) {
      if (!b1 || !b2) {
        return false;
      }
      return new Boolean(value1.toString()).equals(new Boolean(value2.toString()));
    }

    // TODO 数组等其他复杂对象暂不考虑

    String v1 = value1.toString();
    String v2 = value2.toString();
    return StringUtils.equals(v1, v2);
  }

  public static int count(List<Object> values) {
    if (values == null) {
      return 0;
    }
    return values.size();
  }

  // 取和
  public static String sum(List<Object> values) {
    if (values == null) {
      return null;
    }
    BigDecimal bigDecimal = BigDecimal.ZERO;
    for (Object value : values) {
      if (isNull(value)) {
        continue;
      }
      bigDecimal = bigDecimal.add(new BigDecimal(value.toString()));
    }
    return bigDecimal.toString();
  }

  // 格式化时间
  public static String fd(Object value, String format) throws ParseException {
    if (isNull(value)) {
      return null;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = simpleDateFormat.parse(value.toString());
    SimpleDateFormat fdFormat = new SimpleDateFormat(format);
    return fdFormat.format(date);
  }

  public static String money(Object value, String... args) {
    return getAmountString(value, true, args);
  }

  public static String amount(Object value, String... args) {
    return getAmountString(value, false, args);
  }

  /**
   * 
   * @param args
   * 
   *          args 0 : scale
   * 
   *          arg 1 : roundingMode
   * 
   * @return
   */
  private static String getAmountString(Object value, boolean money, String... args) {
    if (isNull(value)) {
      return null;
    }
    int scale = DEFAULT_SCALE;
    int roundingMode = BigDecimal.ROUND_HALF_UP;
    if (args != null) {
      if (args.length >= 1) {
        scale = Integer.parseInt(args[0]);
      }
      if (args.length >= 2) {
        roundingMode = Integer.parseInt(args[1]);
      }
    }
    BigDecimal bigDecimal = new BigDecimal(value.toString());
    boolean negative = bigDecimal.compareTo(BigDecimal.ZERO) < 0;
    StringBuilder sb = new StringBuilder();
    if (negative) {
      sb.append("-");
    }
    if (money) {
      sb.append("￥");
    }
    if (negative) {
      bigDecimal = BigDecimal.ZERO.subtract(bigDecimal);
    }
    sb.append(bigDecimal.setScale(scale, roundingMode));
    return sb.toString();
  }

  public static boolean isNull(Object value) {
    if (value == null) {
      return true;
    }
    if (value instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode) {
      JsonPropertyAccessor.ToStringFriendlyJsonNode jsonNode = (JsonPropertyAccessor.ToStringFriendlyJsonNode) value;
      JsonNode node = jsonNode.getTarget();
      return node.isNull();
    }
    return false;
  }

  public static boolean isNumber(Object value) {
    if (value == null) {
      return false;
    }
    if (value instanceof Number) {
      return true;
    }
    if (value instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode) {
      JsonPropertyAccessor.ToStringFriendlyJsonNode jsonNode = (JsonPropertyAccessor.ToStringFriendlyJsonNode) value;
      JsonNode node = jsonNode.getTarget();
      return node.isNumber();
    }
    return false;
  }

  public static boolean isBoolean(Object value) {
    if (value == null) {
      return false;
    }
    if (value instanceof Boolean || boolean.class.isAssignableFrom(value.getClass())) {
      return true;
    }
    if (value instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode) {
      JsonPropertyAccessor.ToStringFriendlyJsonNode jsonNode = (JsonPropertyAccessor.ToStringFriendlyJsonNode) value;
      JsonNode node = jsonNode.getTarget();
      return node.isBoolean();
    }
    return false;
  }

  private static boolean isArray(Object value) {
    if (value == null) {
      return false;
    }
    if (value.getClass().isArray()) {
      return true;
    }
    if (value instanceof JsonPropertyAccessor.ToStringFriendlyJsonNode) {
      JsonPropertyAccessor.ToStringFriendlyJsonNode jsonNode = (JsonPropertyAccessor.ToStringFriendlyJsonNode) value;
      JsonNode node = jsonNode.getTarget();
      return node.isArray();
    }
    return false;
  }

}
