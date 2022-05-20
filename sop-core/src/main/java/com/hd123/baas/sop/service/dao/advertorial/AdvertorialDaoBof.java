package com.hd123.baas.sop.service.dao.advertorial;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.advertorial.Advertorial;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import static com.hd123.rumba.commons.jdbc.sql.Predicates.like;
import static com.hd123.rumba.commons.jdbc.sql.Predicates.or;

@Repository
public class AdvertorialDaoBof extends BofBaseDao {
  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Advertorial.class, PAdvertorial.class)
      .addConditionProcessor((condition, context) -> {
        if (condition == null) {
          return null;
        }
        String alias = context.getPerzAlias();
        if (StringUtils.equalsIgnoreCase(Advertorial.Queries.KEYWORD, condition.getOperation())) {
          return or(like(alias, PAdvertorial.UUID, condition.getParameter()),
              like(alias, PAdvertorial.TITLE, condition.getParameter()));
        }
        return null;
      })
      .build();

  public QueryResult<Advertorial> query(String tenant, QueryDefinition qd) {
    qd.addByField(Advertorial.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement selectStatement = QUERY_PROCESSOR.process(qd);
    return executor.query(selectStatement, new AdvertorialMapper());
  }

  public Advertorial get(String tenant, String uuid) {
    SelectStatement selectStatement = new SelectBuilder().select(PAdvertorial.allColumns())
        .from(PAdvertorial.TABLE_NAME)
        .setWhere(Predicates.equals(PAdvertorial.TENANT, tenant))
        .setWhere(Predicates.equals(PAdvertorial.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(selectStatement, new AdvertorialMapper()));
  }

  public void update(Advertorial advertorial, OperateInfo operateInfo) {
    Assert.notNull(operateInfo, "OperateInfo");
    advertorial.setLastModifyInfo(operateInfo);
    UpdateStatement updateStatement = new UpdateBuilder().table(PAdvertorial.TABLE_NAME)
        .addValues(PAdvertorial.forSaveModify(advertorial))
        .addValue(PAdvertorial.TENANT, advertorial.getTenant())
        .addValue(PAdvertorial.UUID, advertorial.getUuid())
        .addValue(PAdvertorial.TITLE, advertorial.getTitle())
        .addValue(PAdvertorial.CONTENT, advertorial.getContent())
        .addValue(PAdvertorial.TH_URI, advertorial.getThUri())
        .setWhere(Predicates.equals(PAdvertorial.TENANT, advertorial.getTenant()))
        .setWhere(Predicates.equals(PAdvertorial.UUID, advertorial.getUuid()))
        .build();
    jdbcTemplate.update(updateStatement);
  }

  public String insert(Advertorial advertorial, OperateInfo operateInfo) {
    Assert.notNull(advertorial, "advertorial");
    Assert.notNull(operateInfo, "operateInfo");
    advertorial.setCreateInfo(operateInfo);
    advertorial.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(PAdvertorial.TABLE_NAME)
        .addValues(PAdvertorial.forSaveNew(advertorial))
        .addValue(PAdvertorial.TENANT, advertorial.getTenant())
        .addValue(PAdvertorial.TITLE, advertorial.getTitle())
        .addValue(PAdvertorial.CONTENT, advertorial.getContent())
        .addValue(PAdvertorial.TH_URI, advertorial.getThUri())
        .build();
    jdbcTemplate.update(insert);
    return advertorial.getUuid();
  }

  public void delete(String tenant, String uuid) {
    DeleteStatement deleteStatement = new DeleteBuilder().table(PAdvertorial.TABLE_NAME)
        .setWhere(Predicates.equals(PAdvertorial.TENANT, tenant))
        .setWhere(Predicates.equals(PAdvertorial.UUID, uuid))
        .build();
    jdbcTemplate.update(deleteStatement);

  }

}
