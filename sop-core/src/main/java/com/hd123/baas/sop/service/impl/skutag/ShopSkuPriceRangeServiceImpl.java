package com.hd123.baas.sop.service.impl.skutag;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.dao.basedata.ShopSkuPriceRangeDaoBof;
import com.hd123.baas.sop.service.api.skutag.ShopSkuPriceRange;
import com.hd123.baas.sop.service.api.skutag.ShopSkuPriceRangeService;
import com.hd123.baas.sop.service.api.skutag.Tag;
import com.hd123.baas.sop.service.dao.skutag.TagDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ShopSkuPriceRangeServiceImpl implements ShopSkuPriceRangeService {

  @Autowired
  private ShopSkuPriceRangeDaoBof shopSkuPriceRangeDao;
  @Autowired
  private TagDaoBof tagDao;

  public static final byte DELETE_TAG = 1;

  @Tx
  @Override
  public String saveNew(String tenant, ShopSkuPriceRange priceRange, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceRange, "priceRange");
    priceRange.setCreateInfo(operateInfo);
    priceRange.setLastModifyInfo(operateInfo);
    return shopSkuPriceRangeDao.insert(tenant, priceRange);
  }

  @Tx
  @Override
  public void saveModify(String tenant, ShopSkuPriceRange priceRange, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(priceRange, "priceRange");
    ShopSkuPriceRange history = shopSkuPriceRangeDao.get(tenant, priceRange.getUuid(), true);
    if (history == null) {
      throw new BaasException("价格带不存在uuid=" + priceRange.getUuid() + ",无法修改");
    }
    if (history.getDeleted() == DELETE_TAG) {
      throw new BaasException("价格带已删除uuid=" + priceRange.getUuid() + ",无法修改");
    }
    priceRange.setCreateInfo(history.getCreateInfo());
    priceRange.setLastModifyInfo(operateInfo);
    shopSkuPriceRangeDao.update(tenant, priceRange);
  }

  @Tx
  @Override
  public void batchSave(String tenant, List<ShopSkuPriceRange> priceRanges, OperateInfo operateInfo) {
    Assert.notNull(tenant);
    Assert.notEmpty(priceRanges, "priceRanges");
    priceRanges.forEach(i -> {
      i.setCreateInfo(operateInfo);
      i.setLastModifyInfo(operateInfo);
    });
    shopSkuPriceRangeDao.batchInsert(tenant, priceRanges);
  }

  @Tx
  @Override
  public void delete(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    ShopSkuPriceRange history = shopSkuPriceRangeDao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("价格带不存在uuid=" + uuid + ",无法删除");
    }
    if (history.getDeleted() == DELETE_TAG) {
      log.info("价格带已删除，忽略，uuid={}", uuid);
      return;
    }
    history.setDeleted(DELETE_TAG);
    history.setLastModifyInfo(operateInfo);
    shopSkuPriceRangeDao.update(tenant, history);
  }

  @Override
  public ShopSkuPriceRange get(String tenant, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    ShopSkuPriceRange result = shopSkuPriceRangeDao.get(tenant, uuid, false);
    if (result == null) {
      log.error("价格带不存在uuid={}", uuid);
      return null;
    }
    List<String> list = JSONUtil.safeToObject(result.getTagIds(), List.class);
    List<Integer> tagIds = new ArrayList<>();
    for (String s : list) {
      tagIds.add(Integer.valueOf(s));
    }
    if (CollectionUtils.isNotEmpty(tagIds)) {
      List<Tag> tags = tagDao.listByUuids(tenant, tagIds);
      result.setTags(tags);
    }
    return result;
  }

  @Override
  public List<ShopSkuPriceRange> listBySkuIds(String tenant, String shopId, List<String> skuIds) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shopId, "shopId");

    if (CollectionUtils.isEmpty(skuIds)) {
      return new ArrayList<>();
    }

    return shopSkuPriceRangeDao.list(tenant, shopId, skuIds);
  }

  @Override
  public QueryResult<ShopSkuPriceRange> query(String tenant, QueryDefinition qd, String... fetchParts) {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(qd, "qd");
    QueryResult<ShopSkuPriceRange> result = shopSkuPriceRangeDao.query(tenant, qd);
    if (fetchParts == null || fetchParts.length == 0) {
      return result;
    }
    //级联查询
    fetch(tenant, result.getRecords(), fetchParts);
    return result;
  }

  private void fetch(String tenant, List<ShopSkuPriceRange> item, String... fetchParts) {
    if (CollectionUtils.isEmpty(item)) {
      return;
    }
    List<String> fetchList = Arrays.asList(fetchParts);
    if (fetchList.contains(ShopSkuPriceRange.PART_TAGS)) {
      item.stream().forEach(i -> {
        List<Integer> tagIds = new ArrayList<>();
        String ids = i.getTagIds();
        List<String> list = null;
        try {
          list = JSONUtil.safeToObject(ids, List.class);
        } catch (BaasException e) {
          log.error("transfer to json error");
        }
        if (CollectionUtils.isNotEmpty(list)) {
          for (String s : list) {
            tagIds.add(Integer.valueOf(s));
          }
          List<Tag> tags = tagDao.listByUuids(tenant, tagIds);
          i.setTags(tags);
        }
      });
    }
  }
}
