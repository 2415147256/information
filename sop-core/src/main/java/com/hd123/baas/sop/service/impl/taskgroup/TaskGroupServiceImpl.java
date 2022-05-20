package com.hd123.baas.sop.service.impl.taskgroup;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.basedata.store.Store;
import com.hd123.baas.sop.service.api.basedata.store.StoreFilter;
import com.hd123.baas.sop.service.api.basedata.store.StoreService;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupData;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupService;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupShop;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplate;
import com.hd123.baas.sop.service.api.taskgroup.TaskTemplateMaxSeq;
import com.hd123.baas.sop.service.dao.taskgroup.TaskGroupDaoBof;
import com.hd123.baas.sop.service.dao.taskgroup.TaskGroupShopDaoBof;
import com.hd123.baas.sop.service.dao.taskgroup.TaskTemplateDaoBof;
import com.hd123.baas.sop.service.dao.taskplan.TaskPlanDaoBof;
import com.hd123.baas.sop.service.impl.price.BillNumberMgr;
import com.hd123.baas.sop.job.bean.TaskGroupMgr;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.taskgroup.TaskGroupCreateEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.taskgroup.TaskGroupCreateEvCallMsg;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskGroupServiceImpl implements TaskGroupService {

  @Autowired
  private TaskGroupDaoBof dao;

  @Autowired
  private TaskGroupShopDaoBof taskGroupShopDao;

  @Autowired
  private TaskPlanDaoBof taskPlanDao;

  @Autowired
  private EvCallEventPublisher publisher;

  @Autowired
  private TaskGroupMgr taskGroupMgr;

  @Autowired
  private StoreService storeService;

  @Autowired
  private TaskTemplateDaoBof taskTemplateDao;

  @Autowired
  private BillNumberMgr billNumberMgr;

  @Override
  @Tx
  public void saveNew(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "任务组");
    Assert.notNull(taskGroup.getName(), "任务组名");
    TaskGroup daoGroup = dao.getByName(tenant, taskGroup.getName());
    if (daoGroup != null) {
      throw new BaasException("任务名称重复!");
    }
    if (taskGroup.getUuid() == null) {
      taskGroup.setUuid(UUID.randomUUID().toString());
    }
    dao.insert(tenant, taskGroup, operateInfo);
    TaskGroupCreateEvCallMsg msg = new TaskGroupCreateEvCallMsg();
    msg.setTenant(tenant);
    msg.setGroupId(taskGroup.getUuid());
    publisher.publishForNormal(TaskGroupCreateEvCallExecutor.TASK_GROUP_CREATE_EXECUTOR_ID, msg);
  }

  @Override
  @Tx
  public int saveModify(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(taskGroup, "任务组");
    Assert.notNull(taskGroup.getName(), "任务组名");
    Assert.notNull(taskGroup.getUuid(), "ownerUuid");
    TaskGroup checkTaskGroup = this.get(tenant, taskGroup.getUuid());
    if (checkTaskGroup == null) {
      throw new BaasException("该任务组不存在");
    }
    TaskGroup daoGroup = dao.getByName(tenant, taskGroup.getName());
    if (daoGroup != null && !daoGroup.getUuid().equals(taskGroup.getUuid())) {
      throw new BaasException("任务名称重复!");
    }

    if (taskGroup.getRemindTime() != null) {
      if (!isValidDate(taskGroup.getRemindTime())) {
        throw new BaasException("提醒时间格式不正确");
      }
    }
    return dao.update(tenant, taskGroup, operateInfo);
  }

  public boolean isValidDate(String str) {
    String hhmmss = "HH:mm:ss";
    String hhmm = "HH:mm";
    boolean flag = true;
    SimpleDateFormat sdf = new SimpleDateFormat(hhmm);
    try {
      sdf.parse(str);
    } catch (ParseException e) {
      sdf = new SimpleDateFormat(hhmmss);
      try {
        sdf.parse(str);
      } catch (ParseException parseException) {
        flag = false;
      }
    }
    return flag;
  }

  @Override
  public int delete(String tenant, String uuid) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuids");
    TaskGroup taskGroup = dao.get(tenant, uuid);
    if (taskGroup == null) {
      throw new BaasException("任务组不存在");
    }
    taskGroupShopDao.deleteByGroupId(tenant, uuid);
    taskPlanDao.deleteByGroupId(tenant, uuid);
    return dao.delete(tenant, uuid);
  }

  @Override
  public TaskGroup get(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return dao.get(tenant, uuid);
  }

  @Override
  public QueryResult<TaskGroup> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");
    qd.addByField(TaskGroup.Queries.TENANT, Cop.EQUALS, tenant);
    return dao.query(qd);
  }

  /**
   * 任务组关联门店
   */
  @Override
  public Boolean relateShops(String tenant, String orgId, String uuid, List<String> shops) throws BaasException {
    Assert.notNull(tenant);
    Assert.notNull(uuid);
    Assert.notNull(shops);

    if (CollectionUtils.isEmpty(shops)) {
      log.info("收到的门店集合为空,仅进行解绑操作");
      taskGroupShopDao.deleteByGroupId(tenant, uuid);
      return true;
    }

    StoreFilter filter = new StoreFilter();
    filter.setIdIn(shops);
    QueryResult<Store> query = storeService.query(tenant, filter);
    if (CollectionUtils.isEmpty(query.getRecords()) || query.getRecords().size() != shops.size()) {
      throw new BaasException("存在未知门店,请检查");
    }
    TaskGroup tg = dao.get(tenant, uuid);
    if (tg == null) {
      throw new BaasException("该任务组不存在");
    }

    List<TaskGroup> tgF = dao.getByShops(tenant, shops);
    // 日结任务只能绑定一个
    if (tgF != null && tg.getType().equals(TaskGroupType.DAILY)) {
      List<TaskGroup> collect = tgF.stream()
          .filter(i -> i.getType().name().equals(tg.getType().name()))
          .filter(i -> !i.getUuid().equals(tg.getUuid()))
          .collect(Collectors.toList());
      if (CollectionUtils.isNotEmpty(collect)) {
        throw new BaasException("存在门店已绑定相同类型任务组");
      }
    }
    taskGroupShopDao.deleteByGroupId(tenant, uuid);

    taskGroupShopDao.batchInsert(tenant, uuid, shops);
    return true;
  }

  @Override
  public List<String> getRelateShops(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    List<TaskGroupShop> taskGroupShops = taskGroupShopDao.getRelateShops(tenant, uuid);
    List<String> list = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(taskGroupShops)) {
      return taskGroupShops.stream().map(TaskGroupShop::getShop).collect(Collectors.toList());
    }
    return list;
  }

  @Override
  public Set<String> getRelateShops(String tenant, TaskGroupType type) {
    Assert.notNull(tenant, "租户");
    List<TaskGroupShop> taskGroupShops = taskGroupShopDao.getRelateShops(tenant, type);
    Set<String> set = new HashSet<>();
    if (CollectionUtils.isNotEmpty(taskGroupShops)) {
      return taskGroupShops.stream().map(TaskGroupShop::getShop).collect(Collectors.toSet());
    }
    return set;
  }

  @Override
  public List<TaskGroupData> packingBTaskGroupData(String tenant, TaskGroup... queryResults) {

    Assert.notEmpty(tenant, "tenant");
    Assert.notEmpty(queryResults, "queryResult");

    List<TaskGroup> queryResult = Arrays.stream(queryResults).collect(Collectors.toList());
    List<TaskGroupData> taskGroupData = new ArrayList<>();
    // 基础数据的转换
    taskGroupData = ConverterUtil.convert(queryResult, TaskGroupToTaskGroupData.getInstance());
    List<String> taskGroupIds = queryResult.stream().map(TaskGroup::getUuid).collect(Collectors.toList());
    List<TaskTemplate> taskTemplates = taskTemplateDao.listByOwners(tenant, taskGroupIds);
    // 此处为空则表示该巡检主题下面没有巡检内容或者模板
    if (CollectionUtils.isEmpty(taskTemplates)) {
      return taskGroupData;
    }
    // 加和计算每个任务主题的模板分值和
    Map<String, BigDecimal> totalScore = taskTemplates.stream()
        .collect(Collectors.groupingBy(TaskTemplate::getOwner))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            m -> m.getValue()
                .stream()
                .map(TaskTemplate::getScore)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    // 加和计算每个任务主题下有多少模板
    Map<String, Integer> totalTemplate = taskTemplates.stream()
        .collect(Collectors.groupingBy(TaskTemplate::getOwner))
        .entrySet()
        .stream()
        .collect(Collectors.toMap(Map.Entry::getKey, m -> m.getValue().size()));
    // 将bTaskGroups的值进行补全操作
    taskGroupData.forEach(item -> {
      if (totalScore.containsKey(item.getUuid())) {
        item.setScore(totalScore.get(item.getUuid()));
      }
      if (totalTemplate.containsKey(item.getUuid())) {
        item.setCount(new BigDecimal(totalTemplate.get(item.getUuid())));
      }
    });
    return taskGroupData;
  }

  @Override
  @Tx
  public void updateState(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.notEmpty(tenant, "tenant");
    Assert.notEmpty(uuid, "uuid");
    //没有巡检内容的不能发布
    List<TaskTemplate> taskTemplates = taskTemplateDao.listByOwner(tenant, uuid);
    if (CollectionUtils.isEmpty(taskTemplates)) {
      throw new BaasException("该巡检主题下没有巡检内容不能发布，该巡检主题id为:" + uuid);
    }
    dao.updateState(tenant, uuid, operateInfo);
  }

  /**
   * 修改原因: 添加新增时的名称重复校验。
   * 时间：2021-07-09
   * 修改人：zhuyuntao
   *
   * @param tenant      租户
   * @param taskGroup   主题
   * @param operateInfo 操作人信息
   * @return 巡检主题ID
   */
  @Override
  @Tx
  public String saveNewTaskGroup(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(taskGroup, "newTaskGroup");
    beforeSaveNewTaskGroup(tenant, taskGroup);

    List<TaskTemplate> templateList = taskGroup.getTemplateList();
    String taskGroupId = IdGenUtils.buildRdUuid();
    String code = generateTaskGroupCode(tenant);
    taskGroup.setUuid(taskGroupId);
    taskGroup.setCode(code);
    taskGroup.setState(TaskGroupState.INIT.name());

    dao.insertNew(tenant, taskGroup, operateInfo);
    if (CollectionUtils.isNotEmpty(templateList)) {
      templateList.forEach(taskTemplate -> {
        taskTemplate.setOwner(taskGroupId);
        taskTemplate.setFlowNo(code);
      });
      this.saveTaskTemplateBatch(tenant, templateList, operateInfo);
    }
    return taskGroup.getUuid();
  }

  /**
   * 修改原因：添加修改时名称的重复校验，并且校验修改时是否有改动过的内容。
   * 时间：2021-07-09
   * 修改人：zhuyuntao
   *
   * @param tenant      租户
   * @param taskGroup   需要修改的任务组信息
   * @param operateInfo 操作者信息
   */
  @Override
  @Tx
  public void updateTaskGroupName(String tenant, TaskGroup taskGroup, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(taskGroup.getUuid(), "uuid");
    Assert.notNull(taskGroup.getName(), "name");

    beforeUpdateTaskGroup(tenant, taskGroup);

    dao.updateTaskGroupName(tenant, taskGroup, operateInfo);
  }

  @Override
  @Tx
  public void deleteByUuid(String tenant, String uuid) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(uuid, "uuid");
    dao.delete(tenant, uuid);
  }

  @Override
  public List<TaskTemplate> queryByOwner(String tenant, String owner) {
    Assert.notNull(tenant, "tenant");
    Assert.notNull(owner, "owner");

    return taskTemplateDao.listByOwner(tenant, owner);
  }

  @Override
  public TaskTemplate getTaskTemplate(String tenant, String uuid) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    return dao.getTaskTemplate(tenant, uuid);
  }


  @Override
  @Tx
  public String saveNewTaskTemplate(String tenant, TaskTemplate taskTemplate, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.notNull(taskTemplate, "taskTemplate");
    Assert.hasText(taskTemplate.getContent(), "content");
    Assert.notNull(taskTemplate.getScore(), "score");
    Assert.hasText(taskTemplate.getName(), "name");
    Assert.hasText(taskTemplate.getOwner(), "owner");
    taskTemplate.setUuid(IdGenUtils.buildRdUuid());
    final TaskTemplate templateName = taskTemplateDao.getByOwnerAndName(tenant, taskTemplate.getOwner(), taskTemplate.getName());
    if (templateName != null) {
      throw new BaasException("该巡检主题下的巡检内容已存在，请勿重复创建！");
    }
    // 获取该主题下最大的排序值
    List<TaskTemplateMaxSeq> taskTemplateMaxSeqByOwnerList = getTaskTemplateMaxSeqByOwnerList(tenant, Collections.singletonList(taskTemplate.getOwner()));
    if (CollectionUtils.isNotEmpty(taskTemplateMaxSeqByOwnerList)) {
      int seq = taskTemplateMaxSeqByOwnerList.get(0).getSeq();
      taskTemplate.setSeq(++seq);
    }
    taskTemplateDao.saveNew(tenant, taskTemplate, operateInfo);
    return taskTemplate.getUuid();
  }

  @Override
  @Tx
  public String saveModifyTaskTemplate(String tenant, TaskTemplate taskTemplate,
                                       OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(taskTemplate.getContent(), "content");
    Assert.notNull(taskTemplate.getScore(), "score");
    Assert.hasText(taskTemplate.getName(), "name");
    Assert.hasText(taskTemplate.getUuid(), "uuid");
    Assert.hasText(taskTemplate.getOwner(), "owner");

    beforeUpdateTaskTemplate(tenant, taskTemplate);

    taskTemplateDao.saveModifyTaskTemplate(tenant, taskTemplate, operateInfo);
    return taskTemplate.getUuid();
  }

  @Override
  @Tx
  public void deleteTaskTemplateByUuid(String tenant, String uuid) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    taskTemplateDao.delete(tenant, uuid);
  }

  @Override
  public List<TaskGroup> listByCodes(String tenant, List<String> codes) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(codes, "codes");
    return dao.listByCodes(tenant, codes);
  }


  /**
   * 生成主题码
   *
   * @return 主题代码
   */
  private String generateTaskGroupCode(String tenant) throws BaasException {
    String code = billNumberMgr.generateTaskGroupCode(tenant);
    if (StringUtils.isEmpty(code) || code.length() < 8) {
      throw new BaasException("生成主题code失败");
    }
    return code.substring(1);
  }

  @Override
  public List<TaskGroup> listByTypeAndName(String tenant, String type, List<String> importTaskGroupNames) {
    Assert.hasText(tenant, "租户");
    Assert.hasText(type, "type");
    if (importTaskGroupNames.isEmpty()) {
      log.info("导入的主题名称列表为空，所以不做查询");
      return new ArrayList<>();
    }
    return dao.listByTypeAndName(tenant, type, importTaskGroupNames);
  }

  @Override
  public List<TaskTemplate> listTemplateByOwners(String tenant, List<String> owners) {
    Assert.hasText(tenant, "租户");
    Assert.notNull(owners, "owners");
    if (owners.isEmpty()) {
      return new ArrayList<>();
    }
    return taskTemplateDao.listByOwners(tenant, owners);
  }

  @Tx
  @Override
  public void saveBatch(String tenant, List<TaskGroup> insertTaskGroupList, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    if (insertTaskGroupList.isEmpty()) {
      return;
    }
    for (TaskGroup taskGroup : insertTaskGroupList) {
      taskGroup.setUuid(IdGenUtils.buildRdUuid());
      taskGroup.setCode(generateTaskGroupCode(tenant));
      taskGroup.setState(TaskGroupState.INIT.name());
    }
    dao.saveBatch(tenant, insertTaskGroupList, operateInfo);
  }

  @Tx
  @Override
  public void saveTaskTemplateBatch(String tenant, Collection<TaskTemplate> insertTaskTemplateList, OperateInfo operateInfo) {
    insertTaskTemplateList.forEach(taskTemplate -> taskTemplate.setUuid(IdGenUtils.buildRdUuid()));
    taskTemplateDao.saveBatch(tenant, insertTaskTemplateList, operateInfo);
  }

  @Tx
  @Override
  public void updateTaskTemplateBatch(String tenant, Collection<TaskTemplate> updateTaskTemplateList, OperateInfo operateInfo) {
    taskTemplateDao.updateBatch(tenant, updateTaskTemplateList, operateInfo);
  }

  @Override
  @Tx
  public void adjustSeq(String tenant, List<TaskTemplate> templateList, OperateInfo operateInfo) {
    Assert.hasText(tenant, "tenant");
    Assert.notEmpty(templateList, "templateList");

    taskTemplateDao.adjustSeq(tenant, templateList, operateInfo);
  }

  @Override
  public List<TaskTemplateMaxSeq> getTaskTemplateMaxSeqByOwnerList(String tenant, List<String> taskGroupIdList) {
    Assert.hasText(tenant, "tenant");
    if (CollectionUtils.isEmpty(taskGroupIdList)) {
      return new ArrayList<>();
    }
    return taskTemplateDao.getTaskTemplateMaxSeqByOwnerList(tenant, taskGroupIdList);
  }

  private void beforeUpdateTaskGroup(String tenant, TaskGroup taskGroup) throws BaasException {
    //校验是否发布
    TaskGroup taskGroup1 = dao.get(tenant, taskGroup.getUuid());
    if (null == taskGroup1) {
      throw new BaasException("该巡检主题不存在不能进行修改!");
    }
    if (TaskGroupState.SUBMITTED.name().equals(taskGroup1.getState())) {
      throw new BaasException("已发布的巡检主题不能进行修改!");
    }
    final TaskGroup groupName = dao.getByName(tenant, taskGroup.getName());
    if (groupName != null && !groupName.getUuid().equals(taskGroup.getUuid())) {
      throw new BaasException(taskGroup.getName() + "巡检主题已存在，请重新修改！");
    }
  }

  private void beforeUpdateTaskTemplate(String tenant, TaskTemplate taskTemplate) throws BaasException {
    TaskGroup taskGroup = get(tenant, taskTemplate.getOwner());
    if (null == taskGroup) {
      throw new BaasException("该主题不存在，不允许更改巡检内容");
    }
    if (TaskGroupState.SUBMITTED.name().equals(taskGroup.getState())) {
      throw new BaasException("该主题已发布，不允许更改巡检内容");
    }
    final TaskTemplate templateName = taskTemplateDao.getByOwnerAndName(tenant, taskTemplate.getOwner(), taskTemplate.getName());
    if (templateName != null && !templateName.getUuid().equals(taskTemplate.getUuid())) {
      throw new BaasException("该巡检主题下的巡检内容已存在，请重新修改！");
    }
  }

  private void beforeSaveNewTaskGroup(String tenant, TaskGroup taskGroup) throws BaasException {
    List<TaskTemplate> templateList = taskGroup.getTemplateList();
    final TaskGroup groupName = dao.getByName(tenant, taskGroup.getName());
    if (groupName != null) {
      throw new BaasException(taskGroup.getName() + "巡检主题已存在，请勿重复创建！");
    }
    if (CollectionUtils.isNotEmpty(templateList)) {
      if (templateList.size() != templateList.stream().map(TaskTemplate::getName).distinct().count()) {
        throw new BaasException("巡检内容名称不能重复！");
      }
      templateList.forEach(this::validTaskTemplateFields);
    }
  }

  void validTaskTemplateFields(TaskTemplate taskTemplate) {
    Assert.notNull(taskTemplate, "taskTemplate");
    Assert.hasText(taskTemplate.getName(), "name");
    Assert.notNull(taskTemplate.getScore(), "score");
    Assert.hasText(taskTemplate.getContent(), "content");
  }

}
