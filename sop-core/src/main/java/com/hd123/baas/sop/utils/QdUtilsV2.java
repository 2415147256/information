package com.hd123.baas.sop.utils;

import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http.SortParam;
import com.qianfan123.baas.common.util.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 提供界面条件转换成QueryDefinition的工具
 *
 * @author W.J.H.7
 * @since 1.14.0
 **/
public class QdUtilsV2 {
  private static Logger logger = getLogger(QdUtilsV2.class);
  private Map<String, ParamConverter> converters = new HashMap<>();

  /***
   * 将界面条件转换成QueryDefinition
   *
   * @param query
   *          界面传入的查询条件
   * @param clazz
   * @return qd
   */
  public QueryDefinition toQd(QueryRequest query, Class clazz) {
    QueryDefinition qd = new QueryDefinition();
    if (query == null) {
      return qd;
    }
    Assert.notNull(clazz, "clazz");

    Integer start = null != query.getStart() ? query.getStart() : 0;
    qd.setPage(query.getLimit() == 0 ? 0 : start / query.getLimit());
    qd.setPageSize(query.getLimit());

    Class queries = getQueryClass(clazz);

    // 过滤条件
    try {
      initFilter(qd, query, queries);
    } catch (Exception e) {
      logger.error("转换查询条件失败", e);
      throw new RuntimeException("转换查询条件失败: " + e.getMessage());
    }
    // 排序条件
    try {
      initSort(qd, query, queries);
    } catch (Exception e) {
      logger.error("转换排序条件失败", e);
      throw new RuntimeException("转换排序条件失败: " + e.getMessage());
    }
    return qd;
  }

  private void initFilter(QueryDefinition qd, QueryRequest query, Class queries)
      throws InstantiationException, IllegalAccessException {
    for (FilterParam param : query.getFilters()) {
      String property = param.getProperty();
      Object value = param.getValue();
      if (StringUtils.isBlank(property)) {
        continue;
      }
      String[] s = property.split(":");
      if (s.length == 2) {
        // QueryField
        String field = getField(queries, s[0]);
        String operator = getOperator(s[1]);
        if ((value == null || StringUtils.isBlank(value.toString()))
            && !(Cop.IS_NULL.equals(operator) || Cop.not(Cop.IS_NULL).equals(operator))) {
          // 参数为空的不处理
          continue;
        }
        if (value instanceof Collection && CollectionUtils.isEmpty((Collection) value)) {
          continue;
        }
        if (StringUtils.isBlank(field)) {
          // QueryOperation
          field = getField(queries, s[0], s[1]);
          if (StringUtils.isBlank(field)) {
            throw new RuntimeException("不支持的查询条件");
          }

          Object[] params = paramsConverter(property, operator, value);
          qd.addByOperation(field, params);
        } else {
          Object[] params = paramsConverter(property, operator, value);
          qd.addByField(field, operator, params);
        }
      } else {
        throw new RuntimeException("不支持的查询条件");
      }
    }
  }

  private Object[] paramsConverter(String property, String operator, Object value) {
    ParamConverter converter = converters.get(property);
    if (converter != null) {
      return converter.converter(property, value);
    }
    if (value instanceof Object[]) {
      return (Object[]) value;
    }
    if (Cop.IN.equals(operator) || "notIn".equals(operator) || "!in".equals(operator)) {
      try {
        return new StringArrayConverter().converter(property, value);
      } catch (Exception e) {
        return new Object[] {
            value };
      }
    }
    // 日期特殊处理
    if (property.equals("created:[,]") || property.endsWith("Date:[,]")) {
      try {
        return new DateBtwConverter().converter(property, value);
      } catch (Exception e) {
        return new Object[] {
            value };
      }
    }

    // 日期特殊处理
    if (property.startsWith("lastModified:") && value instanceof String && value != null) {
      try {
        value = Converter.toDate(value.toString());
      } catch (Exception e) {
        return new Object[] {
            value };
      }
    }
    return new Object[] {
        value };
  }

  private void initSort(QueryDefinition qd, QueryRequest query, Class queries)
      throws InstantiationException, IllegalAccessException {

    for (SortParam sp : query.getSorters()) {
      String property = sp.getProperty();
      if (StringUtils.isBlank(property)) {
        continue;
      }
      // QueryField
      String field = getField(queries, property, null);
      if (StringUtils.isBlank(field)) {
        if (property.toLowerCase().contains("sum(") || property.toLowerCase().contains("avg(")
            || property.toLowerCase().contains("count(")) {
          field = property;
        }
        if (field == null) {
          continue;
        }
      }
      qd.addOrder(field, QueryOrderDirection.valueOf(sp.getDirection().toLowerCase()));
    }

  }

  private String getOperator(String operator) {
    if (operator.contains(Cop.NOT)) {
      return Cop.not(operator.replace(Cop.NOT, ""));
    }
    if ("notIn".equals(operator)) {
      return Cop.not(Cop.IN);
    }
    return operator;
  }

  private String getField(Class queries, String field) throws IllegalAccessException, InstantiationException {
    Assert.notNull(field, "field");

    for (Field f : queries.getFields()) {
      if ("PREFIX".equals(f.getName())) {
        continue;
      }

      // QueryField
      if (f.getName().replaceAll("_", "").equalsIgnoreCase(field.replaceAll(" ", ""))) {
        return (String) f.get(queries.newInstance());
      }

    }

    return null;
  }

  private String getField(Class queries, String field, String operator)
      throws IllegalAccessException, InstantiationException {
    Assert.notNull(field, "field");

    for (Field f : queries.getFields()) {
      if ("PREFIX".equals(f.getName())) {
        continue;
      }

      // QueryField
      if (f.getName().replaceAll("_", "").equalsIgnoreCase(field.replaceAll(" ", ""))) {
        return (String) f.get(queries.newInstance());
      }

      // QueryOperation
      if (StringUtils.isNotBlank(operator)) {
        String newField = field;
        if (Cop.LIKES.equals(operator)) {
          newField = field + " " + "like";
        } else if (Cop.EQUALS.equals(operator)) {
          newField = field + " " + "equals";
        } else if (Cop.IN.equals(operator)) {
          newField = field + " " + "in";
        } else if ("notIn".equals(operator)) {
          newField = field + " " + "notIn";
        } else if ("!=".equals(operator)) {
          newField = field + " " + "not equals";
        } else if ("[,]".equals(operator)) {
          newField = field + " " + "btw";
        } else if ("exist".equals(operator)) {
          newField = field + " " + "exist";
        }
        if (f.getName().replaceAll("_", "").equalsIgnoreCase(newField.replaceAll(" ", ""))) {
          return (String) f.get(queries.newInstance());
        }
      }
    }

    return null;
  }

  private String getPrefix(Class queries) throws IllegalAccessException, InstantiationException {
    String prefix = "";

    for (Field f : queries.getDeclaredFields()) {
      if ("PREFIX".equals(f.getName())) {
        f.setAccessible(true);
        prefix = (String) f.get(queries.newInstance());
        break;
      }
    }
    return prefix;
  }

  private Class getQueryClass(Class clazz) {
    Class[] classes = clazz.getDeclaredClasses();
    if (classes == null || classes.length == 0) {
      throw new RuntimeException("不支持的Class：" + clazz.getName());
    }
    Class queries = null;
    for (Class c : classes) {
      if ("Queries".equals(c.getSimpleName())) {
        queries = c;
        break;
      }
    }
    if (queries == null) {
      throw new RuntimeException("不支持的Class：" + clazz.getName());
    }
    return queries;
  }

  public QdUtilsV2 mapParam(String property, ParamConverter converter) {
    this.converters.put(property, converter);
    return this;
  }

  public interface ParamConverter<T> {
    T[] converter(String property, Object value);
  }

  static class StringArrayConverter implements ParamConverter<String> {
    @Override
    public String[] converter(String property, Object value) {
      if (value == null) {
        return null;
      }
      List<String> values = ((List<String>) value);
      return values.toArray(new String[] {});
    }
  }

  static class IntBtwConverter implements ParamConverter<Integer> {
    @Override
    public Integer[] converter(String property, Object value) {
      if (value == null) {
        return null;
      }
      List<Integer> values = ((List<Integer>) value);
      Integer start = null;
      if (values.get(0) != null && StringUtils.isNotBlank(values.get(0) + "")) {
        start = new Integer(values.get(0) + "");
      }
      Integer end = null;
      if (values.get(1) != null && StringUtils.isNotBlank(values.get(1) + "")) {
        end = new Integer(values.get(1) + "");
      }
      return new Integer[] {
          start, end };
    }
  }

  static class BigDecimalBtwConverter implements ParamConverter<BigDecimal> {
    @Override
    public BigDecimal[] converter(String property, Object value) {
      if (value == null) {
        return null;
      }
      List values = ((List) value);
      BigDecimal start = null;
      if (values.get(0) != null && StringUtils.isNotBlank(values.get(0) + "")) {
        start = new BigDecimal(values.get(0) + "");
      }
      BigDecimal end = null;
      if (values.get(1) != null && StringUtils.isNotBlank(values.get(1) + "")) {
        end = new BigDecimal(values.get(1) + "");
      }
      return new BigDecimal[] {
          start, end };
    }
  }

  static class DateBtwConverter implements ParamConverter<Date> {
    @Override
    public Date[] converter(String property, Object value) {
      if (value == null) {
        return null;
      }
      List<String> values = ((List<String>) value);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date start = null;
      if (values.get(0) != null && StringUtils.isNotBlank(values.get(0) + "")) {
        try {
          start = sdf.parse(values.get(0));
        } catch (ParseException e) {
          throw new RuntimeException("日期转换失败");
        }
      }
      Date end = null;
      if (values.get(1) != null && StringUtils.isNotBlank(values.get(1) + "")) {
        try {
          end = sdf.parse(values.get(1));
        } catch (ParseException e) {
          throw new RuntimeException("日期转换失败");
        }
      }
      return new Date[] {
          start, end == null ? null : DateUtils.addMilliseconds(DateUtils.addDays(end, 1), -1) };
    }
  }

  static class TimeBtwConverter implements ParamConverter<Date> {
    @Override
    public Date[] converter(String property, Object value) {
      if (value == null) {
        return null;
      }
      List<String> values = ((List<String>) value);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date start = null;
      if (values.get(0) != null && StringUtils.isNotBlank(values.get(0) + "")) {
        try {
          start = sdf.parse(values.get(0));
        } catch (ParseException e) {
          throw new RuntimeException("日期转换失败");
        }
      }
      Date end = null;
      if (values.get(1) != null && StringUtils.isNotBlank(values.get(1) + "")) {
        try {
          end = sdf.parse(values.get(1));
        } catch (ParseException e) {
          throw new RuntimeException("日期转换失败");
        }
      }
      return new Date[] {
          start, end };
    }
  }

}
