package com.hd123.baas.sop.service.impl.pomdata;

import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.dao.pomdata.PromSingleProductDao;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.api.pomdata.PrmPriceQueryRequest;
import com.hd123.baas.sop.service.api.pomdata.PromSingleProductService;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.service.api.promotion.PromotionType;
import com.hd123.baas.sop.service.api.promotion.condition.DateRangeCondition;
import com.hd123.baas.sop.service.api.promotion.condition.ProductCondition;
import com.hd123.baas.sop.utils.ZipUtils;
import com.hd123.rumba.commons.biz.entity.Entity;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryOrderDirection;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.oss.api.Bucket;
import com.hd123.spms.commons.calendar.DateRange;
import com.hd123.spms.commons.util.CollectionUtil;
import com.hd123.spms.commons.util.FileUtils;
import com.hd123.spms.service.bill.SingleProduct;
import com.qianfan123.baas.common.BaasException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PromSingleProductServiceImpl implements PromSingleProductService {
  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private PromSingleProductDao promSingleProductDao;
  @Autowired
  private StoreService storeService;
  @Autowired(required = false)
  private Bucket bucket;

  public static void main(String[] args) throws ParseException {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(StringUtil.toDate("2021-2-28", "yyyy-MM-dd"));
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    System.out.println(dayOfWeek);
    List<DateRangeCondition.DayOfWeek> list = new ArrayList<>();
    list.add(DateRangeCondition.DayOfWeek.friday);
    System.out.println(list.contains(DateRangeCondition.DayOfWeek.values()[dayOfWeek]));
  }

  @Override
  public String generalZip(String tenant, int spanDays) throws Exception {
    Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    Date endDate = DateUtils.addDays(startDate, spanDays + 1);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(PromRule.Queries.BEGIN_DATE, Cop.LESS, endDate);
    qd.addByField(PromRule.Queries.END_DATE, Cop.GREATER_OR_EQUALS, startDate);
    qd.addByField(PromRule.Queries.STARTER_ORG_UUID, Cop.EQUALS, "-");
    qd.addByField(PromRule.Queries.STATE, Cop.EQUALS, PromRule.State.effect.name());
    qd.addByField(PromRule.Queries.PROMOTION_TYPE, Cop.EQUALS, PromotionType.price.name());
    qd.addByField(PromRule.Queries.TIME_PERIOD_CONDITION, Cop.IS_NULL);
    qd.addOrder(PromRule.Queries.LAST_MODIFIED, QueryOrderDirection.desc);
    qd.setPage(0);
    qd.setPageSize(0);
    List<PromRule> ruleList = promRuleDao.query(tenant, qd, Arrays.asList(PromRule.PART_JOIN_UNITS, PromRule.PART_PROMOTION)).getRecords();
    return generalZip(tenant, ruleList, null, null, spanDays, "FULL");
  }

  @Override
  public String generalZip(PromRule target, ProductCondition productCondition, PromotionJoinUnits joinUnits, int spanDays) throws Exception {
    PromRule rule = promRuleDao.get(target.getTenant(), target.getUuid(), PromRule.ALL_PARTS);
    List<PromRule> ruleList = new ArrayList<>();
    Set<ProductCondition.Item> gooditems = new HashSet<>();
    Set<String> storeUuids = new HashSet<>();
    if (rule != null) {
      if (rule.getState() == PromRule.State.effect) {
        ruleList.add(rule);
      }
      if (rule.getPromotion().getProductCondition() != null) {
        gooditems.addAll(rule.getPromotion().getProductCondition().getItems());
      }
      if (rule.getJoinUnits().getAllUnit() == Boolean.TRUE) {
        storeUuids = null;
      } else {
        for (UCN store : rule.getJoinUnits().getStores()) {
          storeUuids.add(store.getUuid());
        }
      }
    }
    if (productCondition != null) {
      gooditems.addAll(productCondition.getItems());
    }
    if (storeUuids != null && joinUnits != null) {
      if (joinUnits.getAllUnit() == Boolean.TRUE) {
        storeUuids = null;
      } else {
        for (UCN store : joinUnits.getStores()) {
          storeUuids.add(store.getUuid());
        }
      }
    }
    if (gooditems.isEmpty() == false) {
      Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
      Date endDate = DateUtils.addDays(startDate, spanDays + 1);
      for (List<ProductCondition.Item> partList : CollectionUtil.sizeBy(gooditems, 100)) {
        List<String> gdGids = partList.stream().map(Entity::getUuid).collect(Collectors.toList());
        List<String> billUuids = promSingleProductDao.query4H6Task(target.getTenant(), startDate, endDate, gdGids);
        ruleList.addAll(promRuleDao.gets(target.getTenant(), billUuids, PromRule.ALL_PARTS));
      }
    }
    return generalZip(target.getTenant(), ruleList, gooditems, storeUuids, spanDays, "ADD");
  }

  @Override
  public List<PrmPriceQueryRequest.ProductLine> queryPrmPrice(String tenant, PrmPriceQueryRequest request) {
    List<String> gdGids = request.getLines().stream().map(PrmPriceQueryRequest.ProductLine::getGdGid).collect(Collectors.toList());
    List<SingleProduct> products = promSingleProductDao.query4Prm(tenant, request.getStoreUuid(), request.getTargetDate(), gdGids);
    Map<String, SingleProduct> productMap = new HashMap<>();
    for (SingleProduct product : products) {
      String key = product.getEntityUuid() + "_" + product.getQpc().stripTrailingZeros().toPlainString();
      SingleProduct target = productMap.computeIfAbsent(key, k -> product);
      if (product.getAuditTime().after(target.getAuditTime())) {
        productMap.put(key, product);
      }
    }

    for (PrmPriceQueryRequest.ProductLine line : request.getLines()) {
      String key = line.getGdGid() + "_" + line.getGdQpc().stripTrailingZeros().toPlainString();
      SingleProduct product = productMap.get(key);
      if (product != null) {
        line.setPrmPrice(product.getPromValue());
      }
    }
    return request.getLines();
  }

  private String generalZip(String tenant, List<PromRule> ruleList, Collection<ProductCondition.Item> goodItems, Collection<String> storeUuids, int spanDays, String type) throws Exception {
    File dir = File.createTempFile("PROMOTION_", String.valueOf(System.nanoTime()));
    File zipFile = File.createTempFile("PROMOTION_", System.nanoTime() + ".zip");
    if (dir.exists()) {
      dir.delete();
    }
    dir.mkdir();

    try {
      List<PromotionJoinUnits.JoinUnit> allUnits = null;
      Date startDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
      List<String> gdGids = goodItems == null ? null : goodItems.stream().map(Entity::getUuid).collect(Collectors.toList());
      for (int i = 0; i <= spanDays; i++) {
        Date targetDate = DateUtils.addDays(startDate, i);
        String fileName = MessageFormat.format("PROMOTION_{0}_{1}.csv", type, StringUtil.dateToString(targetDate, "yyyyMMdd"));
        File csvFile = new File(dir, fileName);

        ruleList.sort((o1, o2) -> o2.getLastModifyInfo().getTime().compareTo(o1.getLastModifyInfo().getTime()));
        Map<String, CsvRowData> rowDataMap = new HashMap<>();
        if (goodItems != null) {
          if (storeUuids == null) {
            if (allUnits == null) {
              allUnits = queryAllUnits(tenant);
            }
            storeUuids = allUnits.stream().map(Entity::getUuid).collect(Collectors.toList());
          }
          for (String storeUuid : storeUuids) {
            for (ProductCondition.Item item : goodItems) {
              String key = MessageFormat.format("{0}_{1}_{2}", storeUuid, item.getUuid(), item.getQpc().stripTrailingZeros().toPlainString());
              CsvRowData newData = new CsvRowData();
              newData.setStoreGid(storeUuid);
              newData.setGdGid(item.getUuid());
              newData.setQpc(item.getQpc());
              newData.setTargetDate(targetDate);
              CsvRowData rowData = rowDataMap.put(key, newData);
            }
          }
        }

        for (PromRule rule : ruleList) {
          // 只有促销价的规则，才会产下发任务
          if (rule.getPromotion().getPromotionType() != PromotionType.price) {
            continue;
          } else if (rule.getTimePeriodCondition() != null) {
            continue;
          } else if ("-".equals(rule.getStarterOrgUuid()) == false) {
            continue;
          }

          if (rule.getDateRangeCondition().getDateRange().include(targetDate) == false) {
            continue;
          } else if (acceptTargetDate(rule.getDateRangeCondition(), targetDate) == false) {
            continue;
          }

          List<PromotionJoinUnits.JoinUnit> stores = rule.getJoinUnits().getStores();
          if (rule.getJoinUnits().getAllUnit() == Boolean.TRUE) {
            if (allUnits == null) {
              allUnits = queryAllUnits(tenant);
            }
            stores = allUnits;
          }
          if (CollectionUtils.isEmpty(stores)) {
            continue;
          }
          for (UCN store : stores) {
            if (storeUuids != null && storeUuids.contains(store.getUuid()) == false) {
              continue; // 不包含的门店，就不需要生成了
            }
            if (rule.getPromotion().getProductCondition() != null && rule.getPromotion().getProductCondition().getItems() != null) {
              int lineNo = 0;
              for (ProductCondition.Item item : rule.getPromotion().getProductCondition().getItems()) {
                PromData promData = new PromData();
                promData.setBillNumber(rule.getBillNumber());
                promData.setItemUuid(MessageFormat.format("{0}_{1}_{2}", rule.getTenant(), rule.getBillNumber(), String.valueOf(lineNo++)));
                promData.setDateRange(rule.getDateRangeCondition().getDateRange());
                promData.setUpdateTime(rule.getLastModifyInfo().getTime());
                promData.setPrmPrice(item.getPrmPrice());
                if (gdGids != null && gdGids.contains(item.getUuid()) == false) {
                  continue; // 不包含的商品，就不需要生成了
                }
                String key = MessageFormat.format("{0}_{1}_{2}", store.getUuid(), item.getUuid(), item.getQpc().stripTrailingZeros().toPlainString());
                CsvRowData rowData = rowDataMap.computeIfAbsent(key, k -> {
                  CsvRowData newData = new CsvRowData();
                  newData.setStoreGid(store.getUuid());
                  newData.setGdGid(item.getUuid());
                  newData.setQpc(item.getQpc());
                  newData.setTargetDate(targetDate);
                  return newData;
                });
                if (rule.getOnlyMember() == Boolean.TRUE) {
                  // 会员专享
                  if (rowData.getPromData2() == null) {
                    rowData.setPromData2(promData);
                  }
                } else {
                  if (rowData.getPromData1() == null) {
                    rowData.setPromData1(promData);
                  }
                  if (rowData.getPromData2() == null) {
                    rowData.setPromData2(promData);
                  }
                }
              }
            }
          }
        }
        try (FileOutputStream outputStream = new FileOutputStream(csvFile)) {
          for (CsvRowData rowData : rowDataMap.values()) {
            rowData.write(outputStream);
          }
          outputStream.flush();
        }
      }
      ZipUtils.zip(dir, zipFile);

      String key = "tempOf7Day" + "/" + tenant + "/shop_price/" + zipFile.getName();
      bucket.put(key, new FileInputStream(zipFile));
      return bucket.getUrl(key, Bucket.CONTENT_TYPE_OF_WILDCARD);
    } finally {
      FileUtils.deleteDir(dir);
      zipFile.delete();
    }
  }

  private boolean acceptTargetDate(DateRangeCondition dateRangeCondition, Date targetDate) {
    if (dateRangeCondition.getTimeCycle() == null) {
      return true;
    }
    if (dateRangeCondition.getTimeCycle().getBy() == DateRangeCondition.By.week) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(targetDate);
      int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
      return dateRangeCondition.getTimeCycle().getDayOfWeek().contains(DateRangeCondition.DayOfWeek.values()[dayOfWeek]);
    }
    return false;
  }

  private List<PromotionJoinUnits.JoinUnit> queryAllUnits(String tenant) throws BaasException {
    StoreFilter filter = new StoreFilter();
    return storeService.query(tenant, filter).getRecords().stream()
        .map(store -> new PromotionJoinUnits.JoinUnit(store.getId(), store.getCode(), store.getName()))
        .collect(Collectors.toList());
  }

  @Data
  public static class CsvRowData {
    private String orgId;
    private String storeGid;
    private String gdGid;
    private BigDecimal qpc;
    private Date targetDate;
    private PromData promData1;
    private PromData promData2;

    public void write(OutputStream outputStream) throws IOException {
      List<String> list = new ArrayList<String>();
      list.add(storeGid); // 门店Gid
      list.add(gdGid);// 商品Gid
      list.add(qpc.stripTrailingZeros().toPlainString());// 规格
      list.add(StringUtil.dateToString(targetDate, "yyyy-MM-dd"));// 促销日期
      list.add(promData1 != null ? promData1.getBillNumber() : ""); // 促销单号
      list.add(promData1 != null ? promData1.getItemUuid() : ""); // 促销条目 uuid,
      list.add(promData1 != null ? StringUtil.dateToString(promData1.getDateRange().getBeginDate(), "yyyy-MM-dd HH:mm:ss") : ""); // 促销开始时间,
      list.add(promData1 != null ? StringUtil.dateToString(promData1.getDateRange().getEndDate(), "yyyy-MM-dd HH:mm:ss") : "");// 促销结束时间,
      list.add(promData1 != null ? StringUtil.dateToString(promData1.getUpdateTime(), "yyyy-MM-dd HH:mm:ss") : "");// 最后修改时间
      list.add(promData1 != null ? promData1.getPrmPrice().toString() : "");// 促销价
      list.add(promData2 != null ? promData2.getBillNumber() : ""); // 促销单号
      list.add(promData2 != null ? promData2.getItemUuid() : ""); // 促销条目 uuid,
      list.add(promData2 != null ? StringUtil.dateToString(promData2.getDateRange().getBeginDate(), "yyyy-MM-dd HH:mm:ss") : ""); // 促销开始时间,
      list.add(promData2 != null ? StringUtil.dateToString(promData2.getDateRange().getEndDate(), "yyyy-MM-dd HH:mm:ss") : "");// 促销结束时间,
      list.add(promData2 != null ? StringUtil.dateToString(promData2.getUpdateTime(), "yyyy-MM-dd HH:mm:ss") : "");// 最后修改时间
      list.add(promData2 != null ? promData2.getPrmPrice().toString() : "");// 促销价
      outputStream.write((StringUtils.join(list, ",") + "\n").getBytes());
    }
  }

  @Data
  public static class PromData {
    private String billNumber;
    private String itemUuid;
    private DateRange dateRange;
    private Date updateTime;
    private BigDecimal prmPrice;
  }
}
