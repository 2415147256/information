package com.hd123.baas.sop.evcall.exector.subsidyplan;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.basedata.area.Area;
import com.hd123.baas.sop.service.api.basedata.area.AreaService;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.config.DingTalkTemplateConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.dingtalk.DingTalkLinkMsg;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.FmsMsg;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasResponse;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.uas.UasClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class SubsidyPlanCallExecutor extends AbstractEvCallExecutor<SubsidyPlanMsg> {

  public static final String SUBSIDY_PLAN_EXECUTOR_ID = SubsidyPlanCallExecutor.class.getSimpleName();
  //public static final String TEMPLATE_ID = "910020";
  public static final String MSG_CONTEXT = "msgContext";

  protected static final String DEFAULT_ORG_TYPE = "-";

  @Autowired
  private UasClient uasClient;
  @Autowired
  private RsMasClient rsMasClient;
  @Autowired
  private AreaService areaService;
  @Autowired
  private SysConfigService sysConfigService;
  @Autowired
  private FmsClient fmsClient;
  @Value("${sop-service.appId}")
  private String appId;
  @Autowired
  private BaasConfigClient baasConfigClient;

  @Override
  protected void doExecute(SubsidyPlanMsg message, EvCallExecutionContext context) throws Exception {
    log.info("SubsidyPlanCallExecutor doExecute --- 补贴计划执行器开始执行，msg={}", JSONUtil.safeToJson(message));
    Assert.notNull(message.getTenant(), "tenant");
    Assert.notNull(message.getShop(), "shop");
    Assert.notNull(message.getDingTalkLinkMsg(), "dingTalkLinkMsg");
    String tenant = message.getTenant();
    String shop = message.getShop();
    String orgId = message.getOrgId();
    try {
      BaasResponse<RsStore> baasResponse = covertBaasResponse(rsMasClient.storeGet(tenant, DEFAULT_ORG_TYPE, orgId, shop));
      if (!baasResponse.isSuccess() || baasResponse.getData() == null) {
        log.error("未查询到门店信息,tenant={},orgType={},orgId={},shop={},baasResponse={}", tenant, DEFAULT_ORG_TYPE, orgId, shop, JSONUtil.safeToJson(baasResponse));
        return;
      }
      List<Store> storeResult = convertStore(tenant, Collections.singletonList(baasResponse.getData()));
      log.info("门店信息，store={}", JSONUtil.safeToJson(storeResult));
      Store store = storeResult.get(0);

      BaasResponse<String> userResponse = uasClient.getMobileByAreaId(tenant, store.getArea().getUuid());
      if (!userResponse.isSuccess()) {
        log.error("查询用户信息失败,tenant={},userResponse={}", tenant, JSONUtil.safeToJson(userResponse));
        throw new BaasException("查询督导电话失败");
      }
      if (StringUtils.isBlank(userResponse.getData())) {
        log.error("未查询到督导电话");
        return;
      }
      String mobile = userResponse.getData();
      DingTalkTemplateConfig baasConfig = baasConfigClient.getConfig(tenant, DingTalkTemplateConfig.class);
      if (baasConfig == null || StringUtils.isEmpty(baasConfig.getTemplateId())) {
        throw new BaasException("获取模板配置失败");
      }
      FmsMsg fmsMsg = initFmsParams(message, mobile, baasConfig);
      if (fmsMsg != null) {
        BaasResponse rsp = fmsClient.dingtalkSend(tenant, fmsMsg);
        if (!rsp.success) {
          log.error("调用FMS发送消息失败,内容: <{}>, 错误原因: <{}>", BaasJSONUtil.safeToJson(fmsMsg), rsp.getCode());
          throw new BaasException("调用FMS发送消息失败");
        }
        log.info("调用FMS发送消息成功,内容: <{}>", BaasJSONUtil.safeToJson(fmsMsg));
      }
    } catch (BaasException e) {
      log.error("SubsidyPlanCallExecutor错误:{}", JsonUtil.objectToJson(e));
      throw e;
    }
  }

  private FmsMsg initFmsParams(SubsidyPlanMsg message, String mobile, DingTalkTemplateConfig baasConfig) {
    if (StringUtils.isNotBlank(mobile)) {
      FmsMsg fmsMsg = new FmsMsg();
      fmsMsg.setAppId(appId);
      fmsMsg.setTemplateId(baasConfig.getTemplateId());
      fmsMsg.setTarget(Arrays.asList(mobile));
      fmsMsg.getTemplateParams().put(MSG_CONTEXT, JsonUtil.objectToJson(message.getDingTalkLinkMsg()));
      return fmsMsg;
    } else {
      log.warn("手机号不存在，忽略发送消息");
      return null;
    }
  }

  @Override
  protected SubsidyPlanMsg decodeMessage(String msg) throws BaasException {
    log.info("补贴计划通知消息SubsidyPlanMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, SubsidyPlanMsg.class);
  }

  protected BaasResponse covertBaasResponse(RsMasResponse rsMasResponse) {
    BaasResponse response = new BaasResponse();
    response.setMsg(rsMasResponse.getEchoMessage());
    response.setCode(rsMasResponse.getEchoCode() == 0 ? 2000 : rsMasResponse.getEchoCode());
    response.setSuccess(rsMasResponse.isSuccess());
    response.setData(rsMasResponse.getData());
    return response;
  }

  protected List<Store> convertStore(String tenant, List<RsStore> rsStores)
      throws BaasException {
    QueryResult<Area> areaQueryResult = areaService.queryByMas(tenant, null);
    Map<String, Area> areaMap = areaQueryResult.getRecords().stream().collect(
        Collectors.toMap(Area::getCode, Function.identity(), (existing, replacement) -> existing));

    List<Store> storeList = new ArrayList<>();
    if (!org.apache.commons.collections4.CollectionUtils.isEmpty(rsStores)) {
      for (RsStore source : rsStores) {
        Store target = new Store();
        BeanUtils.copyProperties(source, target);
        if (source.getState() != null) {
          target.setState(source.getState().name());
        }
        if (source.getBusinessState() != null) {
          target.setBusinessState(source.getBusinessState().name());
        }
        UCN area = new UCN(null, source.getArea(), null);
        if (areaMap.containsKey(source.getArea())) {
          Area areaByMas = areaMap.get(source.getArea());
          if (areaByMas != null) {
            area.setUuid(areaByMas.getId());
            area.setName(areaByMas.getName());
          }
        }
        target.setTelephone(source.getContact() == null ? "" : source.getContact().getTelephone());
        target.setArea(area);
        getStoreAddress(source, target);
        storeList.add(target);
      }
    }
    return storeList;
  }

  private void getStoreAddress(RsStore source, Store target) {
    if (source.getAddress() != null) {
      StringBuffer address = new StringBuffer();
      if (!StringUtil.isNullOrBlank(source.getAddress().getProvinceName())) {
        address.append(source.getAddress().getProvinceName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getCityName())) {
        address.append(source.getAddress().getCityName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getDistrictName())) {
        address.append(source.getAddress().getDistrictName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getStreetName())) {
        address.append(source.getAddress().getStreetName());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getDetailAddress())) {
        address.append(source.getAddress().getDetailAddress());
      }
      if (!StringUtil.isNullOrBlank(address.toString())) {
        target.setAddress(address.toString());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getLongitude())) {
        target.setLongitude(source.getAddress().getLongitude());
      }
      if (!StringUtil.isNullOrBlank(source.getAddress().getLatitude())) {
        target.setLatitude(source.getAddress().getLatitude());
      }
    }
  }

  public static void main(String[] args) {
    DingTalkLinkMsg msg = new DingTalkLinkMsg();
    DingTalkLinkMsg.Link link = new DingTalkLinkMsg.Link();
    link.setMessageUrl("http://www.baidu.com");
    link.setPicUrl("http://www.baidu.com/pic");
    link.setText("测试");
    link.setTitle("标题");
    msg.setLink(link);
    System.out.println(JsonUtil.objectToJson(msg));
  }
}
