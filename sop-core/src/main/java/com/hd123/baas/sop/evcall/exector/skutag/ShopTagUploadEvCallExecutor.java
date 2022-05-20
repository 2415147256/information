package com.hd123.baas.sop.evcall.exector.skutag;

import java.util.ArrayList;
import java.util.List;

import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.basedata.sku.Sku;
import com.hd123.baas.sop.service.api.basedata.sku.SkuService;
import com.hd123.baas.sop.service.api.skutag.ShopTag;
import com.hd123.baas.sop.service.dao.skutag.ShopTagDaoBof;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.skutag.StoreGdTag;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class ShopTagUploadEvCallExecutor extends AbstractEvCallExecutor<ShopTagUploadMsg> {

  public static final String SHOP_TAG_UPLOAD_EXECUTOR_ID = ShopTagUploadEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private SkuService skuService;
  @Autowired
  private ShopTagDaoBof shopTagDao;

  public final static String DEFAULT_ORG_TYPE = "-";

  @Override
  protected void doExecute(ShopTagUploadMsg message, EvCallExecutionContext context) throws Exception {
    try {
      String tenant = message.getTenant();
      String skuId = message.getSkuId();
      String shop = message.getShop();
      String orgId = message.getOrgId();

      Sku sku = skuService.get(tenant, DEFAULT_ORG_TYPE, DefaultOrgIdConvert.toMasDefOrgId(orgId), skuId);
      if (sku == null) {
        throw new BaasException("商品ID<{0}>不存在", skuId);
      }
      List<ShopTag> tags = shopTagDao.getTags(tenant, orgId, skuId, shop);
      if (CollectionUtils.isEmpty(tags)) {
        log.info("删除商品<{}>门店<{}>的标签", skuId, shop);
      } else {
        log.info("更新商品<{}>门店<{}>的标签", skuId, shop);
      }
      List<String> tagIds = new ArrayList<>();
      List<StoreGdTag> storeGdTags = new ArrayList<>();
      StoreGdTag storeGdTag = new StoreGdTag();
      storeGdTag.setGdGid(Integer.parseInt(sku.getGoodsGid()));
      storeGdTag.setStoreGid(Integer.parseInt(shop));
      if (CollectionUtils.isNotEmpty(tags)) {
        for (ShopTag shopTag : tags) {
          tagIds.add(shopTag.getTagId());
        }
      }
      storeGdTag.setTagIds(tagIds);
      storeGdTags.add(storeGdTag);
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> skuShopTagsUpload = rsH6SOPClient.skuShopTagsUpload(tenant, storeGdTags);
      if (!skuShopTagsUpload.isSuccess()) {
        throw new BaasException("调用h6标签上传接口失败");
      }
    } catch (Exception e) {
      log.error("TagDeleteEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected ShopTagUploadMsg decodeMessage(String msg) throws BaasException {
    log.info("ShopTagUploadMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ShopTagUploadMsg.class);
  }

}
