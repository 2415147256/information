package com.hd123.baas.sop.service.dao.offset;

import org.springframework.stereotype.Repository;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.offset.Offset;
import com.hd123.baas.sop.service.api.offset.OffsetType;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author zhengzewang on 2020/11/17.
 */
@Repository
public class OffsetDaoBof extends BofBaseDao {

  public Offset get(String tenant, OffsetType type, String spec) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(type, "type");
    Assert.hasText(spec, "spec");
    SelectStatement select = new SelectBuilder().select(POffset.allColumns())
        .from(POffset.TABLE_NAME)
        .where(Predicates.equals(POffset.TENANT, tenant))
        .where(Predicates.equals(POffset.TYPE, type.name()))
        .where(Predicates.equals(POffset.SPEC, spec))
        .build();
    return getFirst(jdbcTemplate.query(select, new OffsetMapper()));
  }

  public Offset getWithLock(String tenant, OffsetType type, String spec) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(type, "type");
    Assert.hasText(spec, "spec");
    SelectStatement select = new SelectBuilder().select(POffset.allColumns())
        .from(POffset.TABLE_NAME)
        .where(Predicates.equals(POffset.TENANT, tenant))
        .where(Predicates.equals(POffset.TYPE, type.name()))
        .where(Predicates.equals(POffset.SPEC, spec))
        .forUpdate() //
        .build();
    return getFirst(jdbcTemplate.query(select, new OffsetMapper()));
  }

  public void insert(String tenant, Offset offset) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(offset, "offset");
    Assert.notNull(offset.getType(), "offset.type");
    Assert.hasText(offset.getSpec(), "offset.spec");
    Assert.notNull(offset.getSeq(), "offset.seq");
    InsertStatement insert = new InsertBuilder().table(POffset.TABLE_NAME)
        .addValue(POffset.TENANT, tenant)
        .addValue(POffset.TYPE, offset.getType().name())
        .addValue(POffset.SPEC, offset.getSpec())
        .addValue(POffset.SEQ, offset.getSeq())
        .build();
    jdbcTemplate.update(insert);
  }

  public void update(String tenant, Offset offset) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(offset, "offset");
    Assert.notNull(offset.getType(), "offset.type");
    Assert.hasText(offset.getSpec(), "offset.spec");
    Assert.notNull(offset.getSeq(), "offset.seq");
    UpdateStatement insert = new UpdateBuilder().table(POffset.TABLE_NAME)
        .addValue(POffset.SEQ, offset.getSeq())
        .where(Predicates.equals(POffset.TENANT, tenant))
        .where(Predicates.equals(POffset.TYPE, offset.getType().name()))
        .where(Predicates.equals(POffset.SPEC, offset.getSpec()))
        .build();
    jdbcTemplate.update(insert);
  }

}
