package com.hd123.baas.sop.evcall.exector.voice;

import com.google.common.collect.Lists;
import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.config.ExplosiveActivityVoiceConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfig;
import com.hd123.baas.sop.service.api.sysconfig.SysConfigService;
import com.hd123.baas.sop.service.api.voice.*;
import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivity;
import com.hd123.baas.sop.service.api.pms.explosive.ExplosiveActivitySignJoin;
import com.hd123.baas.sop.service.dao.explosive.ExplosiveActivityDao;
import com.hd123.baas.sop.service.api.promotion.PromotionJoinUnits;
import com.hd123.baas.sop.redis.RedisService;
import com.hd123.baas.sop.remote.fms.FmsV2Client;
import com.hd123.baas.sop.remote.fms.bean.BMsgPushTemplate;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployee;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployeeFilter;
import com.hd123.baas.sop.remote.uas.BUser;
import com.hd123.baas.sop.remote.uas.UasClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author maodapeng
 * @Since
 */
@Slf4j
@Component
public class ActivityVoiceEvCallExecutor extends AbstractEvCallExecutor<ActivityVoiceNotifyMsg> {

  public static final String ACTIVITY_VOICE_EXECUTOR_ID = ActivityVoiceEvCallExecutor.class.getSimpleName();
  @Autowired
  private RedisService redisService;
  @Autowired
  private ExplosiveActivityDao activityDao;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private StoreService storeService;
  @Autowired
  private VoiceService voiceService;
  @Autowired
  private UasClient uasClient;
  @Autowired
  private RsMasClient rsMasClient;
  @Autowired
  private SysConfigService sysConfigService;
  @Autowired
  private FmsV2Client fmsV2Client;

  @Override
  protected void doExecute(ActivityVoiceNotifyMsg message, EvCallExecutionContext context) throws Exception {
    Assert.notNull(message.getTenant(), "tenant");
    Assert.notNull(message.getNotifyDate(), "notifyDate");
    log.info("??????{}????????????????????????????????????????????????{}", message.getTenant(), message.getNotifyDate());
    String tenant = message.getTenant();
    SysConfig timeSysConfig = sysConfigService.get(tenant, SysConfig.EXPLOSIVE_ACTIVITY_VOICE_TIME);
    if (timeSysConfig == null) {
      log.warn("???????????????????????????????????????");
      return;
    }
    Integer voiceNotifyTime = Integer.valueOf(timeSysConfig.getCfValue());
    ExplosiveActivityVoiceConfig config = configClient.getConfig(tenant, ExplosiveActivityVoiceConfig.class);
    if (config.getRoleId() == null) {
      log.warn("?????????????????????????????????");
      return;
    }
    Date callDate = DateUtil.add(message.getNotifyDate(), Calendar.MINUTE, voiceNotifyTime);
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ExplosiveActivity.Queries.SIGN_RANGE_END_DATE, Cop.BETWEEN_CLOSED_CLOSED, message.getNotifyDate(),
        callDate);
    qd.addByField(ExplosiveActivity.Queries.STATE, Cop.EQUALS, ExplosiveActivity.State.audited.name());
    List<String> fetchParts = new ArrayList<>();
    fetchParts.add(ExplosiveActivity.PARTS_JOIN_UNITS);
    fetchParts.add(ExplosiveActivity.PARTS_SIGN_JOINS);
    QueryResult<ExplosiveActivity> query = activityDao.query(tenant, qd, fetchParts);

    List<ExplosiveActivity> activities = query.getRecords();
    if (CollectionUtils.isEmpty(activities)) {
      log.info("??????{}?????????????????????????????????", tenant);
      return;
    }

    for (ExplosiveActivity activity : activities) {
      String billNumber = activity.getBillNumber();
      String value = redisService.get(billNumber);
      log.info("??????<>?????????????????????:{}", activity.getName(), value);
      if (value != null) {
        continue;
      }
      log.info("?????????<{}>,??????????????????", activity.getName());
      redisService.set(billNumber, "1", 24, TimeUnit.HOURS);

      try {
        call(activity);
      } catch (Exception e) {
        log.error("?????????????????????????????????{}", JsonUtil.objectToJson(e));
      }

    }
  }

  private void call(ExplosiveActivity activity) throws BaasException {
    String tenant = activity.getTenant();
    ExplosiveActivityVoiceConfig config = configClient.getConfig(tenant, ExplosiveActivityVoiceConfig.class);
    PromotionJoinUnits joinUnits = activity.getJoinUnits();
    Set<UCN> unSignStore;
    if (joinUnits.getAllUnit()) {
      StoreFilter filter = new StoreFilter();
      filter.setOrgIdEq(DefaultOrgIdConvert.toMasDefOrgId(activity.getOrgId()));
      unSignStore = storeService.query(activity.getTenant(), filter)
          .getRecords()
          .stream()
          .map(store -> new UCN(store.getId(), store.getCode(), store.getName()))
          .collect(Collectors.toSet());
    } else {
      unSignStore = joinUnits.getStores()
          .stream()
          .map(s -> new UCN(s.getUuid(), s.getCode(), s.getName()))
          .collect(Collectors.toSet());
    }
    List<ExplosiveActivitySignJoin> signJoins = activity.getSignJoins();
    Set<UCN> signStore = signJoins.stream().map(ExplosiveActivitySignJoin::getStore).collect(Collectors.toSet());
    if (CollectionUtils.isNotEmpty(signJoins)) {
      unSignStore.removeAll(signStore);
    }
    log.info("????????????????????????{}", JsonUtil.objectToJson(unSignStore));
    if (CollectionUtils.isNotEmpty(unSignStore)) {
      String roleId = config.getRoleId();// ??????
      // ????????????
      List<BUser> bUsers = queryByRoleId(tenant, roleId);
      if (CollectionUtils.isEmpty(bUsers)) {
        log.error("??????[{}]???????????????[{}]", tenant, roleId);
        return;
      }
      // ??????????????????
      final VoiceTemplate voiceTemplate = getTemplate(tenant, VoiceTemplateCode.EXPLOSIVE_ACTIVITY.name());
      Voice voice = new Voice();
      voice.setTenant(tenant);
      voice.setTitle(activity.getName());
      voice.setTemplateId(voiceTemplate.getUuid());
      voice.setRequestId(UUID.randomUUID().toString());
      voice.setTemplateCode(voiceTemplate.getCode());
      voice.setTemplateContent(voiceTemplate.getContent());
      voice.setCreated(new Date());
      voice.setLines(new ArrayList<>());
      for (UCN ucn : unSignStore) {
        VoiceLine line = new VoiceLine();
        line.setTenant(tenant);
        BUser userByRoleId = getUserByRoleId(tenant, bUsers, ucn.getUuid());
        if (userByRoleId == null) {
          log.warn("??????{}???????????????", ucn.getUuid());
          continue;
        }
        if (userByRoleId.getMobile() == null) {
          log.warn("??????{}????????????????????????", tenant);
          continue;
        }
        //mobile???????????????linkMan??????????????????
        String callee = userByRoleId.getLinkMan();
        line.setCallee(callee);
        line.setShop(ucn);
        Map<String, String> map = new HashMap<>();
        int time = DateUtil.get(activity.getSignRange().getEndDate(), Calendar.HOUR_OF_DAY);
        map.put("time", String.valueOf(time));
        line.setTemplateParams(map);
        voice.getLines().add(line);
      }
      log.info("???????????????????????????{}", JsonUtil.objectToJson(voice));
      voiceService.call(tenant, voice, getSysOperateInfo());
    }

  }

  private VoiceTemplate getTemplate(String tenant, String templateCode) throws BaasException {
    BaasResponse<BMsgPushTemplate> response = fmsV2Client.getTemplateByCode(tenant, templateCode);
    if (response.isSuccess() && response.getData() == null) {
      log.error("??????{}?????????????????????", templateCode);
      throw new BaasException("??????{}?????????????????????", templateCode);
    }
    return response.getData().toVoiceTemplate();
  }

  private BUser getUserByRoleId(String tenant, List<BUser> users, String shop) {
    List<String> shopUserIds = queryStoreEmployee(tenant, shop);
    if (CollectionUtils.isEmpty(shopUserIds) || CollectionUtils.isEmpty(users)) {
      return null;
    }
    for (String shopUserId : shopUserIds) {
      for (BUser user : users) {
        if (shopUserId.equals(user.getUuid())) {
          return user;
        }
      }
    }
    return null;
  }

  // ???????????????
  private List<BUser> queryByRoleId(String tenant, String roleId) throws BaasException {
    QueryRequest request = new QueryRequest();
    request.setStart(0);
    request.setLimit(10000);
    request.setFilters(new ArrayList<>());
    FilterParam param = new FilterParam();
    param.setProperty("roleId:=");
    param.setValue(roleId);
    request.getFilters().add(param);
    List<String> fetchParts = Lists.newArrayList();
    fetchParts.add("role");
    request.setFetchParts(fetchParts);
    String appId = "sopWeb"; // todo ?????????
    BaasResponse<List<BUser>> query = uasClient.query(tenant, appId, request);
    return query.getData();
  }

  // ??????????????????ID??????
  private List<String> queryStoreEmployee(String tenant, String shop) {
    RsEmployeeFilter filter = new RsEmployeeFilter();
    filter.setStoreIdEq(shop);
    filter.setPageSize(10000);
    RsMasPageResponse<List<RsEmployee>> response = rsMasClient.employeeQuery(tenant, filter);
    if (!response.isSuccess()) {
      log.warn("??????{}????????????????????????", shop, JsonUtil.objectToJson(response));
      return null;
    }
    if (CollectionUtils.isEmpty(response.getData())) {
      log.warn("??????{}????????????????????????", shop);
      return null;
    }
    return response.getData().stream().map(RsEmployee::getUserId).collect(Collectors.toList());
  }

  @Override
  protected ActivityVoiceNotifyMsg decodeMessage(String msg) throws BaasException {
    log.info("??????ActivityVoiceNotifyMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, ActivityVoiceNotifyMsg.class);
  }
}
