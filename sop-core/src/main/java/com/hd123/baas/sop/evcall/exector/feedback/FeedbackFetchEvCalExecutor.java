package com.hd123.baas.sop.evcall.exector.feedback;

import com.google.common.collect.Lists;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.service.api.basedata.category.Category;
import com.hd123.baas.sop.service.api.basedata.category.CategoryFilter;
import com.hd123.baas.sop.service.api.basedata.category.CategoryService;
import com.hd123.baas.sop.service.api.basedata.goods.Goods;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsFilter;
import com.hd123.baas.sop.service.api.basedata.goods.GoodsService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.OrgConfig;
import com.hd123.baas.sop.service.api.feedback.Feedback;
import com.hd123.baas.sop.service.api.feedback.FeedbackService;
import com.hd123.baas.sop.service.api.feedback.FeedbackState;
import com.hd123.baas.sop.service.api.feedback.RsToFeedback;
import com.hd123.baas.sop.common.OrgConstants;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.remote.rssos.feedback.RsFeedback;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yu lilin on 2020/11/23
 */
@Slf4j
@Component
public class FeedbackFetchEvCalExecutor extends AbstractEvCallExecutor<FeedbackFetchMsg> {
  public static final String FEEDBACK_FETCH_EXECUTOR_ID = FeedbackFetchEvCalExecutor.class.getSimpleName();

  @Autowired
  private FeedbackService feedbackService;
  @Autowired
  private GoodsService goodsService;
  @Autowired
  private CategoryService categoryService;
  @Value("${sop-service.feedback.category.targetLevel:2}")
  private int targetLevel;
  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private StoreService storeService;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  protected void doExecute(FeedbackFetchMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    String id = message.getId();
    String shop = message.getShop();

    // 根据id获取反馈单
    BaasResponse<RsFeedback> response = feignClientMgr.getClient(tenant, null, RsSOSClient.class)
        .feedbackGet(tenant, shop, id);
    if (!response.isSuccess()) {
      throw new Exception(response.getMsg());
    }

    RsFeedback rsFeedback = response.getData();
    if (rsFeedback == null) {
      throw new Exception(MessageFormat.format("单据标识为{0}的收货质量反馈单不存在。", id));
    }
    if (!FeedbackState.submitted.equals(rsFeedback.getState())) {
      log.info(MessageFormat.format("单据标识为{0}的收货质量反馈单的状态不为已提交。", id));
      return;
    }

    String billId = feedbackService.isExists(id);
    if (!StringUtil.isNullOrBlank(billId)) {
      log.info(MessageFormat.format("单据标识为{0}的收货质量反馈单已存在。", id));
      return;
    }
    Feedback feedback = RsToFeedback.getInstance().convert(response.getData());

    if (isExcludeModule(tenant)){
      feedback.setOrgId(OrgConstants.DEFAULT_MAS_ORG_ID);
    }else {
      //查询门店
      StoreFilter storeFilter = new StoreFilter();
      storeFilter.setIdIn(Lists.newArrayList(shop));
      QueryResult<Store> storeQueryResult = storeService.query(tenant, storeFilter);
      if (CollectionUtils.isEmpty(storeQueryResult.getRecords())){
        throw new Exception(MessageFormat.format("单据标识为{0}的收货质量反馈单，门店{1}不存在。", id,shop));
      }
      Store store = storeQueryResult.getRecords().get(0);
      feedback.setOrgId(store.getOrgId());
    }
    // 设置商品所属类别名称和代码
    GoodsFilter filter = new GoodsFilter();
    List<String> ids = new ArrayList<>();
    ids.add(feedback.getGdUuid());
    filter.setFetchParts(Goods.PART_CATEGORY);
    filter.setIdIn(ids);
    QueryResult<Goods> queryResult = goodsService.query(tenant, filter);

    if (CollectionUtils.isEmpty(queryResult.getRecords())) {
      throw new Exception(MessageFormat.format("Id为{0}的商品不存在。", feedback.getGdUuid()));
    }
    Goods goods = queryResult.getRecords().get(0);
    Category category = goods.getCategory();

    String gdTypeCode = "";
    StringBuilder gdTypeName = new StringBuilder();

    if (category != null) {
      Map<String, Category> categoryMap = new HashMap<>();
      categoryMap.put(category.getCode(), category);

      List<String> categoryUpperIds = new ArrayList<>();

      int limitLevel = Math.min(category.getLevel(), targetLevel);
      String[] paths = splitPath(category.getPath());
      for (int i = 0; i < limitLevel; i++) {
        categoryUpperIds.add(paths[i]);
      }

      if (CollectionUtils.isNotEmpty(categoryUpperIds)) {
        CategoryFilter categoryFilter = new CategoryFilter();
        categoryFilter.setIdIn(categoryUpperIds);
        QueryResult<Category> categories = categoryService.query(tenant, categoryFilter);
        if (CollectionUtils.isNotEmpty(categories.getRecords())) {
          categories.getRecords().stream().forEach(c -> {
            categoryMap.put(c.getCode(), c);
          });
        }
      }

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

    feedback.setGdTypeCode(gdTypeCode);
    feedback.setGdTypeName(gdTypeName.toString());
    feedbackService.save(feedback);
  }

  private String[] splitPath(String path) {
    if (path.startsWith("/")) {
      path = path.substring(1);
    }
    return path.split("/");
  }

  @Override
  protected FeedbackFetchMsg decodeMessage(String msg) throws BaasException {
    log.info("收到FeedbackFetchMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, FeedbackFetchMsg.class);
  }


  private boolean isExcludeModule(String tenant) {
    OrgConfig config = configClient.getConfig(tenant, OrgConfig.class);
    if (config.getExcludeModules() == null) {
      return false;
    }
    String[] excludeModules = config.getExcludeModules().split(String.valueOf(','));
    for (String module : excludeModules) {
      if (module.equals(OrgConstants.EXCLUDE_FEEDBACK)) {
        return true;
      }
    }
    return false;
  }

}
