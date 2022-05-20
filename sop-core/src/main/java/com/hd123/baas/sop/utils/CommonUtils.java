package com.hd123.baas.sop.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.qianfan123.baas.common.http.FilterParam;

public class CommonUtils {
  public static boolean isInteger(String str) {
    if (StringUtils.isEmpty(str)) {
      return false;
    }
    Pattern pattern = Pattern.compile("^[0-9]*[0-9][0-9]*$");
    return pattern.matcher(str).matches();
  }

  /**
   * 从对象集合中取出某个字段的集合
   *
   */
  public static <P, S> List<S> extractField(Collection<P> entities, Function<P, S> function) {
    return entities.stream().map(function).collect(Collectors.toList());
  }

  /**
   * 从对象集合中取出某个字段的集合
   *
   */
  public static <P, S> Set<S> extractFieldToSet(Collection<P> entities, Function<P, S> function) {
    return entities.stream().map(function).collect(Collectors.toSet());
  }

  /**
   * 过滤
   */
  public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
    return list.stream().filter(predicate).collect(Collectors.toList());
  }

  /**
   * 过滤
   */
  public static <T> Optional<T> findFirst(List<T> list, Predicate<? super T> predicate) {
    return list.stream().filter(predicate).collect(Collectors.toList()).stream().findFirst();
  }

  /**
   * 字段映射，字段必须唯一
   */
  public static <K, T> Map<K, T> toMap(List<T> entities, Function<T, K> function) {
    return entities.stream().collect(Collectors.toMap(function, a -> a));
  }

  /**
   * 分组
   */
  public static <K, T> Map<K, List<T>> groupBy(List<T> entities, Function<T, K> function) {
    return entities.stream().collect(Collectors.groupingBy(function));
  }

  public static String findValueAsString(List<FilterParam> filters, String property) {
    if (CollectionUtils.isEmpty(filters)) {
      return null;
    }

    for (FilterParam filter : filters) {
      if (Objects.equals(filter.getProperty(), property)) {
        return filter.getValue() == null ? null : filter.getValue().toString();
      }
    }
    return null;
  }

  public static Object[] findAsArray(List<FilterParam> filters, String property) {
    if (CollectionUtils.isEmpty(filters)) {
      return ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    List result = new ArrayList<>();
    for (FilterParam param : filters) {
      if (!StringUtils.equalsIgnoreCase(param.getProperty(), property)) {
        continue;
      }

      if (param.getValue() == null) {
        continue;
      }

      if (param.getValue() instanceof Collection) {
        result.addAll((Collection) param.getValue());
        continue;
      }

      if (StringUtils.isBlank(param.getValue().toString())) {
        continue;
      }

      result.add(param.getValue());
    }

    return result.toArray();
  }

  /**
   * 两个BigDecimal类型的对象是否值相等，参数可为null
   *
   * <pre>
   * valueEquals(new BigDecimal("1"), new BigDecimal("1"))  = true
   * valueEquals(new BigDecimal("1"), new BigDecimal("2"))  = false
   * valueEquals(null, new BigDecimal("2"))                 = false
   * valueEquals(null, null)                                = true
   * </pre>
   *
   */
  public static boolean valueEquals(BigDecimal d1, BigDecimal d2) {
    if (d1 == d2) {
      return true;
    }
    if (d1 != null && d2 != null) {
      return d1.compareTo(d2) == 0;
    }

    return false;

  }

}
