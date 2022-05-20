package com.hd123.baas.sop.service.dao;

/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * <p>
 * 项目名：	ras-service
 * 文件名：	BaseDao.java
 * 模块说明：
 * 修改历史：
 * 2018年01月05日 - yanghaixiao - 创建。
 */

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.hd123.baas.sop.configuration.cache.DbNameMgr;
import com.hd123.rumba.commons.biz.entity.VersionConflictException;
import com.hd123.rumba.commons.biz.entity.VersionedEntity;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用持久化基类<br/>
 * 提供批量更新操作<br/>
 * 支持{@link AbstractStatement}的子类,如{@link InsertStatement},{@link UpdateStatement},
 * {@link DeleteStatement}<br/>
 *
 * @author yanghaixiao
 * @since 0.1
 **/
@Slf4j
public abstract class BofBaseDao {

  @Autowired
  protected JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  @Qualifier("nameMgr")
  @Autowired
  private DbNameMgr dbNameMgr;

  protected String getDBName(String tenant) {
    return dbNameMgr.getName(tenant);
  }

  /**
   * 版本检查
   *
   * @param version
   *          本次操作版本
   * @param po
   *          对批数据，一般指数据库数据版本
   * @param caption
   *          数据标题
   * @throws VersionConflictException
   *           操作版本与数库存版本不一致进抛出
   */
  public static void checkVersion(long version, VersionedEntity po, String caption) throws VersionConflictException {
    if (po != null && version != po.getVersion()) {
      throw new VersionConflictException("该" + caption + "正在被其他操作修改，您暂时无法进行此操作");
    }
  }

  /**
   * 判断影响数据库行数，以进行并发控制。
   * <p>
   * 调用都需保证只可能因版本不一致导致数库更新失败。
   *
   * @param affectedRows
   *          生效的行
   * @param caption
   *          数据标题
   * @throws VersionConflictException
   *           操作版本与数库存版本不一致进抛出
   */
  public static void checkVersion(int affectedRows, String caption) throws VersionConflictException {
    if (affectedRows < 1) {
      throw new VersionConflictException("该" + caption + "正在被其他操作修改，您暂时无法进行此操作");
    }
  }

  /**
   * 批量插入<br/>
   * 只适合同时操作一个db.<br/>
   *
   * @param statements
   * @throws IllegalArgumentException
   *           如果发现多个db，则抛
   */
  protected void batchUpdate(List<InsertStatement> statements) {
    Assert.notNull(statements, "statements");
    if (statements.isEmpty()) {
      return;
    }
    List<Object[]> insertParams = new LinkedList<>();
    String insertSqlTemplate = null;
    Set<String> dbSet = new HashSet<>();
    for (InsertStatement statement : statements) {
      dbSet.add(statement.getDatabase());
      insertSqlTemplate = statement.getSql();
      insertParams.add(statement.getParameters().toArray());
    }
    if (dbSet.size() > 1) {
      throw new IllegalArgumentException("不可批量操作不同的db");
    }
    batchInsert(insertSqlTemplate, insertParams);
  }

  private static int getCharacterPosition(String string, int count) {
    Matcher slashMatcher = Pattern.compile("\\(").matcher(string);
    int mIdx = 0;
    while (slashMatcher.find()) {
      mIdx++;
      if (mIdx == count) {
        break;
      }
    }
    return slashMatcher.start();
  }

  private void batchInsert(String sqlTemplate, List<Object[]> paramsList) {
    int valueTemplateStart = getCharacterPosition(sqlTemplate, 2);
    String valueTemplate = sqlTemplate.substring(valueTemplateStart);
    StringBuilder sb = new StringBuilder(sqlTemplate);
    for (int i = 0; i < paramsList.size() - 1; i++) {
      sb.append(",").append(valueTemplate);
    }
    Object[] all = new Object[] {};
    for (Object[] params : paramsList) {
      for (int i = 0; i < params.length; i++) {
        Object param = params[i];
        if (param instanceof Date && !(param instanceof java.sql.Date) && !(param instanceof Time)
            && !(param instanceof Timestamp)) {
          params[i] = new Timestamp(((Date) param).getTime());
        }
      }
      all = ArrayUtils.addAll(all, params);
    }
    jdbcTemplate.update(sb.toString(), all);
  }

  public <T> T getFirst(Collection<T> col) {
    if (CollectionUtils.isEmpty(col)) {
      return null;
    }
    return col.iterator().next();
  }

  /**
   * 获取列表的第一个，如果不存在，返回NULL。
   */
  public <T> T getFirst(SelectBuilder select, RowMapper<T> rowMapper) {
    return getFirst(select.build(), rowMapper);
  }

  /**
   * 获取列表的第一个，如果不存在，返回NULL。
   */
  public <T> T getFirst(SelectStatement select, RowMapper<T> rowMapper) {
    List<T> list = jdbcTemplate.query(select, rowMapper);
    return getFirst(list);
  }

  /**
   * 将指定任意SELECT语句转换为另一个语句，转换后的语句用于取得指定的SELECT语句的结果集计数，用于优化查询速度
   *
   * @param select
   *          SELECT语句对象。禁止传入null。
   * @throws IllegalArgumentException
   *           当参数select为null时抛出。
   */
  protected static SelectStatement createCountStatement(SelectStatement select) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(select, "select");
    SelectStatement inner = select.clone();
    inner.getSelectClause().getFields().clear();
    inner.select("1");
    inner.getOrderByClause().getItems().clear();
    inner.limit(Long.MAX_VALUE);
    SelectStatement countSelect = new SelectBuilder().build();
    // TODO 方言问题，待考虑。
    countSelect.select("count(1)");
    // derby限制标识符不能以“_”开头。
    countSelect.from(inner, "c__");
    return countSelect;
  }

  protected void batchUpdate(Collection<? extends AbstractStatement> statements) {
    if (CollectionUtils.isEmpty(statements)) {
      return;
    }
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    batchUpdater.add(statements.toArray(new AbstractStatement[0]));
    batchUpdater.update();
  }

}
