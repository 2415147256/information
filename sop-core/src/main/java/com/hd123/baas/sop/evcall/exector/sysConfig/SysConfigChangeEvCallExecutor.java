package com.hd123.baas.sop.evcall.exector.sysConfig;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hd123.baas.sop.service.api.sysconfig.FeedbackAutoAuditConfig;
import com.hd123.baas.sop.service.api.sysconfig.FeedbackGrade;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.api.feedback.FeedbackOption;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.feedback.RsH6FeedbackFeeGenerateTime;
import com.hd123.baas.sop.remote.rsh6sop.fineRule.FineRule;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyAutoAudit;
import com.hd123.baas.sop.remote.rsh6sop.ordadvlmtdate.OrdAdvLmtDate;
import com.hd123.baas.sop.remote.rsh6sop.systemconfig.H6SystemConfig;
import com.hd123.baas.sop.remote.rssos.RsSOSClient;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackEndTimeSaver;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackGrade;
import com.hd123.baas.sop.remote.rssos.feedback.BSOPFeedbackGradeSaver;
import com.hd123.baas.sop.remote.rssos.feedback.FeedbackEndTime;
import com.hd123.baas.sop.remote.rssos.receipt.ReceiptOption;
import com.hd123.baas.sop.remote.rssos.require.RequirePeriodOption;
import com.hd123.baas.sop.remote.screen.*;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.mpa.api.common.JSONUtil;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.spms.commons.calendar.DateRange;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.entity.BUcn;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author W.J.H.7
 */
@Slf4j
@Component
public class SysConfigChangeEvCallExecutor extends AbstractEvCallExecutor<SysConfigChangeEvCallMsg> {

  public static final String SYSCONFIG_CHANGE_EXECUTOR_ID = SysConfigChangeEvCallExecutor.class.getSimpleName();

  @Autowired
  private SysConfigService sysConfigService;
  @Autowired
  private FeignClientMgr feignClientMgr;

  @Override
  protected void doExecute(SysConfigChangeEvCallMsg message, EvCallExecutionContext context) throws Exception {
    String tenant = message.getTenant();
    String cfKey = message.getCfgKey();
    // 有orgId,存储的是orgId
    String spec = message.getSpec();
    Assert.notNull(tenant);
    Assert.notNull(cfKey);
    Assert.notNull(spec);

    SysConfig item = sysConfigService.get(tenant, spec, cfKey);
    RsSOSClient rsSOSClient = feignClientMgr.getClient(tenant, null, RsSOSClient.class);
    RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
    if (item != null) {
      String sosTenant = getSosTenant(tenant);
      String sopTenant = getSopTenant(tenant);
      // 门店调拨自动审核
      if (SysConfig.KEY_AUTO_AUDITOF_INV_XF.equalsIgnoreCase(item.getCfKey())) {
        RsInvXFApplyAutoAudit req = new RsInvXFApplyAutoAudit();
        req.setAutoAudit(Boolean.parseBoolean(item.getCfValue()));
        BaasResponse<Void> response = rsH6SOPClient.saveAutoAudit(sopTenant, req);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 费用生成时间
      if (SysConfig.KEY_FEE_GENERATE_TIME.equalsIgnoreCase(item.getCfKey())) {
        RsH6FeedbackFeeGenerateTime req = new RsH6FeedbackFeeGenerateTime();
        req.setFeeGenerateTime((DateUtils.parseDate(item.getCfValue(), SysConfig.DATE_FORMAT)));
        BaasResponse<Void> response = rsH6SOPClient.feedbackSaveFeeGenerateTime(sopTenant, req);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 收获质量反馈截至时间和反馈时间设置
      if (SysConfig.KEY_FEEDBACK_ENDTIME.equalsIgnoreCase(item.getCfKey())) {
        SysConfig item2 = sysConfigService.get(tenant, spec, SysConfig.KEY_FEEDBACK_ENDTIME);
        SysConfig item3 = sysConfigService.get(tenant, spec, SysConfig.KEY_FEEDBACK_DAYS);
        BSOPFeedbackEndTimeSaver req = new BSOPFeedbackEndTimeSaver();
        List<FeedbackEndTime> endTimes = new ArrayList<>(2);
        Map<String, FeedBackEndTime> feedbackEndTimeMap = new HashMap<>(0);
        Map<String, FeedBackDays> feedBackDaysMap = new HashMap<>(0);
        if (item2 != null) {
          List<FeedBackEndTime> feedbackEndTimes = JsonUtil.jsonToList(item2.getCfValue(), FeedBackEndTime.class);
          feedbackEndTimeMap = feedbackEndTimes.stream().collect(Collectors.toMap(FeedBackEndTime::getSource, o -> o));
        }
        if (item3 != null) {
          List<FeedBackDays> feedBackDays = JsonUtil.jsonToList(item3.getCfValue(), FeedBackDays.class);
          feedBackDaysMap = feedBackDays.stream().collect(Collectors.toMap(FeedBackDays::getSource, o -> o));
        }
        // 设置截止时间和反馈时间
        for (Map.Entry<String, FeedBackEndTime> entity : feedbackEndTimeMap.entrySet()) {
          FeedbackEndTime feedbackEndTime = new FeedbackEndTime();
          feedbackEndTime.setCode(entity.getKey());
          feedbackEndTime.setFeedbackEndTime(entity.getValue().getEndTime());
          feedbackEndTime.setFeedbackEndDays(feedBackDaysMap.get(entity.getKey()).getDays());
          endTimes.add(feedbackEndTime);
        }
        req.setEndTimes(endTimes);
        BaasResponse<Void> response = rsSOSClient.feedbackSaveEndTime(sosTenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false), req);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 门店要货时间范围
      if (SysConfig.SHOP_GET_GOOD_TIME_AROUND_START.equalsIgnoreCase(item.getCfKey())
          || SysConfig.SHOP_GET_GOOD_OFF_SET_DAY.equalsIgnoreCase(item.getCfKey())
          || SysConfig.SHOP_GET_GOOD_TIME_AROUND_END.equalsIgnoreCase(item.getCfKey())
          || SysConfig.GET_GOOD_NOT_LIMIT_STORES.equalsIgnoreCase(item.getCfKey())) {
        SysConfig getGoodStartTime = sysConfigService.get(tenant, spec, SysConfig.SHOP_GET_GOOD_TIME_AROUND_START);
        SysConfig getGoodsOffsetDay = sysConfigService.get(tenant, spec, SysConfig.SHOP_GET_GOOD_OFF_SET_DAY);
        SysConfig getGoodsEndTime = sysConfigService.get(tenant, spec, SysConfig.SHOP_GET_GOOD_TIME_AROUND_END);
        SysConfig getGoodNotLimitStores = sysConfigService.get(tenant, spec, SysConfig.GET_GOOD_NOT_LIMIT_STORES);
        //推送给店务
        RequirePeriodOption req = new RequirePeriodOption();
        req.setEndTime(getGoodsEndTime.getCfValue());
        if (getGoodStartTime != null) {
          req.setStartTime(getGoodStartTime.getCfValue());
        }
        if (getGoodsOffsetDay != null) {
          req.setOffsetDays(Integer.valueOf(getGoodsOffsetDay.getCfValue()));
        }
        if (getGoodNotLimitStores != null) {
          List<BUcn> stores = JSON.parseArray(getGoodNotLimitStores.getCfValue(), BUcn.class);
          List<String> notLimitStores = new ArrayList<>();
          if (CollectionUtils.isNotEmpty(stores)) {
            for (BUcn store : stores) {
              notLimitStores.add(store.getUuid());
            }
            req.setExcludeStores(notLimitStores);
          }
        }
        log.info("SOS服务请求参数req={}", JSONUtil.safeToJson(req));
        BaasResponse<Void> response = rsSOSClient.requireSavePeriodOption(sopTenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false), req);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }

        // 推送给H6
        H6SystemConfig config = new H6SystemConfig();
        config.setOrgGid(Integer.parseInt(DefaultOrgIdConvert.toH6DefOrgId(item.getSpec(), true)));
        List<H6SystemConfig.Item> items = new ArrayList<>();
        H6SystemConfig.Item startItem = new H6SystemConfig.Item();
        startItem.setKey("orderStartTime");
        startItem.setValue(getGoodStartTime.getCfValue());
        H6SystemConfig.Item endItem = new H6SystemConfig.Item();
        endItem.setKey("orderEndTime");
        endItem.setValue(getGoodsEndTime.getCfValue());
        items.add(startItem);
        items.add(endItem);
        config.setConfigs(items);
        BaasResponse<Void> h6Response = rsH6SOPClient.systemConfigSave(tenant, config);
        if (!h6Response.isSuccess()) {
          throw new BaasException(h6Response.getCode(), h6Response.getMsg());
        }
      }
        // 加单要货原因
      if (SysConfig.SHOP_REASON_ADD.equalsIgnoreCase(item.getCfKey())) {
        BaasResponse<Void> response = rsSOSClient.requireSaveApplyAddReason(sopTenant, DefaultOrgIdConvert.toH6DefOrgId(spec,false),
            JsonUtil.jsonToList(item.getCfValue(), String.class));
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 收货单实收是否允许大于实配数
      if (SysConfig.RECEIVE_NOTE_MORE_REAL_COUNT.equalsIgnoreCase(item.getCfKey())) {
        ReceiptOption req = new ReceiptOption();
        req.setReceiptQtyAllowExcAlloc(Boolean.valueOf(item.getCfValue()));
        BaasResponse<Void> response = rsSOSClient.receiptSaveOption(sopTenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false), req);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 报损原因
      if (SysConfig.SHOP_REASON_DAMAGES.equalsIgnoreCase(item.getCfKey())) {
        BaasResponse<Void> response = rsSOSClient.requireSaveApplyLossReason(sopTenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false),
            JsonUtil.jsonToList(item.getCfValue(), String.class));
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 报溢原因
      if (SysConfig.SHOP_REASON_MORE.equalsIgnoreCase(item.getCfKey())) {
        BaasResponse<Void> response = rsSOSClient.requireSaveApplyOverReason(sopTenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false),
            JsonUtil.jsonToList(item.getCfValue(), String.class));
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }

      // 罚息规则
      if (SysConfig.FINE_RULE.equalsIgnoreCase(item.getCfKey())) {
        FineRule fineRule = JsonUtil.jsonToObject(item.getCfValue(), FineRule.class);
        BaasResponse<Void> response = rsH6SOPClient.fineRuleSave(tenant, fineRule);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }

      // 门店叫货停止配置
      if (SysConfig.SHOP_ORDER_STOP_DATE.equalsIgnoreCase(item.getCfKey())) {
        OrdAdvLmtDate ordAdvLmtDate = new OrdAdvLmtDate();
        ordAdvLmtDate.setStoreCode(item.getSpec());
        List<DateRange> dateRanges = JsonUtil.jsonToList(item.getCfValue(), DateRange.class);
        ordAdvLmtDate.setData(dateRanges);
        BaasResponse<Void> response = rsH6SOPClient.ordAdvLmtDateAccept(tenant, ordAdvLmtDate);
        if (!response.isSuccess()) {
          throw new BaasException("调用H6接口失败");
        }
      }

      // 价格屏-图片轮播
      if (SysConfig.SCREEN_PICTURE_CAROUSEL_SECONDS.equalsIgnoreCase(item.getCfKey())) {
        RSSecondsOption option = new RSSecondsOption();
        // setStore(option, item.getSpec());
        option.setType("banner");
        option.setCarouselSeconds(Integer.parseInt(item.getCfValue()));
        MkhScreenClient mkhScreenClient = feignClientMgr.getClient(tenant, null, MkhScreenClient.class);
        BaasResponse<Void> response = mkhScreenClient.savePriceOption(tenant, option);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      if (SysConfig.SCREEN_PICTURE_URLS.equalsIgnoreCase(item.getCfKey())) {
        List<String> urls = JsonUtil.jsonToList(item.getCfValue(), String.class);
        RSBannerOption option = new RSBannerOption();
        setStore(option, item.getSpec());
        List<RSBanner> details = new ArrayList<>();
        for (String url : urls) {
          String fileName = url.substring(url.lastIndexOf("/") + 1);
          RSBanner rsBanner = new RSBanner();
          rsBanner.setFileUrl(url);
          rsBanner.setFileName(fileName);
          details.add(rsBanner);
        }
        option.setDetails(details);
        MkhScreenClient mkhScreenClient = feignClientMgr.getClient(tenant, null, MkhScreenClient.class);
        BaasResponse<Void> response = mkhScreenClient.saveBannerOption(tenant, option);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 价格屏-广告语
      if (SysConfig.SCREEN_TIPS.equalsIgnoreCase(item.getCfKey())) {
        List<String> contents = JsonUtil.jsonToList(item.getCfValue(), String.class);
        RSTipOption rsTipOption = new RSTipOption();
        setStore(rsTipOption, item.getSpec());
        List<RSTip> tips = new ArrayList<>();
        for (String content : contents) {
          RSTip tip = new RSTip();
          tip.setContent(content);
          tips.add(tip);
        }
        rsTipOption.setDetails(tips);
        MkhScreenClient mkhScreenClient = feignClientMgr.getClient(tenant, null, MkhScreenClient.class);
        BaasResponse<Void> response = mkhScreenClient.saveTipOption(tenant, rsTipOption);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      if (SysConfig.SCREEN_TIP_CAROUSEL_SECONDS.equalsIgnoreCase(item.getCfKey())) {
        RSSecondsOption option = new RSSecondsOption();
        option.setType("tip");
        option.setCarouselSeconds(Integer.parseInt(item.getCfValue()));
        MkhScreenClient mkhScreenClient = feignClientMgr.getClient(tenant, null, MkhScreenClient.class);
        BaasResponse<Void> response = mkhScreenClient.savePriceOption(tenant, option);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 价格屏-价格表
      if (SysConfig.SCREEN_PRICE_CAROUSEL_SECONDS.equalsIgnoreCase(item.getCfKey())) {
        RSSecondsOption option = new RSSecondsOption();
        option.setType("price");
        option.setCarouselSeconds(Integer.parseInt(item.getCfValue()));
        MkhScreenClient mkhScreenClient = feignClientMgr.getClient(tenant, null, MkhScreenClient.class);
        BaasResponse<Void> response = mkhScreenClient.savePriceOption(tenant, option);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      // 质量反馈单保存选项配置：
      if (SysConfig.ENABLE_CHOOSE_IMAGE.equalsIgnoreCase(item.getCfKey()) || SysConfig.FEEDBACK_AUTO_AUDIT.equalsIgnoreCase(item.getCfKey())) {
        SysConfig item2 = sysConfigService.get(tenant, spec, SysConfig.ENABLE_CHOOSE_IMAGE);
        SysConfig item3 = sysConfigService.get(tenant, spec, SysConfig.TIME_LMT_MODE);
        SysConfig item4 = sysConfigService.get(tenant, spec, SysConfig.FEEDBACK_GOODS_SCOPE);
        SysConfig autoAuditSysConfig = sysConfigService.get(tenant, spec, SysConfig.FEEDBACK_AUTO_AUDIT);
        FeedbackOption option = new FeedbackOption();
        if (autoAuditSysConfig != null) {
          FeedbackAutoAuditConfig autoAuditConfig = JSON.parseObject(autoAuditSysConfig.getCfValue(), FeedbackAutoAuditConfig.class);
          option.setAutoAudit(autoAuditConfig.getEnable());

        }
        option.setEnableChooseImage(Boolean.parseBoolean(item2.getCfValue()) ? 1 : 0);
        option.setTimeLmtMode(Integer.parseInt(item3.getCfValue()));
        option.setGoodsScope(Integer.parseInt(item4.getCfValue()));
        BaasResponse<String> response = rsSOSClient.feedbackSaveOption(tenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false), option);
        if (!response.isSuccess()) {
          throw new BaasException(response.getCode(), response.getMsg());
        }
      }
      if (SysConfig.FEEDBACK_GRADES.equalsIgnoreCase(item.getCfKey())) {
        SysConfig sysConfig = sysConfigService.get(tenant, spec, SysConfig.FEEDBACK_GRADES);
        if (sysConfig.getCfValue() != null) {
          List<FeedbackGrade> feedbackGrades = JSON.parseArray(sysConfig.getCfValue(), FeedbackGrade.class);
          BSOPFeedbackGradeSaver saver = new BSOPFeedbackGradeSaver();
          List<BSOPFeedbackGrade> sosFeedbackGrades = new ArrayList<>();
          feedbackGrades.forEach(s -> sosFeedbackGrades.add(new BSOPFeedbackGrade(s.getId(), s.getName())));
          saver.setGrades(sosFeedbackGrades);
          log.info("推送质量反馈单到sos:{}", JSON.toJSONString(saver));
          BaasResponse<Void> response = rsSOSClient.feedbackGradeSave(tenant, DefaultOrgIdConvert.toH6DefOrgId(spec, false), saver);
          if (!response.isSuccess()) {
            throw new BaasException(response.getCode(), response.getMsg());
          }
        }
      }
    }
  }

  private void setStore(RSOption rsOption, String shop) {
    if (!shop.equalsIgnoreCase("def")) {
      rsOption.setStoreUuids(Lists.newArrayList(shop));
    }
  }

  @Override
  protected SysConfigChangeEvCallMsg decodeMessage(String arg) {
    return JsonUtil.jsonToObject(arg, SysConfigChangeEvCallMsg.class);
  }

  private String getSosTenant(String tenant) {
    // 不需要转换
    return tenant;
  }

  private String getSopTenant(String tenant) {
    // 不需要转换
    return tenant;
  }

}
