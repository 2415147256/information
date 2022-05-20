package com.hd123.baas.sop.service.dao.feedback;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.Sort;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackApproval;
import com.hd123.baas.sop.service.api.feedback.FeedbackDepLine;
import com.hd123.baas.sop.service.api.feedback.FeedbackExt;
import com.hd123.baas.sop.service.api.feedback.FeedbackFilter;
import com.hd123.baas.sop.service.api.feedback.FeedbackImage;
import com.hd123.baas.sop.service.api.feedback.FeedbackReasonType;
import com.hd123.baas.sop.service.api.feedback.FeedbackRejection;
import com.hd123.baas.sop.service.api.feedback.FeedbackResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.sql.AndPredicate;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.OrPredicate;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.spms.commons.json.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 质量反馈单数据访问
 *
 * @author yu lilin on 2020/11/12
 */
@Repository
public class FeedbackDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  protected JdbcPagingQueryExecutor executor;

  public Feedback get(String id, String... fetchParts) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    SelectStatement select = new SelectBuilder()
        .select(PFeedback.allColumns()).from(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_BILLID, id))
        .build();
    List<Feedback> feedbacks = jdbcTemplate.query(select, new FeedbackMapper());
    if (feedbacks.isEmpty()) {
      return null;
    }
    Feedback feedback = feedbacks.get(0);
    //获取图片
    if (ArrayUtils.contains(fetchParts, Feedback.FETCH_IMAGES)) {
      feedback.setImages(fetchImage(feedback.getBillId()));
    }
    //获取承担明细
    if (ArrayUtils.contains(fetchParts, Feedback.FETCH_DEP_LINES)) {
      feedback.setDepLines(fetchDepLine(feedback.getBillId()));
    }
    return feedback;
  }

  private List<FeedbackImage> fetchImage(String billId) {
    SelectStatement select = new SelectBuilder()
        .select(PFeedbackImage.allColumns()).from(PFeedbackImage.TABLE_NAME)
        .where(Predicates.equals(PFeedbackImage.FIELD_BILL_ID, billId))
        .orderBy(PFeedbackImage.FIELD_LINE_NO)
        .build();
    return jdbcTemplate.query(select, new FeedbackImageMapper());
  }

  private List<FeedbackDepLine> fetchDepLine(String billId) {
    SelectStatement select = new SelectBuilder()
        .select(PFeedbackDepLine.allColumns()).from(PFeedbackDepLine.TABLE_NAME)
        .where(Predicates.equals(PFeedbackDepLine.FIELD_BILL_ID, billId))
        .orderBy(PFeedbackDepLine.FIELD_LINE_NO)
        .build();
    return jdbcTemplate.query(select, new FeedbackDepLineMapper());
  }

  public QueryResult<Feedback> query(String tenant, FeedbackFilter filter) {
    SelectBuilder builder = new SelectBuilder()
        .select(PFeedback.allColumns()).from(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_TENANT, tenant));

    AndPredicate andPredicate = new AndPredicate();

    if (!StringUtil.isNullOrBlank(filter.getOrgIdEq())) {
      andPredicate.add(Predicates.equals(PFeedback.ORG_ID, filter.getOrgIdEq()));
    }
    if (CollectionUtils.isNotEmpty(filter.getOrgIdIn())) {
      andPredicate.add(Predicates.in2(PFeedback.ORG_ID, filter.getOrgIdIn().toArray()));
    }

    if (!StringUtil.isNullOrBlank(filter.getKeywordLike())) {
      andPredicate.add(Predicates.or(
          Predicates.like(PFeedback.FIELD_GDCODE, filter.getKeywordLike()),
          Predicates.like(PFeedback.FIELD_GDINPUTCODE, filter.getKeywordLike()),
          Predicates.like(PFeedback.FIELD_GDNAME, filter.getKeywordLike())));
    }
    if (!StringUtil.isNullOrBlank(filter.getTypeNameLike())) {
      andPredicate.add(Predicates.like(PFeedback.FIELD_GDTYPE_NAME, filter.getTypeNameLike()));
    }
    if (!StringUtil.isNullOrBlank(filter.getShopNameLike())) {
      OrPredicate orPredicate = new OrPredicate();
      orPredicate.add(Predicates.like(PFeedback.FIELD_SHOP_NO, filter.getShopNameLike()));
      orPredicate.add(Predicates.like(PFeedback.FIELD_SHOP_NAME, filter.getShopNameLike()));
      andPredicate.add(orPredicate);
    }
    if (filter.getTypeEq() != null) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_TYPE, filter.getTypeEq().name()));
    }
    if (!StringUtil.isNullOrBlank(filter.getApplyReasonEq())) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_APPLY_REASON, filter.getApplyReasonEq()));
    }
    if (filter.getResultEq() != null) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_RESULT, filter.getResultEq().name()));
    }
    if (filter.getStateEq() != null) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_STATE, filter.getStateEq().name()));
    }
    if (filter.getDeliveryTimeIn() != null && filter.getDeliveryTimeIn().size() == 2) {
      andPredicate.add(Predicates.between(PFeedback.FIELD_DELIVERY_TIME,
          filter.getDeliveryTimeIn().get(0), filter.getDeliveryTimeIn().get(1)));
    }
    if (filter.getAuditTimeIn() != null && filter.getAuditTimeIn().size() == 2) {
      andPredicate.add(Predicates.between(PFeedback.FIELD_AUDIT_TIME,
          filter.getAuditTimeIn().get(0), filter.getAuditTimeIn().get(1)));
    }
    if (filter.getChannelEq() != null) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_CHANNEL, filter.getChannelEq()));
    }
    if (filter.getSpNoEq() != null) {
      andPredicate.add(Predicates.equals(PFeedback.FIELD_SP_NO, filter.getChannelEq()));
    }

    if (andPredicate.getOperands().length > 0) {
      builder.where(andPredicate);
    }

    if (CollectionUtils.isNotEmpty(filter.getSorts())) {
      for (Sort sort : filter.getSorts()) {
        builder.orderBy(sort.getSortKey(), !sort.isDesc());
      }
    } else {
      builder.orderBy(PFeedback.CREATE_INFO_TIME, false);//默认申请时间倒序
    }
    SelectStatement select = builder.build();

    QueryResult<Feedback> result = executor.query(select,
        filter.getPage(), filter.getPageSize(), new FeedbackMapper());
    List<Feedback> feedbacks = result.getRecords();
    for (Feedback feedback : feedbacks) {
      //获取图片
      feedback.setImages(fetchImage(feedback.getBillId()));
      //获取承担明细
      feedback.setDepLines(fetchDepLine(feedback.getBillId()));
    }

    result.setRecords(feedbacks);
    return result;
  }

  @Tx
  public String save(Feedback feedback) {
    if (StringUtils.isBlank(feedback.getUuid())) {
      feedback.setUuid(UUID.randomUUID().toString());
    }
    InsertStatement insert = new InsertBuilder()
        .table(PFeedback.TABLE_NAME)
        .addValue(PFeedback.UUID, feedback.getUuid())
        .addValues(PFeedback.toAllFieldValues(feedback))
        .build();
    jdbcTemplate.update(insert);
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (int i = 0; i < feedback.getImages().size(); i++) {
      FeedbackImage img = feedback.getImages().get(i);
      if (StringUtils.isBlank(img.getUuid())) {
        img.setUuid(UUID.randomUUID().toString());
      }
      insert = new InsertBuilder()
          .table(PFeedbackImage.TABLE_NAME)
          .addValues(PFeedbackImage.forSaveNew(img))
          .addValue(PFeedbackImage.FIELD_BILL_ID, feedback.getBillId())
          .addValue(PFeedbackImage.FIELD_ID, img.getId())
          .addValue(PFeedbackImage.FIELD_LINE_NO, i + 1)
          .addValue(PFeedbackImage.FIELD_URL, img.getUrl())
          .addValue(PFeedbackImage.FIELD_SHOP, feedback.getShop())
          .addValue(PFeedbackImage.FIELD_TENANT, feedback.getTenant())
          .build();
      batchUpdater.add(insert);
    }
    batchUpdater.update();
    return feedback.getBillId();
  }

  @Tx
  public void deleteReasons(String tenant, String orgId, FeedbackReasonType type) {
    DeleteStatement delete = new DeleteBuilder()
        .table(PFeedbackReason.TABLE_NAME)
        .where(Predicates.equals(PFeedbackReason.FIELD_TENANT, tenant))
        .where(Predicates.equals(PFeedbackReason.ORG_ID, orgId))
        .where(Predicates.equals(PFeedbackReason.FIELD_TYPE, type.name()))
        .build();
    jdbcTemplate.update(delete);
  }

  @Tx
  public void saveReasons(String tenant, String orgId, List<String> reasons, FeedbackReasonType type,
      OperateInfo operateInfo) {
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    InsertStatement insert;
    for (int i = 0; i < reasons.size(); i++) {
      insert = new InsertBuilder()
          .table(PFeedbackReason.TABLE_NAME)
          .addValue(PStandardEntity.UUID, UUID.randomUUID().toString())
          .addValues(PStandardEntity.toCreateInfoFieldValues(operateInfo))
          .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
          .addValue(PFeedbackReason.FIELD_TENANT, tenant)
          .addValue(PFeedbackReason.ORG_ID, orgId)
          .addValue(PFeedbackReason.FIELD_TYPE, type.name())
          .addValue(PFeedbackReason.FIELD_CONTENT, reasons.get(i))
          .build();
      batchUpdater.add(insert);
    }
    batchUpdater.update();
  }

  public List<String> listReasons(String tenant, String orgId, FeedbackReasonType type) {
    SelectStatement select = new SelectBuilder()
        .select(PFeedbackReason.FIELD_CONTENT).from(PFeedbackReason.TABLE_NAME)
        .where(Predicates.equals(PFeedbackReason.FIELD_TENANT, tenant))
        .where(Predicates.equals(PFeedbackReason.ORG_ID, orgId))
        .where(Predicates.equals(PFeedbackReason.FIELD_TYPE, type.name()))
        .build();
    return jdbcTemplate.query(select, new SingleColumnRowMapper<>());
  }

  public void insert(String tenant, Feedback feedback) {
    InsertStatement insert = new InsertBuilder()
        .table(PFeedback.TABLE_NAME)
        .addValue(PFeedback.UUID, UUID.randomUUID().toString())
        .addValues(PFeedback.toAllFieldValues(feedback))
        .build();
    jdbcTemplate.update(insert);
  }

  /**
   * 是否存在指定质量反馈单
   *
   * @return 状态
   */
  public String isExists(String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    SelectStatement select = new SelectBuilder()
        .select(PFeedback.FIELD_STATE).from(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_BILLID, id))
        .build();
    List<String> list = jdbcTemplate.query(select, new SingleColumnRowMapper<>());
    if (list.isEmpty()) {
      return null;
    } else {
      return list.get(0);
    }
  }

  @Tx
  public void reject(FeedbackRejection rejection, OperateInfo operateInfo) {
    Assert.notNull(rejection);
    //更新质量反馈单
    UpdateStatement update = new UpdateBuilder()
        .table(PFeedback.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PFeedback.FIELD_AUDITOR_ID, operateInfo.getOperator().getId())
        .addValue(PFeedback.FIELD_AUDITOR_NAME, operateInfo.getOperator().getFullName())
        .addValue(PFeedback.FIELD_AUDIT_TIME, operateInfo.getTime())
        .addValue(PFeedback.FIELD_STATE, FeedbackState.audited.name())
        .addValue(PFeedback.FIELD_RESULT, FeedbackResult.rejected.name())
        .addValue(PFeedback.FIELD_AUDIT_REASON, rejection.getReason())
        .addValue(PFeedback.FIELD_AUDIT_NOTE, rejection.getNote())
        .where(Predicates.equals(PFeedback.FIELD_BILLID, rejection.getBillId()))
        .build();
    jdbcTemplate.update(update);
  }

  @Tx
  public void audit(FeedbackApproval approval, OperateInfo operateInfo, String tenant,
      String shop) {
    Assert.notNull(approval);
    //更新质量反馈单
    UpdateStatement update = new UpdateBuilder()
        .table(PFeedback.TABLE_NAME)
        .addValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo))
        .addValue(PFeedback.FIELD_AUDITOR_ID, operateInfo.getOperator().getId())
        .addValue(PFeedback.FIELD_AUDITOR_NAME, operateInfo.getOperator().getFullName())
        .addValue(PFeedback.FIELD_AUDIT_TIME, operateInfo.getTime())
        .addValue(PFeedback.FIELD_STATE, FeedbackState.audited.name())
        .addValue(PFeedback.FIELD_RESULT, FeedbackResult.approved.name())
        .addValue(PFeedback.FIELD_AUDIT_REASON, approval.getReason())
        .addValue(PFeedback.FIELD_PAY_RATE, approval.getPayRate())
        .addValue(PFeedback.FIELD_PAY_TOTAL, approval.getPayTotal())
        .addValue(PFeedback.FIELD_AUDIT_NOTE, approval.getNote())
        .where(Predicates.equals(PFeedback.FIELD_BILLID, approval.getBillId()))
        .build();
    jdbcTemplate.update(update);

    //插入承担明细
    InsertStatement insert;
    BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
    for (int i = 0; i < approval.getFeedbackDepLines().size(); i++) {
      FeedbackDepLine depLine = approval.getFeedbackDepLines().get(i);
      if (StringUtils.isBlank(depLine.getUuid())) {
        depLine.setUuid(UUID.randomUUID().toString());
      }
      insert = new InsertBuilder()
          .table(PFeedbackDepLine.TABLE_NAME)
          .addValues(PFeedbackDepLine.forSaveNew(depLine))
          .addValue(PFeedbackDepLine.FIELD_BILL_ID, approval.getBillId())
          .addValue(PFeedbackDepLine.FIELD_LINE_NO, i + 1)
          .addValue(PFeedbackDepLine.FIELD_DEP_CODE, depLine.getDepCode())
          .addValue(PFeedbackDepLine.FIELD_DEP_NAME, depLine.getDepName())
          .addValue(PFeedbackDepLine.FIELD_RATE, depLine.getRate())
          .addValue(PFeedbackDepLine.FIELD_TOTAL, depLine.getTotal())
          .addValue(PFeedbackDepLine.FIELD_TENANT, tenant)
          .addValue(PFeedbackDepLine.FIELD_SHOP, shop)
          .build();
      batchUpdater.add(insert);
    }
    batchUpdater.update();
  }

  @Tx
  public void batchUpdateState(List<String> billIds, FeedbackState state,
      OperateInfo operateInfo) {
    Map<String, Object> map = new HashMap<>();
    map.put(PFeedback.FIELD_STATE, StringUtil.toString(state));
    map.put(PFeedback.LAST_MODIFY_INFO_TIME, operateInfo.getTime());
    map.put(PFeedback.LAST_MODIFY_INFO_OPERATOR_ID, operateInfo.getOperator().getId());
    map.put(PFeedback.LAST_MODIFY_INFO_OPERATOR_FULL_NAME, operateInfo.getOperator().getFullName());
    map
        .put(PFeedback.LAST_MODIFY_INFO_OPERATOR_NAMESPACE, operateInfo.getOperator().getNamespace());

    UpdateStatement statement = new UpdateBuilder().table(PFeedback.TABLE_NAME).setValues(map)
        .where(Predicates.in(null, PFeedback.FIELD_BILLID, billIds.toArray()))
        .build();

    jdbcTemplate.update(statement);
  }

  public void addSpNo(String tenant, String billId, String spNo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(billId, "feedback");
    UpdateStatement update = new UpdateBuilder()
        .table(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_BILLID, billId))
        .addValue(PFeedback.FIELD_SP_NO, spNo)
        .setValue(PFeedback.LAST_MODIFY_INFO_TIME, new Date())
        .build();
    jdbcTemplate.update(update);
  }


  public void updateState(String tenant, String billId, FeedbackState state, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(billId, "feedback");
    UpdateBuilder builder = new UpdateBuilder()
        .table(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_BILLID, billId))
        .setValue(PFeedback.FIELD_STATE, state.name())
        .setValues(PStandardEntity.toLastModifyInfoFieldValues(operateInfo));
    jdbcTemplate.update(builder.build());
  }

  public void addExt(String tenant, String billId, FeedbackExt ext) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(billId, "feedback");
    UpdateStatement update = new UpdateBuilder()
        .table(PFeedback.TABLE_NAME)
        .where(Predicates.equals(PFeedback.FIELD_BILLID, billId))
        .setValue(PFeedback.FIELD_EXT, JsonUtil.objectToJson(ext))
        .setValue(PFeedback.LAST_MODIFY_INFO_TIME, new Date())
        .build();
    jdbcTemplate.update(update);
  }
}
