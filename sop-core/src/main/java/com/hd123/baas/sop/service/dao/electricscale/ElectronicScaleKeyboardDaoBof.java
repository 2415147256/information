package com.hd123.baas.sop.service.dao.electricscale;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboard;
import com.hd123.baas.sop.service.api.electricscale.ElecScaleKeyboardHotKey;
import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.rumba.commons.lang.Assert;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ElectronicScaleKeyboardDaoBof extends BofBaseDao {

  public static final ElectronicScaleKeyboardMapper ELECTRONIC_SCALE_KEYBOARD_MAPPER = new ElectronicScaleKeyboardMapper();

  public static final ElectronicScaleKeyboardHotKeyMapper HOT_KEY_MAPPER = new ElectronicScaleKeyboardHotKeyMapper();

  public String insert(String tenant, ElecScaleKeyboard keyboard) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(keyboard, "keyboard");
    if (keyboard.getUuid() == null) {
      keyboard.setUuid(UUID.randomUUID().toString());
    }
    InsertBuilder insert = new InsertBuilder().table(PElectronicScaleKeyboard.TABLE_NAME)
        .addValue(PElectronicScaleKeyboard.TENANT, tenant)
        .addValue(PElectronicScaleKeyboard.UUID, keyboard.getUuid());
    jdbcTemplate.update(insert.build());
    return keyboard.getUuid();
  }

  public ElecScaleKeyboard get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PElectronicScaleKeyboard.TABLE_NAME)
        .where(Predicates.equals(PElectronicScaleKeyboard.TENANT, tenant))
        .where(Predicates.equals(PElectronicScaleKeyboard.UUID, uuid));
    List<ElecScaleKeyboard> query = jdbcTemplate.query(select.build(), ELECTRONIC_SCALE_KEYBOARD_MAPPER);
    if (CollectionUtils.isNotEmpty(query)) {
      return query.get(0);
    }
    return null;
  }

  public List<ElecScaleKeyboardHotKey> getHotKeys(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    SelectBuilder select = new SelectBuilder().from(PElectronicScaleKeyboardHotKey.TABLE_NAME)
        .where(Predicates.equals(PElectronicScaleKeyboardHotKey.TENANT, tenant))
        .where(Predicates.equals(PElectronicScaleKeyboardHotKey.OWNER, uuid));
    return jdbcTemplate.query(select.build(), HOT_KEY_MAPPER);
  }

  public void deleteHotKeyByKeyboard(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    DeleteBuilder delete = new DeleteBuilder().table(PElectronicScaleKeyboardHotKey.TABLE_NAME)
        .where(Predicates.equals(PElectronicScaleKeyboardHotKey.TENANT, tenant))
        .where(Predicates.equals(PElectronicScaleKeyboardHotKey.OWNER, uuid));
    jdbcTemplate.update(delete.build());
  }

  public void delete(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    DeleteBuilder delete = new DeleteBuilder().table(PElectronicScaleKeyboard.TABLE_NAME)
        .where(Predicates.equals(PElectronicScaleKeyboard.TENANT, tenant))
        .where(Predicates.equals(PElectronicScaleKeyboard.UUID, uuid));
    jdbcTemplate.update(delete.build());
  }

  public void saveHotKeys(String tenant, List<ElecScaleKeyboardHotKey> hotKeys) {
    Assert.notNull(tenant,"tenant");
    List<InsertStatement> list = new ArrayList<>();
    for (ElecScaleKeyboardHotKey hotKey : hotKeys) {
      InsertStatement insert = buildInsert(tenant,hotKey);
      list.add(insert);
    }
    batchUpdate(list);
  }

  private InsertStatement buildInsert(String tenant, ElecScaleKeyboardHotKey hotKey) {
    InsertStatement insert = new InsertBuilder().table(PElectronicScaleKeyboardHotKey.TABLE_NAME)
            .addValue(PElectronicScaleKeyboardHotKey.TENANT,tenant)
            .addValue(PElectronicScaleKeyboardHotKey.UUID,hotKey.getUuid())
            .addValue(PElectronicScaleKeyboardHotKey.OWNER,hotKey.getOwner())
            .addValue(PElectronicScaleKeyboardHotKey.HOT_KEY,hotKey.getHotKey())
            .addValue(PElectronicScaleKeyboardHotKey.PARAM,hotKey.getParam()).build();
    return insert;
  }
}
