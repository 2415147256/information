package com.hd123.baas.sop.service.impl.skutag;

import com.google.common.collect.Lists;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.skutag.ShopTag;
import com.hd123.baas.sop.service.api.skutag.SkuShopTag;
import com.hd123.baas.sop.service.api.skutag.SkuShopTagService;
import com.hd123.baas.sop.service.api.skutag.SkuTagSummary;
import com.hd123.baas.sop.service.api.skutag.Tag;
import com.hd123.baas.sop.service.api.skutag.TagService;
import com.hd123.baas.sop.service.dao.skutag.ShopTagDaoBof;
import com.hd123.baas.sop.service.dao.skutag.SkuTagSummaryDaoBof;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.skutag.ShopTagUploadEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.ShopTagUploadMsg;
import com.hd123.baas.sop.evcall.exector.skutag.SkuTagChangeEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.skutag.SkuTagChangeMsg;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @Author maodapeng
 * @Since
 */
@Service
public class SkuShopTagServiceImpl implements SkuShopTagService {
  @Autowired
  private ShopTagDaoBof shopTagDao;
  @Autowired
  private SkuTagSummaryDaoBof skuTagSummaryDao;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private TagService tagService;
  @Autowired
  private SkuService skuService;

  public final static String DEFAULT_ORG_ID = "-";
  public final static String DEFAULT_ORG_TYPE = "-";

  @Override
  public QueryResult<SkuTagSummary> summary(String tenant, List<String> orgIds, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    return skuTagSummaryDao.query(tenant, orgIds, qd);
  }

  @Override
  public SkuTagSummary get(String tenant, String orgId, String skuId) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(skuId, "skuId");
    Sku sku = skuService.get(tenant, DEFAULT_ORG_TYPE, DefaultOrgIdConvert.toMasDefOrgId(orgId), skuId);
    if (sku == null) {
      throw new BaasException("商品不存在");
    }
    int countShopNum = shopTagDao.countShopNum(tenant, orgId, skuId);
    SkuTagSummary summary = new SkuTagSummary();
    summary.setTenant(tenant);
    summary.setSkuId(skuId);
    summary.setSkuName(sku.getName());
    summary.setSkuCode(sku.getCode());
    summary.setSkuQpc(sku.getQpc());
    summary.setOrgId(orgId);
    summary.setShopNum(countShopNum);
    return summary;
  }

  @Override
  @Tx
  public void saveNew(String tenant, SkuShopTag skuShopTag, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(skuShopTag.getOrgId(), "orgId");
    Assert.notNull(skuShopTag.getSkuId(), "skuId");
    Assert.notNull(skuShopTag.getShop(), "shop");
    Assert.notEmpty(skuShopTag.getTags(), "tags");
    shopTagDao.deleteBySkuId(tenant, skuShopTag.getOrgId(), skuShopTag.getSkuId(), skuShopTag.getShop());
    List<ShopTag> shopTags = buildShopTags(tenant, skuShopTag, operateInfo);
    shopTagDao.batchInsert(tenant, shopTags);
    sendSkuShopTagUploadMsg(tenant,skuShopTag.getOrgId(), skuShopTag.getSkuId(), skuShopTag.getShop());
    sendSkuTagChangMsg(tenant, skuShopTag.getOrgId(), skuShopTag.getSkuId());
  }

  @Override
  @Tx
  public void batchSaveNew(String tenant, List<SkuShopTag> tags, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notEmpty(tags);
    Assert.notNull(operateInfo);
    for (SkuShopTag skuShopTag : tags) {
      this.saveNew(tenant, skuShopTag, operateInfo);
    }
  }

  @Override
  @Tx
  public void update(String tenant, SkuShopTag tag, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant);
    this.saveNew(tenant, tag, operateInfo);
  }

  @Override
  @Tx
  public void delete(String tenant, String orgId, String skuId, String shop) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(skuId, "skuId");
    Assert.notNull(shop, "shop");
    shopTagDao.deleteBySkuId(tenant, orgId, skuId, shop);

    sendSkuShopTagUploadMsg(tenant, orgId, skuId, shop);
    sendSkuTagChangMsg(tenant, orgId, skuId);
  }

  @Override
  public QueryResult<SkuShopTag> query(String tenant, String orgId, String skuId, QueryDefinition qd) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(orgId, "orgId");
    Assert.notNull(skuId, "skuId");
    QueryResult<SkuShopTag> result = shopTagDao.query(tenant, orgId, skuId, qd);
    if (CollectionUtils.isNotEmpty(result.getRecords())) {
      List<Tag> tags = tagService.list(tenant,null, Lists.newArrayList(orgId));
      Map<Integer, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getUuid, s -> s));
      for (SkuShopTag record : result.getRecords()) {
        List<ShopTag> shopTags = shopTagDao.getTags(tenant, orgId, skuId, record.getShop());
        List<Tag> ts = new ArrayList<>();
        for (ShopTag shopTag : shopTags) {
          Tag tag = tagMap.get(Integer.parseInt(shopTag.getTagId()));
          ts.add(tag);
        }
        record.setTags(ts);
      }
    }
    return result;
  }

  private void sendSkuShopTagUploadMsg(String tenant,String orgIg, String skuId, String shop) {
    ShopTagUploadMsg msg = new ShopTagUploadMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgIg);
    msg.setSkuId(skuId);
    msg.setShop(shop);
    msg.setTraceId(UUID.randomUUID().toString());
    publisher.publishForNormal(ShopTagUploadEvCallExecutor.SHOP_TAG_UPLOAD_EXECUTOR_ID, msg);
  }


  private void sendSkuTagChangMsg(String tenant, String orgId, String skuId) {
    SkuTagChangeMsg msg = new SkuTagChangeMsg();
    msg.setTenant(tenant);
    msg.setOrgId(orgId);
    msg.setSkuId(skuId);
    msg.setTraceId(UUID.randomUUID().toString());
    publisher.publishForNormal(SkuTagChangeEvCallExecutor.SKU_TAG_CHANGE_EXECUTOR_ID, msg);
  }

  private List<ShopTag> buildShopTags(String tenant, SkuShopTag skuShopTag, OperateInfo operateInfo) {
    List<ShopTag> shopTags = new ArrayList<>();
    for (Tag tag : skuShopTag.getTags()) {
      ShopTag shopTag = new ShopTag();
      shopTag.setTenant(tenant);
      shopTag.setOrgId(skuShopTag.getOrgId());
      shopTag.setSkuId(skuShopTag.getSkuId());
      shopTag.setShop(skuShopTag.getShop());
      shopTag.setShopCode(skuShopTag.getShopCode());
      shopTag.setShopName(skuShopTag.getShopName());
      shopTag.setTagId(String.valueOf(tag.getUuid()));
      shopTag.setCreateInfo(operateInfo);
      shopTag.setLastModifyInfo(operateInfo);
      shopTags.add(shopTag);
    }
    return shopTags;
  }
}
