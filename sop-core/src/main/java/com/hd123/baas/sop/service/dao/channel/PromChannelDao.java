/**
 * <p>
 * 项目名: sop-pms-parent
 * 文件名: PromChannelDao.java
 * 模块说明:
 * 修改历史:
 * 2020年11月01日 - wushuaijun- 创建
 */
package com.hd123.baas.sop.service.dao.channel;

import com.hd123.baas.sop.service.api.pms.channel.PromChannel;
import com.hd123.baas.sop.service.dao.activity.PPromActivity;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http.SortParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wushuaijun
 * @since 1.0
 */
@Repository
public class PromChannelDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void create(PromChannel promChannel) {
    InsertStatement inset = new InsertBuilder()
            .table(PPromChannel.TABLE_NAME)
            .addValues(PPromChannel.toFieldValues(promChannel))
            .build();
    jdbcTemplate.update(inset);
  }

  public void delete(String tenant, String uuid) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PPromChannel.TABLE_NAME)
            .where(Predicates.equals(PPromChannel.TENANT, tenant))
            .where(Predicates.equals(PPromChannel.UUID, uuid))
            .build();
    jdbcTemplate.update(delete);
  }

  public void update(String tenant, PromChannel promChannel) {
    UpdateStatement update = new UpdateBuilder()
            .table(PPromChannel.TABLE_NAME)
            .setValues(PPromChannel.toFieldValues(promChannel))
            .where(Predicates.equals(PPromChannel.TENANT,tenant))
            .where(Predicates.equals(PPromChannel.UUID, promChannel.getUuid()))
            .build();
    jdbcTemplate.update(update);
  }

  public PromChannel get(String tenant, String id) {
    SelectStatement select = new SelectBuilder()
            .select(PPromChannel.COLUMNS)
            .from(PPromChannel.TABLE_NAME)
            .where(Predicates.equals(PPromChannel.TENANT, tenant))
            .where(Predicates.or(Predicates.equals(PPromChannel.UUID, id),
                    Predicates.equals(PPromChannel.CODE, id)))
            .build();
    List<PromChannel> promChannels = jdbcTemplate.query(select, PPromChannel::mapRow);
    return promChannels.isEmpty() ? null : promChannels.get(0);
  }

  public QueryResult<PromChannel> query(String tenant, QueryRequest request) {
    SelectStatement select = new SelectBuilder()
            .from(PPromChannel.TABLE_NAME)
            .where(Predicates.equals(PPromChannel.TENANT, tenant))
            .build();
    for (FilterParam filterParam : request.getFilters()) {
      if (PromChannel.FILTER_CODE_LIKES.equals(filterParam.getProperty())) {
        select.where(Predicates.like(PPromChannel.CODE, filterParam.getValue()));
      }
      if (PromChannel.FILTER_NAME_LIKES.equals(filterParam.getProperty())) {
        select.where(Predicates.like(PPromChannel.NAME, filterParam.getValue()));
      }
    }
    if (CollectionUtils.isEmpty(request.getSorters())) {
      select.orderBy(PPromActivity.CREATE_INFO_TIME,false);
    } else {
      for (SortParam sortParam : request.getSorters()) {
        select.orderBy(sortParam.getProperty(), "asc".equals(sortParam.getDirection()));
      }
    }
    JdbcPagingQueryExecutor<PromChannel> queryExecutor =
            new JdbcPagingQueryExecutor<>(jdbcTemplate, PPromChannel::mapRow);
    int page = request.getLimit() == 0 ? 0 : request.getStart() / request.getLimit();
    QueryResult<PromChannel> queryResult = queryExecutor.query(select, page, request.getLimit());
    return queryResult;
  }

}
