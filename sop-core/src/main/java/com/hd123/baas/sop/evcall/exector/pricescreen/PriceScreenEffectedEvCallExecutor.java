package com.hd123.baas.sop.evcall.exector.pricescreen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.screen.*;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.screen.MkhScreenClient;
import com.hd123.baas.sop.remote.screen.RSBanner;
import com.hd123.baas.sop.remote.screen.RSScheme;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class PriceScreenEffectedEvCallExecutor extends AbstractEvCallExecutor<PriceScreenEffectedMsg> {

  public static final String PRICE_SCREEN_EFFECTED_EXECUTOR_ID = PriceScreenEffectedEvCallExecutor.class
      .getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private PriceScreenService priceScreenService;

  @Override
  protected void doExecute(PriceScreenEffectedMsg msg, EvCallExecutionContext context) throws Exception {
    Assert.notNull(msg.getTenant(), "tenant");
    Assert.notNull(msg.getUuid(), "uuid");
    PriceScreen priceScreen = priceScreenService.get(msg.getTenant(), msg.getUuid());
    if (priceScreen == null) {
      log.error("价格屏方案<{}>不存在", priceScreen.getUuid());
      return;
    }
    RSScheme options = new RSScheme();
    options.setOrgUuid(DefaultOrgIdConvert.toH6DefOrgId(priceScreen.getOrgId()));
    options.setId(priceScreen.getUuid());
    options.setName(priceScreen.getContentName());
    options.setAllStore(priceScreen.getAllShops() ? 1 : 0);
    if (CollectionUtils.isNotEmpty(priceScreen.getShops())) {
      List<String> shops = priceScreen.getShops().stream().map(PriceScreenShop::getShop).collect(Collectors.toList());
      options.setStoreUuids(shops);
    }
    options.setBeginTime(priceScreen.getEffectiveStartTime());
    options.setEndTime(priceScreen.getEffectiveEndTime());
    options.setCreateTime(priceScreen.getCreateInfo().getTime());
    options.setCreator(priceScreen.getCreateInfo().getOperator().getFullName());
    List<Content> contents = JsonUtil.jsonToList(priceScreen.getContent(), Content.class);
    if (CollectionUtils.isNotEmpty(contents)) {
      for (Content content : contents) {
        if (ContentType.ROTATION_IMG.name().equals(content.getType())) {
          List<String> cnts = content.getCnts();
          List<RSBanner> rsBanners = new ArrayList<>();
          for (String cnt : cnts) {
            RSBanner rsBanner = new RSBanner();
            String fileName = cnt.substring(cnt.lastIndexOf("/") + 1);
            rsBanner.setFileName(fileName);
            rsBanner.setFileUrl(cnt);
            rsBanners.add(rsBanner);
          }
          options.setBanners(rsBanners);
        }
        if (ContentType.TIP_LANGUAGE.name().equals(content.getType())) {
          options.setTips(content.getCnts());
        }
      }
    }
    MkhScreenClient mkhScreenClient = feignClientMgr.getClient(msg.getTenant(), null, MkhScreenClient.class);
    BaasResponse<Void> response = mkhScreenClient.saveScheme(msg.getTenant(), options);
    if (!response.isSuccess()) {
      throw new BaasException(response.getCode(), response.getMsg());
    }
  }

  @Override
  protected PriceScreenEffectedMsg decodeMessage(String msg) throws BaasException {
    log.info("收到PriceScreenEffectedMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, PriceScreenEffectedMsg.class);
  }
}
