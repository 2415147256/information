package com.hd123.baas.sop.service.impl.todo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.remote.fms.FmsClient;
import com.hd123.baas.sop.remote.fms.bean.AppMessageSaveNewReq;
import com.hd123.baas.sop.remote.fms.bean.BAppMessage;
import com.hd123.baas.sop.remote.fms.bean.BAppMessageExt;
import com.hd123.baas.sop.remote.fms.bean.MediaInfo;
import com.hd123.baas.sop.remote.rsmas.RsMasClient;
import com.hd123.baas.sop.remote.rsmas.RsMasPageResponse;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployee;
import com.hd123.baas.sop.remote.rsmas.employee.RsEmployeeFilter;
import com.hd123.baas.sop.remote.rsmas.store.RsStore;
import com.hd123.baas.sop.remote.rsmas.store.RsStoreFilter;
import com.hd123.baas.sop.remote.uas.BDataPermission;
import com.hd123.baas.sop.remote.uas.BPermission;
import com.hd123.baas.sop.remote.uas.BPosition;
import com.hd123.baas.sop.remote.uas.BRole;
import com.hd123.baas.sop.remote.uas.BUser;
import com.hd123.baas.sop.remote.uas.UasClient;
import com.hd123.baas.sop.service.api.todo.Constants;
import com.hd123.baas.sop.service.api.todo.CreateTodoInfo;
import com.hd123.baas.sop.service.api.todo.FinishCondition;
import com.hd123.baas.sop.service.api.todo.TargetTypeEnum;
import com.hd123.baas.sop.service.api.todo.TodoScene;
import com.hd123.baas.sop.service.api.todo.TodoSceneService;
import com.hd123.baas.sop.service.api.todo.TodoSetting;
import com.hd123.baas.sop.service.api.todo.TodoSettingService;
import com.hd123.baas.sop.service.dao.todo.TodoSceneDao;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mibo
 */
@Service
@Slf4j
public class TodoSceneServiceImpl implements TodoSceneService {

  @Autowired
  TodoSceneDao todoSceneDao;
  @Autowired
  TodoSettingService todoSettingService;
  @Autowired
  FmsClient fmsClient;
  @Autowired
  RsMasClient rsMasClient;

  @Autowired
  UasClient uasClient;

  private static final Set<String> DIAN_WU_SCENE_CODE = new HashSet<>();
  private static final Set<String> SOP_SCENE_CODE = new HashSet<>();

  // 携带变量名称
  private static final String VAR_NAME = "varName";
  // 门店
  private static final String SHOP = "shop";

  static {
    DIAN_WU_SCENE_CODE.add("TASK_INVENTORY_PLAN");
    DIAN_WU_SCENE_CODE.add("TASK_RECEIVE");
    DIAN_WU_SCENE_CODE.add("TASK_RETURN_DIRECT");
    DIAN_WU_SCENE_CODE.add("TASK_RETURN_NOTICE");
    DIAN_WU_SCENE_CODE.add("TASK_RETURN_ORGANIZATION");
    DIAN_WU_SCENE_CODE.add("TASK_RETURN_UNIFIED");
    DIAN_WU_SCENE_CODE.add("TASK_TRANSFER_IN");
    DIAN_WU_SCENE_CODE.add("TASK_TRANSFER_OUT");

    SOP_SCENE_CODE.add("SHOP_TASK_UNCLAIMED");
    SOP_SCENE_CODE.add("SHOP_TASK_UNDISPOSED");
    SOP_SCENE_CODE.add("SHOP_TASK_TRANSFER");
  }

  @Override
  @Tx
  public String saveNew(String tenant, String orgId, TodoScene todoScene, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(todoScene, "todoScene");
    todoScene.setOrgId(orgId);
    todoScene.setCreateInfo(operateInfo);
    todoScene.setLastModifyInfo(operateInfo);
    if (StringUtils.isEmpty(todoScene.getUuid())) {
      todoScene.setUuid(IdGenUtils.buildRdUuid());
    }
    checkCode(tenant, orgId, null, todoScene.getCode());
    todoSceneDao.insert(tenant, todoScene);
    return todoScene.getUuid();
  }

  @Override
  @Tx
  public void saveModify(String tenant, String orgId, TodoScene todoScene, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(todoScene, "todoScene");
    Assert.hasText(todoScene.getUuid(), "uuid");
    TodoScene history = todoSceneDao.get(tenant, todoScene.getUuid(), true);
    if (history == null) {
      throw new BaasException("场景不存在，无法更新");
    }
    // 校验场景是够被引用，如果引用不允许修改code
    List<TodoSetting> settingHistory = todoSettingService.listBySceneId(tenant, orgId, todoScene.getUuid());
    if (CollectionUtils.isNotEmpty(settingHistory)) {
      throw new BaasException("场景已被引用,不允许修改场景值");
    }
    // 校验code是否重复
    checkCode(tenant, orgId, history.getUuid(), todoScene.getCode());

    todoScene.setOrgId(orgId);
    todoScene.setCreateInfo(history.getCreateInfo());
    todoScene.setLastModifyInfo(operateInfo);
    todoSceneDao.update(tenant, todoScene);
  }

  @Override
  @Tx
  public void delete(String tenant, String orgId, String uuid) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");

    TodoScene history = todoSceneDao.get(tenant, uuid, true);
    if (history == null) {
      throw new BaasException("场景不存在，无法删除");
    }
    // 校验该场景已被引用为一个待办配置或一个消息配置，则该场景不允许被删除。
    List<TodoSetting> settingHistory = todoSettingService.listBySceneId(tenant, orgId, uuid);
    if (CollectionUtils.isNotEmpty(settingHistory)) {
      throw new BaasException("该场景已被引用，无法删除");
    }
    todoSceneDao.delete(tenant, uuid);
  }

  @Override
  public QueryResult<TodoScene> query(String tenant, String orgId, QueryDefinition qd) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(qd, "qd");
    qd.addByField(TodoScene.Queries.ORG_ID, Cop.EQUALS, orgId);
    return todoSceneDao.query(tenant, qd);
  }

  @Override
  public TodoScene get(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    return todoSceneDao.get(tenant, uuid, false);
  }

  @Override
  public TodoScene getByCode(String tenant, String orgId, String code) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.hasText(code, "code");
    return todoSceneDao.getByCode(tenant, orgId, code, false);
  }

  private void checkCode(String tenant, String orgId, String uuid, String code) throws BaasException {
    TodoScene history = todoSceneDao.getByCode(tenant, orgId, code, true);
    if (history != null && !history.getUuid().equals(uuid)) {
      throw new BaasException("场景值不能重复");
    }
  }

  @Override
  @Tx
  public List<String> createTodo(String tenant, String orgId, CreateTodoInfo createTodoInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(orgId, "orgId");
    Assert.notNull(createTodoInfo, "createTodoInfo");
    // 1.根据场景值获取场景
    TodoScene scene = this.getByCode(tenant, orgId, createTodoInfo.getSceneCode());
    if (scene == null) {
      throw new BaasException("场景不存在");
    }
    if (!scene.getSource().name().equals(createTodoInfo.getSource())) {
      throw new BaasException("场景触发方不匹配");
    }

    // 2.根据场景查询待办或者消息设置
    List<TodoSetting> todoSettingList = this.todoSettingService.listBySceneId(tenant, orgId, scene.getUuid());
    if (CollectionUtils.isNotEmpty(todoSettingList)) {
      QueryRequest request = new QueryRequest();
      FilterParam source = new FilterParam();
      source.setProperty("source:=");
      source.setValue(createTodoInfo.getSourceId());
      FilterParam sourceType = new FilterParam();
      sourceType.setProperty("sourceTypeId:in");
      sourceType.setValue(todoSettingList.stream().map(TodoSetting::getUuid).collect(Collectors.toList()));
      request.setFilters(Lists.newArrayList(sourceType, source));
      BaasResponse<List<BAppMessage>> query = fmsClient.query(tenant, request);
      if (query.isSuccess() && CollectionUtils.isNotEmpty(query.getData())) {
        log.info("该任务已生成：{}", JsonUtil.objectToJson(createTodoInfo));
        return Collections.emptyList();
      }
      // 3，根据设置生成消息
      List<AppMessageSaveNewReq> messages = buildAppMessages(tenant, orgId, scene, createTodoInfo, todoSettingList);
      // 4，调用fms
      if (CollectionUtils.isNotEmpty(messages)) {
        fmsClient.batchSave(tenant, orgId, messages);
      } else {
        log.info("根据配置无法生成待办或消息");
      }
    }
    return new ArrayList<>();
  }

  private List<AppMessageSaveNewReq> buildAppMessages(String tenant, String orgId, TodoScene scene,
      CreateTodoInfo createTodoInfo, List<TodoSetting> records) throws BaasException {
    Assert.notNull(createTodoInfo.getSourceId(), "sourceId");
    Assert.hasText(createTodoInfo.getSourceExt(), "ext");
    if (CollectionUtils.isEmpty(records)) {
      return null;
    }
    HashMap<String, String> hashMap = JsonUtil.jsonToObject(createTodoInfo.getSourceExt(), HashMap.class);
    Map<String, RsStore> shopIdMap = new HashMap<>();
    if (TargetTypeEnum.SHOP.name().equals(scene.getTarget().name()) && StringUtils.isNotBlank(scene.getTargetExt())) {
      List<String> shopIds = Collections.singletonList(scene.getTargetExt());
      List<String> target = (List<String>) JsonUtil.jsonToObject(scene.getSourceExt(), HashMap.class)
          .getOrDefault(VAR_NAME, new ArrayList<>());
      if (hashMap == null) {
        // 防止后续出现空指针异常
        hashMap = new HashMap<>();
      }
      if (!hashMap.keySet().containsAll(target)) {
        throw new BaasException("缺少必要的参数：{}", target);
      }
      if (DIAN_WU_SCENE_CODE.contains(scene.getCode()) ||
          SOP_SCENE_CODE.contains(scene.getCode())) {
        if (!hashMap.containsKey(SHOP)) {
          throw new BaasException("缺少必要的参数：{}", SHOP);
        }
        shopIds = Collections.singletonList(hashMap.get(SHOP));
      }
      RsStoreFilter storeFilter = buildStoreFilter(orgId, shopIds);
      RsMasPageResponse<List<RsStore>> storeResult = rsMasClient.storeQuery(tenant, storeFilter);
      if (CollectionUtils.isNotEmpty(storeResult.getData())) {
        storeResult.getData().forEach(store -> shopIdMap.put(store.getId(), store));
      }
    }

    List<AppMessageSaveNewReq> reqList = new ArrayList<>();
    for (TodoSetting todoSetting : records) {
      FinishCondition finishCondition = JsonUtil.jsonToObject(scene.getFinishCondition(), FinishCondition.class);
      // 接受维度为门店，结束维度为岗位
      if (TargetTypeEnum.SHOP.name().equalsIgnoreCase(scene.getTarget().name())
          && TargetTypeEnum.POSITION.name().equalsIgnoreCase(finishCondition.getOverRange().name())) {
        if (shopIdMap.get(hashMap.get(SHOP)) == null) {
          throw new BaasException("门店{}不存在！请检查门店ID是否正确！", hashMap.get(SHOP));
        }
        RsEmployeeFilter filter = new RsEmployeeFilter();
        filter.setStoreIdEq(hashMap.get(SHOP));
        filter.setOrgTypeEq(Constants.ORG_TYPE);
        filter.setOrgIdEq(orgId);
        filter.setPage(0);
        filter.setPageSize(10000);
        RsMasPageResponse<List<RsEmployee>> rsResult = rsMasClient.employeeQuery(tenant, filter);
        if (!rsResult.isSuccess() || CollectionUtils.isEmpty(rsResult.getData())) {
          log.info("{}", JsonUtil.objectToJson(rsResult));
          throw new BaasException("获取门店用户失败，或门店无关联用户！");
        }
        QueryRequest request = new QueryRequest();
        request.setStart(1);
        request.setLimit(9999);
        FilterParam uuidIn = new FilterParam();
        uuidIn.setProperty("uuid:in");
        uuidIn.setValue(rsResult.getData().stream().map(RsEmployee::getId).collect(Collectors.toList()));
        request.setFilters(Collections.singletonList(uuidIn));
        List<BUser> data = uasClient.queryEs(tenant, orgId, request).getData();
        if (CollectionUtils.isNotEmpty(data)) {
          for (BUser user : data) {
            log.info("user:{}", JsonUtil.objectToJson(user));
            if (user.getUserPosition() == null) {
              log.info("用户岗位为空");
              continue;
            }
            List<BPosition> bPositions = user.getUserPosition().getBPositions();
            List<String> collect = bPositions.stream().map(BPosition::getPositionTypeName).collect(Collectors.toList());
            if (!collect.contains(scene.getTargetExt())) {
              log.info("用户岗位不包含 接受岗位:{}", scene.getTargetExt());
              continue;
            }
            AppMessageSaveNewReq req = buildCommonParams(scene, createTodoInfo, todoSetting);
            req.setUserId(user.getLoginId());
            req.setUserName(user.getNickName());
            RsStore rsStore = shopIdMap.get(hashMap.get(SHOP));
            if (rsStore != null) {
              req.setShop(rsStore.getId());
              req.setShopCode(rsStore.getCode());
              req.setShopName(rsStore.getName());
            }
            reqList.add(req);
          }
        } else {
          log.info("配置的门店Id不存在用户,忽略发送");
        }
      }
      // 接收者为门店，结束维度为账号
      if (TargetTypeEnum.SHOP.name().equalsIgnoreCase(scene.getTarget().name())
          && TargetTypeEnum.ACCOUNT.name().equalsIgnoreCase(finishCondition.getOverRange().name())) {
        RsEmployeeFilter filter = new RsEmployeeFilter();
        filter.setStoreIdEq(scene.getTargetExt());
        filter.setOrgTypeEq(Constants.ORG_TYPE);
        filter.setOrgIdEq(orgId);
        filter.setPage(0);
        filter.setPageSize(10000);
        RsMasPageResponse<List<RsEmployee>> rsResult = rsMasClient.employeeQuery(tenant, filter);
        if (CollectionUtils.isNotEmpty(rsResult.getData())) {
          for (RsEmployee rsEmployee : rsResult.getData()) {
            AppMessageSaveNewReq req = buildCommonParams(scene, createTodoInfo, todoSetting);
            req.setUserId(rsEmployee.getCode());
            req.setUserName(rsEmployee.getName());
            RsStore rsStore = shopIdMap.get(scene.getTargetExt());
            if (DIAN_WU_SCENE_CODE.contains(scene.getCode())) {
              rsStore = shopIdMap.get(hashMap.get(SHOP));
              if (rsStore == null) {
                throw new BaasException("门店{}不存在！请检查门店ID是否正确！", hashMap.get(SHOP));
              }
            }
            if (rsStore != null) {
              req.setShop(rsStore.getId());
              req.setShopCode(rsStore.getCode());
              req.setShopName(rsStore.getName());
            }
            reqList.add(req);
          }
        } else {
          log.info("配置的门店Id不存在用户,忽略发送");
        }
      }
      // 接收者为门店，结束维度为门店
      if (TargetTypeEnum.SHOP.name().equalsIgnoreCase(scene.getTarget().name())
          && TargetTypeEnum.SHOP.name().equalsIgnoreCase(finishCondition.getOverRange().name())) {
        AppMessageSaveNewReq req = buildCommonParams(scene, createTodoInfo, todoSetting);
        RsStore rsStore = shopIdMap.get(scene.getTargetExt());
        if (DIAN_WU_SCENE_CODE.contains(scene.getCode())) {
          rsStore = shopIdMap.get(hashMap.get(SHOP));
          if (rsStore == null) {
            throw new BaasException("门店{}不存在！请检查门店ID是否正确！", hashMap.get(SHOP));
          }
        }
        if (rsStore != null) {
          req.setShop(rsStore.getId());
          req.setShopCode(rsStore.getCode());
          req.setShopName(rsStore.getName());
        }
        reqList.add(req);
      }
      // 接收者为账号，结束维度为账号
      if (TargetTypeEnum.ACCOUNT.name().equalsIgnoreCase(scene.getTarget().name())
          && TargetTypeEnum.ACCOUNT.name().equalsIgnoreCase(finishCondition.getOverRange().name())) {
        AppMessageSaveNewReq req = buildCommonParams(scene, createTodoInfo, todoSetting);
        req.setShop("-");
        req.setShopCode("-");
        req.setShopName("-");
        //配置成账号
        req.setUserId(scene.getTargetExt());
        if (SOP_SCENE_CODE.contains(scene.getCode())) {
          String userId = hashMap.get("userId");
          RsEmployeeFilter filter = new RsEmployeeFilter();
          filter.setStoreIdEq(hashMap.get(SHOP));
          filter.setOrgTypeEq(Constants.ORG_TYPE);
          filter.setOrgIdEq(orgId);
          filter.setIdEq(userId);
          filter.setPage(0);
          filter.setPageSize(10000);
          RsMasPageResponse<List<RsEmployee>> rsResult = rsMasClient.employeeQuery(tenant, filter);
          if (CollectionUtils.isNotEmpty(rsResult.getData())) {
            req.setUserId(rsResult.getData().get(0).getCode());
            req.setUserName(rsResult.getData().get(0).getName());
          }
          if (shopIdMap.containsKey(hashMap.get(SHOP))) {
            RsStore rsStore = shopIdMap.get(hashMap.get(SHOP));
            req.setShop(rsStore.getId());
            req.setShopCode(rsStore.getCode());
            req.setShopName(rsStore.getName());
          }
          req.setTitle(hashMap.get("taskName"));
        }
        reqList.add(req);
      }
    }
    log.info("reqList:{}", reqList);
    return reqList;
  }

  private RsStoreFilter buildStoreFilter(String orgId, List<String> shopTargetRecords) {
    RsStoreFilter rsStoreFilter = new RsStoreFilter();
    rsStoreFilter.setIdIn(shopTargetRecords);
    rsStoreFilter.setOrgIdEq(orgId);
    rsStoreFilter.setOrgTypeEq(Constants.ORG_TYPE);
    rsStoreFilter.setPage(0);
    rsStoreFilter.setPageSize(10000);
    return rsStoreFilter;
  }

  private AppMessageSaveNewReq buildCommonParams(TodoScene scene, CreateTodoInfo createTodoInfo,
      TodoSetting todoSetting) {
    AppMessageSaveNewReq req = new AppMessageSaveNewReq();
    req.setAppId(Constants.APP_ID);
    req.setType(todoSetting.getType());
    req.setSource(createTodoInfo.getSourceId());
    req.setTag(todoSetting.getTag());
    req.setTitle(todoSetting.getSettingName());
    req.setOperateInfo(new OperateInfo(new Operator("system", "system")));
    req.setSourceTypeId(todoSetting.getUuid());
    // 设置跳转url
    req.setJumpType(Constants.MEDIA_TYPE);
    MediaInfo mediaInfo = new MediaInfo();
    mediaInfo.setId(scene.getUrlExt());
    mediaInfo.setType(Constants.MEDIA_TYPE);
    mediaInfo.setContent(scene.getUrlExt());
    JsonNode mediaInfoParams = null;
    try {
      mediaInfoParams = stringToJsonNode(createTodoInfo.getSourceExt());
    } catch (IOException e) {
      log.error("sourceExt 转换失败");
    }
    mediaInfo.setParams(mediaInfoParams);
    req.setMediaInfo(mediaInfo);
    // 设置ext
    if (StringUtils.isNotBlank(createTodoInfo.getSourceExt()) && todoSetting.getCategory() != null) {
      BAppMessageExt bExt = new BAppMessageExt();
      bExt.setType(todoSetting.getCategory().name());
      Map<String, Object> contentMap = new HashMap<>();
      contentMap.put(Constants.TODO_SETTINGS, todoSetting);
      contentMap.put(Constants.FINISH_CONDITION, scene.getFinishCondition());
      contentMap.put(Constants.SOURCE_EXT, createTodoInfo.getSourceExt());
      bExt.setParams(contentMap);
      req.setExt(bExt);
    }
    return req;
  }

  private JsonNode stringToJsonNode(String text) throws IOException {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readTree(text);
  }

}
