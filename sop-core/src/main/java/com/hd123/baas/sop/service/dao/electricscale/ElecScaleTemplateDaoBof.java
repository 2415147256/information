package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleTemplate;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryCondition;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.qd.QueryConditionProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessContext;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessException;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessor;
import com.hd123.rumba.commons.jdbc.qd.QueryProcessorBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.Predicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ElecScaleTemplateDaoBof extends BofBaseDao {
  private static final ElecScaleTemplateMapper ELEC_SCALE_TEMPLATE_MAPPER = new ElecScaleTemplateMapper();

  private final QueryProcessor QUERY_PROCESSOR = new QueryProcessorBuilder(ElecScaleTemplate.class, PElecScaleTemplate.class)
      .addConditionProcessor(new QueryConditionProcessor() {
        @Override
        public Predicate process(QueryCondition condition, QueryProcessContext context)
            throws IllegalArgumentException, QueryProcessException {
          String operation = condition.getOperation();
          Object parameter = condition.getParameter();
          if (ElecScaleTemplate.Queries.SHOP_KEY_WORD.equals(operation)) {
            SelectBuilder select = new SelectBuilder().select(PShopElecScaleTemplate.ELECTRONIC_SCALE_TEMPLATE)
                .from(PShopElecScaleTemplate.TABLE_NAME, PShopElecScaleTemplate.TABLE_AlIAS)
                .where(Predicates.or(
                    Predicates.like(PShopElecScaleTemplate.TABLE_AlIAS, PShopElecScaleTemplate.SHOPCODE,
                        parameter.toString()),
                    Predicates.like(PShopElecScaleTemplate.TABLE_AlIAS, PShopElecScaleTemplate.SHOPNAME,
                        parameter.toString()),
                    Predicates.equals(PShopElecScaleTemplate.TABLE_AlIAS, PShopElecScaleTemplate.ISALLSHOP, 1)));
            return Predicates.in(context.getPerzAlias(), PElecScaleTemplate.UUID, select.build());
          }
          return null;
        }
      })
      .build();

  public String insert(String tenant, ElecScaleTemplate template, OperateInfo operateInfo) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(template, "template");
    if (template.getUuid() == null) {
      template.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder insert = new InsertBuilder().table(PElecScaleTemplate.TABLE_NAME)
        .addValue(PElecScaleTemplate.TENANT, tenant)
        .addValue(PElecScaleTemplate.UUID, template.getUuid())
        .addValue(PElecScaleTemplate.ORG_ID, template.getOrgId())
        .addValue(PElecScaleTemplate.ELECTRONIC_SCALE, template.getElectronicScale())
        .addValue(PElecScaleTemplate.ELEC_SCALE_KEYBOARD, template.getElecScaleKeyBoard())
        .addValue(PElecScaleTemplate.NAME, template.getName());
    if (operateInfo != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    }
    jdbcTemplate.update(insert.build());
    return template.getUuid();
  }

  public ElecScaleTemplate get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PElecScaleTemplate.TENANT, tenant))
        .where(Predicates.equals(PElecScaleTemplate.UUID, uuid));
    List<ElecScaleTemplate> query = jdbcTemplate.query(select.build(), ELEC_SCALE_TEMPLATE_MAPPER);
    if (CollectionUtils.isNotEmpty(query)) {
      return query.get(0);
    }
    return null;
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    DeleteBuilder delete = new DeleteBuilder().table(PElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PElecScaleTemplate.TENANT, tenant))
        .where(Predicates.equals(PElecScaleTemplate.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public QueryResult<ElecScaleTemplate> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    qd.addByField(ElecScaleTemplate.Queries.TENANT, Cop.EQUALS, tenant);
    SelectStatement process = QUERY_PROCESSOR.process(qd);
    return executor.query(process, ELEC_SCALE_TEMPLATE_MAPPER);
  }

  public List<ElecScaleTemplate> listByUuid(String tenant, List<String> uuids) {
    Assert.notNull(tenant, "租户");
    Assert.notEmpty(uuids, "uuids");
    SelectBuilder select = new SelectBuilder().from(PElecScaleTemplate.TABLE_NAME)
        .where(Predicates.equals(PElecScaleTemplate.TENANT, tenant))
        .where(Predicates.in2(PElecScaleTemplate.UUID, uuids.toArray()));
    return jdbcTemplate.query(select.build(), ELEC_SCALE_TEMPLATE_MAPPER);
  }
}
