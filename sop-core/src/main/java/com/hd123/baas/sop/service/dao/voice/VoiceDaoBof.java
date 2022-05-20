package com.hd123.baas.sop.service.dao.voice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.voice.Voice;
import com.hd123.baas.sop.service.api.voice.VoiceLine;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;

/**
 * @author W.J.H.7
 */
@Component
public class VoiceDaoBof extends BofBaseDao {

  private static final VoiceMapper TO_M = new VoiceMapper();
  private static final VoiceLineMapper LINE_TO_M = new VoiceLineMapper();

  private QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(Voice.class, PVoice.class).build();

  public void insert(String tenant, Voice item) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(item, "item");

    InsertBuilder insert = new InsertBuilder().table(PVoice.TABLE_NAME)
        .addValue(PVoice.TENANT, tenant)
        .addValue(PVoice.UUID, item.getUuid())

        .addValue(PVoice.REQUEST_ID, item.getRequestId())
        .addValue(PVoice.TITLE, item.getTitle())
        .addValue(PVoice.TEMPLATE_ID, item.getTemplateId())
        .addValue(PVoice.TEMPLATE_CODE, item.getTemplateCode().name())
        .addValue(PVoice.TEMPLATE_CONTENT, item.getTemplateContent())

        .addValue(PVoice.CREATED, item.getCreated());

    jdbcTemplate.update(insert.build());
  }

  public void insertLine(String tenant, String owner, List<VoiceLine> lines) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");

    List<InsertStatement> list = new ArrayList<>();
    for (VoiceLine line : lines) {
      InsertBuilder insert = new InsertBuilder().table(PVoiceLine.TABLE_NAME)
          .addValue(PVoiceLine.UUID, line.getUuid())
          .addValue(PVoiceLine.TENANT, tenant)
          .addValue(PVoiceLine.OWNER, owner)

          .addValue(PVoiceLine.SHOP_ID, line.getShop().getUuid())
          .addValue(PVoiceLine.SHOP_CODE, line.getShop().getCode())
          .addValue(PVoiceLine.SHOP_NAME, line.getShop().getName())

          .addValue(PVoiceLine.CALLEE, line.getCallee())
          .addValue(PVoiceLine.TEMPLATE_PARAS, JsonUtil.objectToJson(line.getTemplateParams()));

      list.add(insert.build());
    }
    batchUpdate(list);
  }

  public QueryResult<Voice> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant);
    qd.addByField(Voice.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement select = QUERY_PROCESSOR.process(qd);
    return executor.query(select, TO_M);
  }

  public Voice getByRequestId(String tenant, String requestId) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(requestId, "requestId");

    SelectBuilder select = new SelectBuilder().from(PVoice.TABLE_NAME)
        .where(Predicates.equals(PVoice.TENANT, tenant))
        .where(Predicates.equals(PVoice.REQUEST_ID, requestId));
    List<Voice> list = jdbcTemplate.query(select.build(), TO_M);
    return getFirst(list);
  }

  public List<VoiceLine> getLineByOwner(String tenant, String owner) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(owner, "owner");

    SelectBuilder select = new SelectBuilder().from(PVoiceLine.TABLE_NAME)
        .where(Predicates.equals(PVoiceLine.TENANT, tenant))
        .where(Predicates.equals(PVoiceLine.OWNER, owner));
    return jdbcTemplate.query(select.build(), LINE_TO_M);
  }

  public Voice get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "requestId");

    SelectBuilder select = new SelectBuilder().from(PVoice.TABLE_NAME)
        .where(Predicates.equals(PVoice.TENANT, tenant))
        .where(Predicates.equals(PVoice.UUID, uuid));
    List<Voice> list = jdbcTemplate.query(select.build(), TO_M);
    return getFirst(list);
  }
}
