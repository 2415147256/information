package com.hd123.baas.sop.service.dao.skutag;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.skutag.Tag;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.jdbc.temapper.TEMapper;
import com.hd123.rumba.commons.jdbc.temapper.TEMapperBuilder;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author maodapeng
 * @Since
 */
@Repository
public class TagDaoBof extends BofBaseDao {

  private static final TEMapper<Tag> TAG_MAPPER = TEMapperBuilder.of(Tag.class, Tag.Schema.class)
      .primaryKey(Tag.Schema.UUID)
      .build();

  public int insert(String tenant, Tag tag) {
    Assert.notNull(tag);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(tag.getOrgId(), "组织");
    Assert.notNull(tag.getName(), "名称");
    InsertStatement insert = buildInsert(tenant, tag);
    return jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<Tag> tags) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(tags);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (Tag tag : tags) {
      InsertStatement insertStatement = buildInsert(tenant, tag);
      updater.add(insertStatement);
    }
    updater.update();
  }

  public int update(String tenant, Tag tag) {
    Assert.notNull(tag);
    Assert.notNull(tenant, "租戶");
    Assert.notNull(tag.getUuid(), "uuid");
    Assert.notNull(tag.getName(), "名称");
    UpdateStatement update = buildUpdate(tenant, tag);
    return jdbcTemplate.update(update);
  }

  public void batchUpdate(String tenant, List<Tag> tags) {
    Assert.notNull(tenant, "租戶");
    Assert.notEmpty(tags);
    BatchUpdater updater = new BatchUpdater(jdbcTemplate);
    for (Tag tag : tags) {
      Assert.notNull(tag.getUuid(), "uuid");
      Assert.notNull(tag.getName(), "名称");
      UpdateStatement update = buildUpdate(tenant, tag);
      updater.add(update);
    }
    updater.update();
  }

  public Tag get(String tenant, int uuid) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    SelectStatement select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.equals(Tag.Schema.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, TAG_MAPPER));
  }

  public List<Tag> getByName(String tenant, String orgId, String name) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(name, "name");

    SelectStatement select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.equals(Tag.Schema.ORG_ID, orgId))
        .where(Predicates.equals(Tag.Schema.NAME, name))
        .build();
    return jdbcTemplate.query(select, TAG_MAPPER);
  }

  public void delete(String tenant, int uuid) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuid, "uuid");

    DeleteStatement delete = new DeleteBuilder().table(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.equals(Tag.Schema.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public List<Tag> list(String tenant, String source, List<String> orgIds) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(orgIds, "orgIds");

    SelectBuilder select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.in2(Tag.Schema.ORG_ID, orgIds.toArray()))
        .orderBy(Tag.Schema.UUID, true);
    if (StringUtils.isNotEmpty(source)) {
      select.where(Predicates.equals(Tag.Schema.SOURCE, source));
    }
    return jdbcTemplate.query(select.build(), TAG_MAPPER);
  }

  public List<Tag> list(String tenant) {
    Assert.hasLength(tenant, "tenant");

    SelectStatement select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .orderBy(Tag.Schema.UUID, true)
        .build();
    return jdbcTemplate.query(select, TAG_MAPPER);
  }

  private InsertStatement buildInsert(String tenant, Tag tag) {
    return new InsertBuilder().table(Tag.Schema.TABLE_NAME)
        .addValue(Tag.Schema.TENANT, tenant)
        .addValue(Tag.Schema.ORG_ID, tag.getOrgId())
        .addValue(Tag.Schema.NAME, tag.getName())
        .addValue(Tag.Schema.CODE, tag.getCode())
        .addValue(Tag.Schema.SOURCE, tag.getSource())
        .addValue(Tag.Schema.SOURCE_ID, tag.getSourceId())
        .addValue(Tag.Schema.CREATED, tag.getCreated())
        .build();
  }

  private UpdateStatement buildUpdate(String tenant, Tag tag) {
    return new UpdateBuilder().table(Tag.Schema.TABLE_NAME)
        .setValue(Tag.Schema.NAME, tag.getName())
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.equals(Tag.Schema.UUID, tag.getUuid()))
        .build();
  }

  public List<Tag> listByUuids(String tenant, List<Integer> uuids) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(uuids, "uuids");

    SelectStatement select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.in2(Tag.Schema.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, TAG_MAPPER);
  }

  public List<Tag> listBySourceIds(String tenant, List<String> sourceIds) {
    Assert.hasLength(tenant, "tenant");
    Assert.notNull(sourceIds, "uuids");
    if (CollectionUtils.isEmpty(sourceIds)) {
      return new ArrayList<>();
    }

    SelectStatement select = new SelectBuilder().from(Tag.Schema.TABLE_NAME)
        .where(Predicates.equals(Tag.Schema.TENANT, tenant))
        .where(Predicates.in2(Tag.Schema.SOURCE_ID, sourceIds.toArray()))
        .build();
    return jdbcTemplate.query(select, TAG_MAPPER);
  }
}
