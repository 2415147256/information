package com.hd123.baas.sop.service.dao.menu;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.menu.Menu;
import com.hd123.baas.sop.service.api.menu.MenuType;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
@Component
public class MenuDaoBof extends BofBaseDao {

  private static final MenuMapper TO_M = new MenuMapper();

  public void insert(String tenant, Menu item) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(item, "item");

    InsertStatement insert = buildInsertStatement(tenant, item);
    jdbcTemplate.update(insert);
  }

  public void batchInsert(String tenant, List<Menu> items) throws BaasException {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(items)) {
      return;
    }

    List<InsertStatement> statements = new ArrayList<>();
    for (Menu item : items) {

      statements.add(buildInsertStatement(tenant, item));
    }
    batchUpdate(statements);
  }

  public void update(String tenant, Menu item) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(item, "item");
    Assert.notNull(item.getUuid(), "item.uuid");

    UpdateBuilder builder = new UpdateBuilder().table(PMenu.TABLE_NAME)
        .setValue(PMenu.CODE, item.getCode())
        .setValue(PMenu.PATH, item.getPath())
        .setValue(PMenu.UPPER_CODE, item.getUpperCode())
        .setValue(PMenu.TITLE, item.getTitle())
        .setValue(PMenu.TYPE, item.getType().name())

        .setValue(PMenu.ICON, item.getIcon())
        .setValue(PMenu.PARAMETERS, item.getParameters())
        .setValue(PMenu.SEQUENCE, item.getSequence());

    if (item.getCreateInfo() != null) {
      builder.setValues(PStandardEntity.toLastModifyInfoFieldValues(item.getLastModifyInfo()));
    }

    builder.where(Predicates.equals(PMenu.TENANT, tenant));
    builder.where(Predicates.equals(PMenu.UUID, item.getUuid()));

    jdbcTemplate.update(builder.build());
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    DeleteBuilder builder = new DeleteBuilder().table(PMenu.TABLE_NAME)
        .where(Predicates.equals(PMenu.TENANT, tenant))
        .where(Predicates.equals(PMenu.UUID, uuid));
    jdbcTemplate.update(builder.build());
  }

  public void delete(String tenant, MenuType type) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(type, "type");

    DeleteBuilder builder = new DeleteBuilder().table(PMenu.TABLE_NAME)
        .where(Predicates.equals(PMenu.TENANT, tenant))
        .where(Predicates.equals(PMenu.TYPE, type.name()));
    jdbcTemplate.update(builder.build());
  }

  public List<Menu> list(String tenant) {
    Assert.notNull(tenant, "租户");

    SelectBuilder select = new SelectBuilder().from(PMenu.TABLE_NAME)
        .where(Predicates.equals(PMenu.TENANT, tenant));
    return jdbcTemplate.query(select.build(), TO_M);
  }

  public Menu get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");

    SelectBuilder select = new SelectBuilder().from(PMenu.TABLE_NAME)
        .where(Predicates.equals(PMenu.TENANT, tenant))
        .where(Predicates.equals(PMenu.UUID, uuid));
    List<Menu> list = jdbcTemplate.query(select.build(), TO_M);
    return getFirst(list);
  }

  public Menu getByCode(String tenant, String code) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(code, "code");

    SelectBuilder select = new SelectBuilder().from(PMenu.TABLE_NAME)
        .where(Predicates.equals(PMenu.TENANT, tenant))
        .where(Predicates.equals(PMenu.CODE, code));
    List<Menu> list = jdbcTemplate.query(select.build(), TO_M);
    return getFirst(list);
  }

  public List<Menu> getByUpperCode(String tenant, String upperCode) {
    Assert.notNull(tenant, "租户");

    SelectBuilder select = new SelectBuilder().from(PMenu.TABLE_NAME, PMenu.TABLE_ALIAS)
        .where(Predicates.equals(PMenu.TENANT, tenant));
    if (upperCode == null) {
      select.where(Predicates.isNull(PMenu.TABLE_ALIAS, PMenu.UPPER_CODE));
    } else {
      select.where(Predicates.equals(PMenu.UPPER_CODE, upperCode));
    }
    select.orderBy(PMenu.SEQUENCE, true);
    return jdbcTemplate.query(select.build(), TO_M);
  }

  private InsertStatement buildInsertStatement(String tenant, Menu item) {

    if (StringUtils.isBlank(item.getUuid())) {
      item.setUuid(IdGenUtils.buildIidAsString());
    }

    InsertBuilder insert = new InsertBuilder().table(PMenu.TABLE_NAME)
        .addValue(PMenu.TENANT, tenant)
        .addValue(PMenu.UUID, item.getUuid())

        .addValue(PMenu.CODE, item.getCode())
        .addValue(PMenu.PATH, item.getPath())
        .addValue(PMenu.UPPER_CODE, item.getUpperCode())
        .addValue(PMenu.TITLE, item.getTitle())
        .addValue(PMenu.TYPE, item.getType().name())

        .addValue(PMenu.ICON, item.getIcon())
        .addValue(PMenu.PARAMETERS, item.getParameters())
        .addValue(PMenu.SEQUENCE, item.getSequence());

    if (item.getCreateInfo() != null) {
      insert.addValues(PStandardEntity.toCreateInfoFieldValues(item.getCreateInfo()));
      insert.addValues(PStandardEntity.toLastModifyInfoFieldValues(item.getLastModifyInfo()));
    }
    return insert.build();
  }
}
