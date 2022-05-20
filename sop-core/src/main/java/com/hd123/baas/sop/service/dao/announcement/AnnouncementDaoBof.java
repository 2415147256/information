package com.hd123.baas.sop.service.dao.announcement;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.announcement.Announcement;
import com.hd123.baas.sop.service.api.announcement.AnnouncementProgress;
import com.hd123.baas.sop.service.api.announcement.AnnouncementState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author zhengzewang on 2020/11/20.
 */
@Repository
public class AnnouncementDaoBof extends BofBaseDao {

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Announcement.class, PAnnouncement.class).build();

  public QueryResult<Announcement> query(String tenant, QueryDefinition qd) {
    qd.addByField(Announcement.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, new AnnouncementMapper());
  }

  public Announcement get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    SelectStatement select = new SelectBuilder().select(PAnnouncement.allColumns())
        .from(PAnnouncement.TABLE_NAME)
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, uuid))
        .build();
    return getFirst(jdbcTemplate.query(select, new AnnouncementMapper()));
  }

  public List<Announcement> list(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return new ArrayList<>();
    }
    SelectStatement select = new SelectBuilder().select(PAnnouncement.allColumns())
        .from(PAnnouncement.TABLE_NAME)
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.in2(PAnnouncement.UUID, uuids.toArray()))
        .build();
    return jdbcTemplate.query(select, new AnnouncementMapper());
  }

  public String insert(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(announcement, "announcement");
    Assert.notNull(operateInfo, "operateInfo");
    if (StringUtils.isBlank(announcement.getUuid())) {
      announcement.setUuid(UUID.randomUUID().toString());
    }
    announcement.setCreateInfo(operateInfo);
    announcement.setLastModifyInfo(operateInfo);
    InsertStatement insert = new InsertBuilder().table(PAnnouncement.TABLE_NAME)
        .addValues(PAnnouncement.forSaveNew(announcement))
        .addValue(PAnnouncement.TENANT, tenant)
        .addValue(PAnnouncement.ALL_SHOPS, announcement.isAllShops())
        .addValue(PAnnouncement.SEND_POS, announcement.isSendPos())
        .addValue(PAnnouncement.TITLE, announcement.getTitle())
        .addValue(PAnnouncement.CONTENT, announcement.getContent())
        .addValue(PAnnouncement.IMAGE, announcement.getImage())
        .addValue(PAnnouncement.URL, announcement.getUrl())
        .addValue(PAnnouncement.STATE, announcement.getState().name())
        .addValue(PAnnouncement.PROGRESS, announcement.getProgress().name())
        .addValue(PAnnouncement.ORG_ID, announcement.getOrgId())
        .addValue(PAnnouncement.TARGET_TYPE, announcement.getTargetType().name())
        .build();
    jdbcTemplate.update(insert);
    return announcement.getUuid();
  }

  public void update(String tenant, Announcement announcement, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(announcement, "announcement");
    Assert.notNull(operateInfo, "operateInfo");
    announcement.setLastModifyInfo(operateInfo);
    UpdateStatement update = new UpdateBuilder().table(PAnnouncement.TABLE_NAME)
        .addValues(PAnnouncement.forSaveModify(announcement))
        .addValue(PAnnouncement.ALL_SHOPS, announcement.isAllShops())
        .addValue(PAnnouncement.SEND_POS, announcement.isSendPos())
        .addValue(PAnnouncement.TITLE, announcement.getTitle())
        .addValue(PAnnouncement.CONTENT, announcement.getContent())
        .addValue(PAnnouncement.IMAGE, announcement.getImage())
        .addValue(PAnnouncement.URL, announcement.getUrl())
        .addValue(PAnnouncement.STATE, announcement.getState().name())
        .addValue(PAnnouncement.PROGRESS, announcement.getProgress().name())
        .addValue(PAnnouncement.TARGET_TYPE, announcement.getTargetType().name())
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, announcement.getUuid()))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeState(String tenant, String uuid, AnnouncementState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(state, "state");
    UpdateStatement update = new UpdateBuilder().table(PAnnouncement.TABLE_NAME)
        .addValue(PAnnouncement.STATE, state.name())
        .addValues(PAnnouncement.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void changeProgress(String tenant, String uuid, AnnouncementProgress progress, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    Assert.notNull(progress, "progress");
    UpdateStatement update = new UpdateBuilder().table(PAnnouncement.TABLE_NAME)
        .addValue(PAnnouncement.PROGRESS, progress.name())
        .addValues(PAnnouncement.toLastModifyInfoFieldValues(operateInfo))
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, uuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void delete(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    DeleteStatement delete = new DeleteBuilder().table(PAnnouncement.TABLE_NAME)
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.equals(PAnnouncement.UUID, uuid))
        .build();
    jdbcTemplate.update(delete);
  }

  public void batchDelete(String tenant, Collection<String> uuids) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }
    DeleteStatement delete = new DeleteBuilder().table(PAnnouncement.TABLE_NAME)
        .where(Predicates.equals(PAnnouncement.TENANT, tenant))
        .where(Predicates.in2(PAnnouncement.UUID, uuids.toArray()))
        .build();
    jdbcTemplate.update(delete);
  }

}
