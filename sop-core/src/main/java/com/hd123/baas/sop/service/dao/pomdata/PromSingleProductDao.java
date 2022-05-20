package com.hd123.baas.sop.service.dao.pomdata;

import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.manager.dao.bill.PPromotionBill;
import com.hd123.spms.manager.dao.bill.PPromotionBillJoin;
import com.hd123.spms.manager.dao.bill.PSingleProduct;
import com.hd123.spms.service.bill.PromotionBillState;
import com.hd123.spms.service.bill.SingleProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class PromSingleProductDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<SingleProduct> query4StoreValidate(String tenant, String orgId, String storeUuid, DateRange dateRange, Collection<String> gdGids) {
    SelectStatement select = new SelectBuilder()
        .select("p.*")
        .from(PSopSingleProduct.TABLE_NAME, "p")
        .leftJoin(PPromotionBill.TABLE_NAME, "b", Predicates.equals("p", PSingleProduct.BILL_UUID, "b", PPromotionBill.UUID))
        .where(Predicates.equals("p", PSingleProduct.TENANT_ID, tenant))
        .where(Predicates.in("p", PSingleProduct.ENTITY_UUID, gdGids.toArray()))
        .where(Predicates.lessOrEquals("p", PSingleProduct.START, dateRange.getEndDate()))
        .where(Predicates.greater("p", PSingleProduct.FINISH, dateRange.getBeginDate()))
        .where(Predicates.equals("b", PPromotionBill.STATE, PromotionBillState.audited.name()))
        .where(Predicates.equals("b", PPromotionBill.TYPE, "retail"))
        .where(Predicates.equals("b", PPromotionBill.STARTER_ORG_UUID, "-"))
        //  and (b.effectOrgUuid is null or b.effectOrgUuid ='-' or b.effectOrgUuid = '{orgGid}')\
        .where(Predicates.or(
            Predicates.isNull("b", PPromotionBill.EFFECT_ORG_UUID),
            Predicates.equals("b", PPromotionBill.EFFECT_ORG_UUID, "-"),
            Predicates.equals("b", PPromotionBill.EFFECT_ORG_UUID, orgId)
        ))
        .where(Predicates.or(
            Predicates.equals("b", PPromotionBill.ALL_UNIT, true),
            Predicates.exists(new SelectBuilder()
                .from(PPromotionBillJoin.TABLE_NAME, "j")
                .where(Predicates.equals("b", PPromotionBill.UUID, "j", PPromotionBillJoin.BILL_UUID))
                .where(Predicates.equals("j", PPromotionBillJoin.JOIN_ORG_UUID, storeUuid))
                .build())))
        .build();
    return jdbcTemplate.query(select, PSingleProduct::mapRow);
  }

  public List<String> query4H6Task(String tenant, Date startDate, Date endDate, Collection<String> gdGids) {
    SelectStatement select = new SelectBuilder()
        .select(PSopSingleProduct.BILL_UUID)
        .from(PSopSingleProduct.TABLE_NAME, "p")
        .leftJoin(PPromotionBill.TABLE_NAME, "b", Predicates.equals("p", PSingleProduct.BILL_UUID, "b", PPromotionBill.UUID))
        .where(Predicates.equals("p", PSingleProduct.TENANT_ID, tenant))
        .where(Predicates.in("p", PSingleProduct.ENTITY_UUID, gdGids.toArray()))
        .where(Predicates.lessOrEquals("p", PSingleProduct.START, endDate))
        .where(Predicates.greaterOrEquals("p", PSingleProduct.FINISH, startDate))
        .where(Predicates.equals("p", PSopSingleProduct.TIME_CYCLE, false))
        .where(Predicates.equals("b", PPromotionBill.STATE, PromotionBillState.audited.name()))
        .where(Predicates.equals("b", PPromotionBill.TYPE, "retail"))
        .where(Predicates.equals("b", PPromotionBill.STARTER_ORG_UUID, "-"))
        .orderBy("p", PSopSingleProduct.AUDIT_TIME, false)
        .build();
    return jdbcTemplate.query(select, (rs, i) -> rs.getString(PSopSingleProduct.BILL_UUID));
  }

  public List<SingleProduct> query4Prm(String tenant, String storeUuid, Date targetDate, List<String> gdGids) {
    SelectStatement select = new SelectBuilder()
        .select("p.*")
        .from(PSopSingleProduct.TABLE_NAME, "p")
        .leftJoin(PPromotionBill.TABLE_NAME, "b", Predicates.equals("p", PSingleProduct.BILL_UUID, "b", PPromotionBill.UUID))
        .where(Predicates.equals("p", PSingleProduct.TENANT_ID, tenant))
        .where(Predicates.in("p", PSingleProduct.ENTITY_UUID, gdGids.toArray()))
        .where(Predicates.lessOrEquals("p", PSingleProduct.START, targetDate))
        .where(Predicates.greaterOrEquals("p", PSingleProduct.FINISH, targetDate))
        .where(Predicates.equals("p", PSopSingleProduct.TIME_CYCLE, false))
        .where(Predicates.equals("b", PPromotionBill.STATE, PromotionBillState.audited.name()))
        .where(Predicates.equals("b", PPromotionBill.TYPE, "retail"))
        .where(Predicates.or(
            Predicates.equals("b", PPromotionBill.ALL_UNIT, true),
            Predicates.exists(new SelectBuilder()
                .from(PPromotionBillJoin.TABLE_NAME, "j")
                .where(Predicates.equals("b", PPromotionBill.UUID, "j", PPromotionBillJoin.BILL_UUID))
                .where(Predicates.equals("j", PPromotionBillJoin.JOIN_ORG_UUID, storeUuid))
                .build())))
        .build();
    return jdbcTemplate.query(select, PSingleProduct::mapRow);
  }

}
