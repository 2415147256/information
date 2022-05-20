/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea 文件名：	FeedbackImplJob.java 模块说明： 修改历史： 2020/12/3 - Leo - 创建。
 */

package com.hd123.baas.sop.excel.job.feedback;

import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.category.CategoryService;
import com.hd123.baas.sop.service.api.basedata.goods.Goods;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsFilter;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.excel.common.AbstractBean;
import com.hd123.baas.sop.excel.common.AbstractExcelImpJob;
import com.hd123.baas.sop.service.api.feedback.FeedbackCreation;
import com.hd123.baas.sop.service.api.feedback.FeedbackFilter;
import com.hd123.baas.sop.service.api.feedback.FeedbackGdSearchResult;
import com.hd123.baas.sop.service.api.feedback.FeedbackService;
import com.hd123.baas.sop.service.api.feedback.FeedbackType;
import com.hd123.baas.sop.job.tools.JobContext;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * @author Leo
 */
@Slf4j
public class FeedbackImplJob extends AbstractExcelImpJob<FeedbackBean> {
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private static final String XLS = ".xls", XLSX = ".xlsx";
  private static final String FILENAME = "质量反馈单导入失败明细";

  @Autowired
  StoreService storeService;
  @Autowired
  GoodsService goodsService;
  @Autowired
  CategoryService categoryService;
  @Autowired
  FeedbackService feedbackService;
  @Value("${sop-service.feedback.category.targetLevel:2}")
  private int targetLevel;
  @Value("${sop-service.feedback.import.batchHandleSize:10000}")
  private int batchHandleSize;

  @Override
  protected int[] handle(String tenant, List<FeedbackBean> beans)
      throws Exception {
    if (CollectionUtils.isEmpty(beans)) {
      return new int[0];
    }
    log.info(MessageFormat.format("租户为{0}的质量反馈导入作业开始执行。", tenant));
    int[] result = new int[beans.size()];

    JobDataMap jobDataMap = JobContext.getMergedJobDataMap();
    String operateTimeStr = jobDataMap.getString(FeedbackService.OPERATE_TIME);
    String orgId = jobDataMap.getString(FeedbackImplJob.ORG_ID);
    Date operateTime = StringUtil.toDate(operateTimeStr, FeedbackService.DATETIME_FORMAT);
    String operatorId = jobDataMap.getString(FeedbackService.OPERATOR_ID);
    String operatorName = jobDataMap.getString(FeedbackService.OPERATOR_NAME);

    Set<String> storeCodes = new HashSet<>();
    Set<String> gdCodes = new HashSet<>();

    Map<String, FeedbackBean> beanMap = new LinkedHashMap<>();

    Map<String, Set<Date>> storeDeliveryTimeMap = new HashMap<>();

    log.info("质量反馈导入重复数据过滤处理。");

    for (int i = 0; i < beans.size(); i++) {
      FeedbackBean bean = beans.get(i);
      String key = getKeyReason(bean.getStoreCode(), bean.getDeliveryTime(), bean.getGdCode(),
          bean.getApplyReason());
      //重复数据过滤处理
      if (beanMap.get(key) != null) {
        result[i] = AbstractBean.IGNORE;
        bean.setFeedbackMsg("重复数据，忽略执行。");
      } else {
        beanMap.put(key, bean);
        storeCodes.add(bean.getStoreCode());
        gdCodes.add(bean.getGdCode());

        Set<Date> deliveryTimes = storeDeliveryTimeMap
            .computeIfAbsent(bean.getStoreCode(), k -> new HashSet<>());
        deliveryTimes.add(bean.getDeliveryTime());
      }
    }

    log.info("质量反馈导入门店查询操作。");

    List<Store> stores = new ArrayList<>();
    Map<String, Store> storeMap = new HashMap<>();
    if (CollectionUtils.isNotEmpty(storeCodes)) {
      StoreFilter storeFilter = new StoreFilter();
      storeFilter.setCodeIn(new ArrayList<>(storeCodes));
      QueryResult<Store> storeQueryResult = storeService.query(tenant, storeFilter);
      stores = storeQueryResult.getRecords();
    }

    Set<String> gdUuids = new HashSet<>();
    Map<String, List<FeedbackGdSearchResult>> gdCodeSearchsMap = new HashMap<>();
    Map<String, List<FeedbackGdSearchResult>> gdInputCodeSearchsMap = new HashMap<>();

    for (Store store : stores) {
      log.info(MessageFormat.format("门店组织为{0}，门店代码为{1}开始查询可质量反馈商品。", store.getOrgId(), store.getCode()));
      if (!isAllScope(tenant, orgId) && orgId.equals(store.getId())) {
        log.info(MessageFormat.format("门店组织为{0}，门店代码为{1}不存在。", store.getOrgId(), store.getCode()));
        continue;
      }
      storeMap.put(store.getCode(), store);

      Set<Date> deliveryTimes = storeDeliveryTimeMap.get(store.getCode());

      FeedbackFilter feedbackFilter = new FeedbackFilter();
      feedbackFilter.setDeliveryTimeRealIn(new ArrayList<>(deliveryTimes));
      feedbackFilter.setGoodsCodeIn(new ArrayList<>(gdCodes));
      //条件编写

      QueryResult<FeedbackGdSearchResult> queryResult = feedbackService
          .search(store.getId(), tenant, feedbackFilter);

      queryResult.getRecords().forEach(gdSearch -> {
        String key = getKey(store.getCode(), gdSearch.getDeliveryTime(),
            gdSearch.getGdCode());
        List<FeedbackGdSearchResult> gdSearchResults = gdCodeSearchsMap
            .computeIfAbsent(key, k -> new ArrayList<>());
        gdSearchResults.add(gdSearch);//门店代码 到货时间 商品代码维度

        String inputKey = getKey(store.getCode(), gdSearch.getDeliveryTime(),
            gdSearch.getGdInputCode());
        List<FeedbackGdSearchResult> gdIputSearchResults = gdInputCodeSearchsMap
            .computeIfAbsent(inputKey, k -> new ArrayList<>());
        gdIputSearchResults.add(gdSearch);//门店代码 到货时间 商品输入码维度

        gdUuids.add(gdSearch.getGdUuid());
      });
    }

    Map<String, Integer> indexMap = new HashMap<>();
    Map<String, FeedbackBean> validBeanMap = new LinkedHashMap<>();

    log.info("质量反馈导入校验数据有效性。");
    for (int i = 0; i < beans.size(); i++) {
      FeedbackBean bean = beans.get(i);
      if (result[i] == AbstractBean.FAIL || result[i] == AbstractBean.IGNORE) {
        continue;
      }
      Store store = storeMap.get(bean.getStoreCode());
      if (store == null) {
        result[i] = AbstractBean.FAIL;
        String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
        msg += "门店不存在。";
        bean.setFeedbackMsg(msg);
        continue;
      }
      String key = getKey(bean.getStoreCode(), bean.getDeliveryTime(), bean.getGdCode());
      List<FeedbackGdSearchResult> gdSearchs = gdCodeSearchsMap.get(key);
      if (CollectionUtils.isEmpty(gdSearchs)) {
        gdSearchs = gdInputCodeSearchsMap.get(key);
        if (gdSearchs == null) {
          result[i] = AbstractBean.FAIL;
          String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
          msg += "商品不存在或无可反馈数据。";
          bean.setFeedbackMsg(msg);
          continue;
        }
      }

      if (bean.getQty() == null || bean.getQty().compareTo(BigDecimal.ZERO) <= 0) {
        result[i] = AbstractBean.FAIL;
        String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
        msg += "无效申请数量。";
        bean.setFeedbackMsg(msg);
        continue;
      }

      BigDecimal qty = BigDecimal.ZERO;
      for (FeedbackGdSearchResult gdSearch : gdSearchs) {
        qty = qty.add(gdSearch.getQty());
      }

      if (qty.compareTo(BigDecimal.ZERO) <= 0) {
        result[i] = AbstractBean.FAIL;
        String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
        msg += "无可反馈数量。";
        bean.setFeedbackMsg(msg);
        continue;
      }

      String reasonKey = getKeyReason(bean.getStoreCode(), bean.getDeliveryTime(), bean.getGdCode(),
          bean.getApplyReason());

      indexMap.put(reasonKey, i);

      validBeanMap.put(reasonKey, bean);
    }

    log.info("质量反馈导入商品查询。");
    List<Goods> goodsList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(gdUuids)) {
      GoodsFilter goodsFilter = new GoodsFilter();
      goodsFilter.setIdIn(new ArrayList<>(gdUuids));
      goodsFilter.setFetchParts(Goods.PART_CATEGORY);
      QueryResult<Goods> goodsQueryResult = goodsService.query(tenant, goodsFilter);
      goodsList = goodsQueryResult.getRecords();
    }

    Map<String, Goods> goodsMap = new HashMap<>();

    Map<String, Category> categoryMap = new HashMap<>();
    List<String> categoryUpperIds = new ArrayList<>();

    for (Goods goods : goodsList) {
      goodsMap.put(goods.getGoodsGid(), goods);

      if (goods.getCategory() != null) {
        Category category = goods.getCategory();
        categoryMap.put(category.getCode(), category);

        int limitLevel = Math.min(category.getLevel(), targetLevel);
        String[] paths = splitPath(category.getPath());
        for (int i = 0; i < limitLevel; i++) {
          categoryUpperIds.add(paths[i]);
        }
      }
    }

    if (CollectionUtils.isNotEmpty(categoryUpperIds)) {
      log.info("质量反馈商品类别查询。");
      CategoryFilter categoryFilter = new CategoryFilter();
      categoryFilter.setIdIn(categoryUpperIds);
      QueryResult<Category> categories = categoryService.query(tenant, categoryFilter);
      if (CollectionUtils.isNotEmpty(categories.getRecords())) {
        categories.getRecords().stream().forEach(category -> {
          categoryMap.put(category.getCode(), category);
        });
      }
    }

    Date date = new Date();
    OperateInfo createInfo = new OperateInfo();
    createInfo.setTime(date);
    createInfo.setOperator(new Operator(operatorId, operatorName));

    for (Map.Entry<String, FeedbackBean> entry : validBeanMap.entrySet()) {
      FeedbackBean bean = entry.getValue();
      Store store = storeMap.get(entry.getValue().getStoreCode());
      String gdKey = getKey(bean.getStoreCode(), bean.getDeliveryTime(), bean.getGdCode());

      List<FeedbackGdSearchResult> gdSearchs = gdCodeSearchsMap.get(gdKey);
      if (CollectionUtils.isEmpty(gdSearchs)) {
        gdSearchs = gdInputCodeSearchsMap.get(gdKey);
      }

      Goods goods = goodsMap.get(gdSearchs.get(0).getGdUuid());
      Integer index = indexMap.get(entry.getKey());

      log.info("构造执行质量反馈单对象");
      List<FeedbackCreation> feedbacks = buildCreation(store, goods, gdSearchs, categoryMap, bean);

      if (CollectionUtils.isEmpty(feedbacks)) {
        result[index] = AbstractBean.FAIL;
        String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
        msg += "无可反馈数量。";
        bean.setFeedbackMsg(msg);
        continue;
      }

      try {
        for (FeedbackCreation feedback : feedbacks) {//保存质量反馈
          feedbackService.createAndSubmit(tenant, feedback, createInfo);
        }
        result[index] = AbstractBean.SUCCESS;//保存完,则处理成功。
      } catch (Exception e) {
        result[index] = AbstractBean.FAIL;
        bean.setFeedbackMsg(e.getMessage());
      }
    }

    log.info(MessageFormat.format("租户为{0}的质量反馈导入作业结束执行。", tenant));

    return result;
  }  // 反馈文件是否包含成功的数据

  @Override
  protected boolean includeSuccessGenerateFeedbackFile() {
    return true;
  }

  @Override
  protected int batchHandleSize() {
    return batchHandleSize;
  }

  private List<FeedbackCreation> buildCreation(Store store, Goods goods,
      List<FeedbackGdSearchResult> gdSearchs, Map<String, Category> categoryMap, FeedbackBean bean) {
    List<FeedbackCreation> feedbacks = new ArrayList<>();
    List<FeedbackGdSearchResult> gdSearchList = new ArrayList<>();
    for (FeedbackGdSearchResult gdSearch : gdSearchs) {
      if (gdSearch.getQty().compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }
      gdSearchList.add(gdSearch);
    }
    if (CollectionUtils.isEmpty(gdSearchList)) {
      return feedbacks;
    }

    String gdTypeCode = "";
    StringBuilder gdTypeName = new StringBuilder();
    Category category = goods.getCategory();

    if (category != null) {
      int limitLevel = Math.min(category.getLevel(), targetLevel);
      String[] paths = splitPath(category.getPath());
      gdTypeCode = paths[limitLevel - 1];
      for (int i = 0; i < limitLevel; i++) {
        Category upperCategory = categoryMap.get(paths[i]);
        if (i > 0) {
          gdTypeName.append("/");
        }
        if (upperCategory != null) {
          gdTypeName.append(upperCategory.getName());
        }
      }
    }

    BigDecimal qty = bean.getQty() == null ? BigDecimal.ZERO : bean.getQty();
    for (int i = 0; i < gdSearchList.size(); i++) {
      FeedbackGdSearchResult gdSearch = gdSearchList.get(i);
      if (qty.compareTo(BigDecimal.ZERO) <= 0
          || gdSearch.getQty().compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }
      FeedbackCreation feedback = new FeedbackCreation();
      feedback.setBillId(UUID.randomUUID().toString());
      feedback.setShop(store.getId());
      feedback.setOrgId(store.getOrgId());
      feedback.setShopNo(store.getCode());
      feedback.setShopName(store.getName());
      feedback.setReceiptNum(gdSearch.getReceiptNum());
      feedback.setReceiptLineId(gdSearch.getReceiptLineId());
      feedback.setGdUuid(gdSearch.getGdUuid());
      feedback.setGdCode(gdSearch.getGdCode());
      feedback.setGdInputCode(gdSearch.getGdInputCode());
      feedback.setGdName(gdSearch.getGdName());
      feedback.setMunit(gdSearch.getMunit());
      feedback.setMinMunit(gdSearch.getMinMunit());
      feedback.setQpc(gdSearch.getQpc());
      feedback.setGdTypeCode(gdTypeCode);
      feedback.setGdTypeName(gdTypeName.toString());
      feedback.setDeliveryTime(gdSearch.getDeliveryTime());
      feedback.setSinglePrice(gdSearch.getSinglePrice());
      feedback.setReceiptQty(gdSearch.getReceiptQty());
      feedback.setType(FeedbackType.excepted);
      feedback.setChannel(gdSearch.getChannel());
      String msg = StringUtil.isNullOrBlank(bean.getFeedbackMsg()) ? "" : bean.getFeedbackMsg();
      if (StringUtil.isNullOrBlank(msg)) {
        msg += "导入成功，";
      }
      if (gdSearch.getQty().compareTo(qty) >= 0) {
        gdSearch.setQty(gdSearch.getQty().subtract(qty));

        feedback.setQty(qty);
        qty = BigDecimal.ZERO;
        msg += MessageFormat
            .format("申请数量有{0}个拆分至收货单{1}中。", feedback.getQty(), feedback.getReceiptNum());
      } else {
        feedback.setQty(gdSearch.getQty());

        msg += MessageFormat
            .format("申请数量有{0}个拆分至收货单{1}中。", feedback.getQty(), feedback.getReceiptNum());

        if (i == gdSearchList.size() - 1) {
          msg += MessageFormat
              .format("申请数量{0}个，共反馈{1}个，多余的{2}个忽略处理。", bean.getQty(),
                  bean.getQty().subtract(qty).add(gdSearch.getQty()), qty.subtract(gdSearch.getQty()));
        }
        bean.setFeedbackMsg(msg);

        qty = qty.subtract(gdSearch.getQty());
        gdSearch.setQty(gdSearch.getQty().subtract(qty));
      }
      bean.setFeedbackMsg(msg);
      feedback.setTotal(feedback.getSinglePrice().multiply(feedback.getQty()));
      feedback.setApplyReason(bean.getApplyReason());
      feedback.setApplyNote(FeedbackService.IMPORT_APPLY_NOTE);

      feedbacks.add(feedback);
    }
    return feedbacks;
  }

  private String getKey(String storeCode, Date deliveryTime, String gdCode) {
    String deliveryTimeStr = StringUtil.dateToString(deliveryTime, FeedbackService.DATETIME_FORMAT);
    return storeCode + FeedbackService.SPLITER + deliveryTimeStr + FeedbackService.SPLITER + gdCode;
  }

  private String getKeyReason(String storeCode, Date deliveryTime, String gdCode, String reason) {
    String deliveryTimeStr = StringUtil.dateToString(deliveryTime, FeedbackService.DATETIME_FORMAT);
    return storeCode + FeedbackService.SPLITER + deliveryTimeStr + FeedbackService.SPLITER + gdCode
        + FeedbackService.SPLITER + reason;
  }

  private String[] splitPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return path.split("/");
  }

  public File getTempXlsFile(String tenant) throws IOException {
    String defaultBaseDir = System.getProperty("java.io.tmpdir");
    File file = new File(defaultBaseDir,
        tenant + File.separator + "sop" + File.separator + "temp" + File.separator
            + "imp_feedback" + File.separator + FILENAME + FORMAT.format(new Date()) + new Random()
            .nextInt(100000) + XLSX);
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
    return file;
  }

  private boolean isAllScope(String tenant, String orgId) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    return false;
  }

}
