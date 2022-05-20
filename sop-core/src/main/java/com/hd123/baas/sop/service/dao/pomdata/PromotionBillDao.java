package com.hd123.baas.sop.service.dao.pomdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.hd123.baas.sop.annotation.PmsTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.hd123.rumba.commons.jdbc.sql.*;
import com.hd123.spms.manager.dao.bill.PPromotionBill;
import com.hd123.spms.manager.dao.bill.PPromotionBillJoin;
import com.hd123.spms.manager.dao.bill.PPromotionItem;
import com.hd123.spms.service.bill.PromotionBill;
import com.hd123.spms.service.bill.PromotionBillJoin;
import com.hd123.spms.service.bill.PromotionItem;
import com.hd123.spms.service.bill.SingleProduct;

@Repository
public class PromotionBillDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PmsTx
  public void save(PromotionBill target) {
    UpsertStatement insert = new UpsertBuilder().table(PPromotionBill.TABLE_NAME)
        .addValues(PPromotionBill.toFieldValues(target, "save"))
        .keys(PPromotionBill.UUID)
        .build();
    jdbcTemplate.update(insert);

    saveItems(target.getUuid(), target.getItems());
    saveJoins(target.getUuid(), target.getJoins());
  }

  @PmsTx
  public void updateState(String tenant, String billUuid, String state, Date lastModified) {
    UpdateStatement update = new UpdateBuilder().table(PPromotionBill.TABLE_NAME)
        .addValue(PPromotionBill.STATE, state)
        .addValue(PPromotionBill.LAST_MODIFIED, lastModified)
        .addValue(PPromotionBill.ABORT_TIME, lastModified)
        .where(Predicates.equals(PPromotionBill.TENANT_ID, tenant))
        .where(Predicates.equals(PPromotionBill.UUID, billUuid))
        .build();
    jdbcTemplate.update(update);
  }

  public void delete(String uuid) {
    DeleteStatement deleteItems = new DeleteBuilder().table(PPromotionBill.TABLE_NAME)
        .where(Predicates.equals(PPromotionBill.UUID, uuid))
        .build();
    jdbcTemplate.update(deleteItems);
    saveItems(uuid, Collections.emptyList());
    saveJoins(uuid, Collections.emptyList());
  }

  public List<String> queryItems(String billUuid) {
    SelectStatement select = new SelectBuilder().select(PPromotionItem.UUID)
        .from(PPromotionItem.TABLE_NAME)
        .where(Predicates.equals(PPromotionItem.BILL_UUID, billUuid))
        .build();
    return jdbcTemplate.query(select, (rs, i) -> rs.getString(PPromotionItem.UUID));
  }

  public List<PromotionItem> getItems(String billUuid) {
    SelectStatement select = new SelectBuilder().select(PPromotionItem.COLUMNS)
        .from(PPromotionItem.TABLE_NAME)
        .where(Predicates.equals(PPromotionItem.BILL_UUID, billUuid))
        .build();
    return jdbcTemplate.query(select, PPromotionItem::mapRow);
  }

  @PmsTx
  public void saveItems(String billUuid, List<PromotionItem> items) {
    DeleteStatement delete = new DeleteBuilder().table(PPromotionItem.TABLE_NAME)
        .where(Predicates.equals(PPromotionItem.BILL_UUID, billUuid))
        .build();
    jdbcTemplate.update(delete);

    delete = new DeleteBuilder().table(PSopSingleProduct.TABLE_NAME)
        .where(Predicates.equals(PSopSingleProduct.BILL_UUID, billUuid))
        .build();
    jdbcTemplate.update(delete);

    if (items.isEmpty()) {
      return;
    }
    MultilineInsertStatement itemMultilineInsert = new MultilineInsertBuilder().table(PPromotionItem.TABLE_NAME)
        .build();
    List<SingleProduct> singleProducts = new ArrayList<>();
    for (PromotionItem item : items) {
      itemMultilineInsert.addValuesLine(PPromotionItem.toFieldValues(item));
      singleProducts.addAll(item.getProducts());
    }
    jdbcTemplate.update(itemMultilineInsert);

    if (singleProducts.isEmpty() == false) {
      MultilineInsertStatement singleProductMultilineInsert = new MultilineInsertBuilder()
          .table(PSopSingleProduct.TABLE_NAME)
          .build();
      for (SingleProduct product : singleProducts) {
        singleProductMultilineInsert.addValuesLine(PSopSingleProduct.toFieldValues(product));
      }
      jdbcTemplate.update(singleProductMultilineInsert);
    }
  }

  @PmsTx
  public void saveJoins(String billUuid, List<PromotionBillJoin> joins) {
    DeleteStatement delete = new DeleteBuilder().table(PPromotionBillJoin.TABLE_NAME)
        .where(Predicates.equals(PPromotionBillJoin.BILL_UUID, billUuid))
        .build();
    jdbcTemplate.update(delete);

    if (joins.isEmpty()) {
      return;
    }
    MultilineInsertStatement multilineInsert = new MultilineInsertBuilder().table(PPromotionBillJoin.TABLE_NAME)
        .build();
    for (PromotionBillJoin join : joins) {
      multilineInsert.addValuesLine(PSopPromotionBillJoin.toFieldValues(join));
    }
    jdbcTemplate.update(multilineInsert);
  }

  public List<String> query4Sync(String tenantId) {
    SelectStatement select = new SelectBuilder().select(PPromotionBill.UUID)
        .from(PPromotionBill.TABLE_NAME)
        .where(Predicates.equals(PPromotionBill.TENANT_ID, tenantId))
        .where(Predicates.equals(PPromotionBill.STATE, "audited"))
        .where(Predicates.greaterOrEquals(PPromotionBill.FINISH, new Date()))
        .build();
    return jdbcTemplate.query(select, (rs, i) -> rs.getString(PPromotionBill.UUID));
  }

  public PromotionBill get4Sync(String tenantId, String billUuid) {
    SelectStatement select = new SelectBuilder().select(PPromotionBill.COLUMNS)
        .from(PPromotionBill.TABLE_NAME)
        .where(Predicates.equals(PPromotionBill.TENANT_ID, tenantId))
        .where(Predicates.equals(PPromotionBill.UUID, billUuid))
        .build();
    List<PromotionBill> list = jdbcTemplate.query(select, PPromotionBill::mapRow);
    if (list.isEmpty()) {
      return null;
    }
    PromotionBill target = list.get(0);

    select = new SelectBuilder().select(PPromotionItem.COLUMNS)
        .from(PPromotionItem.TABLE_NAME)
        .where(Predicates.equals(PPromotionItem.BILL_UUID, billUuid))
        .build();
    List<PromotionItem> items = jdbcTemplate.query(select, PPromotionItem::mapRow);
    target.setItems(items);

    select = new SelectBuilder().select(PSopPromotionBillJoin.COLUMNS)
        .from(PSopPromotionBillJoin.TABLE_NAME)
        .where(Predicates.equals(PSopPromotionBillJoin.BILL_UUID, billUuid))
        .build();
    List<PromotionBillJoin> joins = jdbcTemplate.query(select, PSopPromotionBillJoin::mapRow2);
    target.setJoins(joins);

    return target;
  }

}
