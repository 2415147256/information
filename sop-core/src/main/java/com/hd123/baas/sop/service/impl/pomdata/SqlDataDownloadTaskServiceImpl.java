package com.hd123.baas.sop.service.impl.pomdata;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.service.api.basedata.pos.Pos;
import com.hd123.baas.sop.service.api.basedata.pos.PosFilter;
import com.hd123.baas.sop.service.api.basedata.pos.PosService;
import com.hd123.baas.sop.service.api.basedata.pos.PromDataDownloadTask;
import com.hd123.baas.sop.config.FmsConfig;
import com.hd123.baas.sop.service.api.pms.rule.PromRule;
import com.hd123.baas.sop.service.impl.message.FmsMgr;
import com.hd123.baas.sop.service.dao.pomdata.SqlDataDownloadTaskDao;
import com.hd123.baas.sop.service.dao.rule.PromRuleDao;
import com.hd123.baas.sop.service.api.pomdata.PosSqlDataDownloadTask;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadCsvFile;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadTask;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadTaskService;
import com.hd123.baas.sop.service.impl.pomdata.event.PromRuleGeneralBillEvent;
import com.hd123.baas.sop.remote.fms.bean.FmsMsg;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallExecutor;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.json.JsonUtil;
import com.hd123.spms.commons.util.CollectionUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SqlDataDownloadTaskServiceImpl implements SqlDataDownloadTaskService, EvCallExecutor {
  public static final String BEAN_ID = "sop-service.promData.sqlDataDownloadTask";

  public static final Map<String, String> querySqls = new HashMap<>();

  static {
    querySqls.put("PomSingleForm.csv", SqlDataDownloadQuerySql.PomSingleForm);
    querySqls.put("PomSingleIndex.csv", SqlDataDownloadQuerySql.PomSingleIndex);
    querySqls.put("PomItemIndex.csv", SqlDataDownloadQuerySql.PomItemIndex);
    querySqls.put("PomItemGroup.csv", SqlDataDownloadQuerySql.PomItemGroup);
    querySqls.put("PomItem.csv", SqlDataDownloadQuerySql.PomItem);
    querySqls.put("PomSingle.csv", SqlDataDownloadQuerySql.PomSingle);
  }

  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private PosService posService;
  @Autowired
  private FmsMgr fmsMgr;
  @Autowired
  private PromRuleDao promRuleDao;
  @Autowired
  private SqlDataDownloadTaskDao sqlDataDownloadTaskDao;
  @Autowired
  private EvCallManager evCallManager;
  @Autowired
  private BaasConfigClient baasConfigClient;

  @PostConstruct
  public void init() {
    evCallManager.addExecutor(this, BEAN_ID);
  }

  @EventListener
  public void processPromRuleGeneralBillEvent(PromRuleGeneralBillEvent event) throws Exception {
    PromRule rule = event.getRule();
    if (CollectionUtils.isNotEmpty(rule.getPromChannels())
        && rule.getPromChannels().size() == 1
        && rule.getPromChannels().get(0).equalsIgnoreCase(PromChannelEnum.online_pos.name())) {
      log.info("促销渠道有且只有仅自助收银机,忽略,tenant={},rule={}", rule.getTenant(), JsonUtil.objectToJson(rule));
      return;
    }
    PromSqlDownloadConfig config = configClient.getConfig(rule.getTenant(), PromSqlDownloadConfig.class);
    if (config.isEnabled() == false) {
      return;
    }
    // 作废的时候，立即下发
    if (rule.getState() == PromRule.State.stopped) {
      generalTasks(rule);
    } else if ("-".equals(rule.getStarterOrgUuid()) == false) {
      generalTasks(rule);
    } else {
      PromRule target = new PromRule();
      target.setTenant(rule.getTenant());
      target.setUuid(rule.getUuid());
      target.setBillNumber(rule.getBillNumber());
      evCallManager.submit(SqlDataDownloadTaskServiceImpl.BEAN_ID, JsonUtil.objectToJson(target));
    }
  }

  @Override
  public void execute(String json, @NotNull
      EvCallExecutionContext context) throws Exception {
    PromRule rule = JsonUtil.jsonToObject(json, PromRule.class);
    rule = promRuleDao.get(rule.getTenant(), rule.getUuid(), PromRule.PART_JOIN_UNITS);
    if (CollectionUtils.isNotEmpty(rule.getPromChannels())
        && rule.getPromChannels().size() == 1
        && rule.getPromChannels().get(0).equalsIgnoreCase(PromChannelEnum.online_pos.name())) {
      log.info("促销渠道有且只有仅自助收银机,忽略,tenant={},rule={}", rule.getTenant(), JsonUtil.objectToJson(rule));
      return;
    }
    generalTasks(rule);
  }

  public void generalTasks(PromRule rule) throws Exception {
    log.info("促销单{}开始创建下发任务", rule.getBillNumber());
    PosFilter filter = new PosFilter();
    int state = 0;
    if (rule.getJoinUnits().getAllUnit()) {
      // do nothings
    } else if (rule.getJoinUnits().getStores() == null || rule.getJoinUnits().getStores().isEmpty()) {
      log.info("促销单{}开始创建下发任务，无参与门店，跳过", rule.getBillNumber());
      return;
    } else {
      filter.setStoreIdIn(rule.getJoinUnits().getStores().stream().map(UCN::getUuid).collect(Collectors.toList()));
    }
    filter.setOrgIdEq(rule.getOrgId());
    List<Pos> posList = posService.query(rule.getTenant(), filter).getRecords();
    log.info("促销单{}开始创建下发任务。本次下发pos数：{}", rule.getBillNumber(), posList.size());
    if (posList.isEmpty()) {
      return;
    }
    boolean effectiveToday = rule.effectiveToday();
    log.info("促销单{}开始创建下发任务。本次下发pos数：{} 是否立即发送通知给POS {}", rule.getBillNumber(), posList.size(), effectiveToday);
    for (List<Pos> list : CollectionUtil.sizeBy(posList, 100)) {
      generalTasks(rule.getTenant(), rule.getUuid(), rule.getLastModifyInfo().getTime(), list, state, effectiveToday);
    }
  }

  @Override
  public void generalTasks(String tenant, String sourceId, Date createTime, List<Pos> posList, int state, boolean notifyPosNow) {
    List<SqlDataDownloadTask> newTaskList = new ArrayList<>();
    List<SqlDataDownloadTask> updateTaskList = new ArrayList<>();
    List<PosSqlDataDownloadTask> posTaskList = new ArrayList<>();
    List<SqlDataDownloadCsvFile> csvFileList = new ArrayList<>();

    List<String> posNoList = posList.stream().map(Pos::getId).collect(Collectors.toList());
    List<SqlDataDownloadTask> currentTasks = sqlDataDownloadTaskDao.queryTasks(tenant, posNoList);
    Map<String, SqlDataDownloadTask> posTaskMap = currentTasks.stream()
        .collect(Collectors.toMap(SqlDataDownloadTask::getPosNo, o -> o));

    for (Pos pos : posList) {
      SqlDataDownloadTask task = posTaskMap.get(pos.getId());
      if (task != null) {
        task.setState(4);
        updateTaskList.add(task);
      }

      task = new SqlDataDownloadTask();
      task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
      task.setTenant(tenant);
      task.setPosNo(pos.getId());
      task.setSourceId(sourceId);
      task.setCreateTime(createTime);
      task.setState(state);
      task.setCls("促销");
      newTaskList.add(task);

      PosSqlDataDownloadTask posTask = new PosSqlDataDownloadTask();
      posTask.setTenant(task.getTenant());
      posTask.setPosNo(task.getPosNo());
      posTask.setTaskId(task.getTaskId());
      posTaskList.add(posTask);

      for (String fileName : querySqls.keySet()) {
        SqlDataDownloadCsvFile csvFile = new SqlDataDownloadCsvFile();
        csvFile.setTaskId(task.getTaskId());
        csvFile.setFileName(fileName);
        String querySql = querySqls.get(fileName);
        querySql = querySql.replaceAll("\\{tenant}", tenant);
        querySql = querySql.replaceAll("\\{orgGid}", pos.getOrgId());
        if (pos.getStore() != null) {
          querySql = querySql.replaceAll("\\{joinOrgUuid}", pos.getStore().getUuid());
        }
        csvFile.setQuerySql(querySql);
        csvFileList.add(csvFile);
      }
    }

    CollectionUtil.sizeBy(newTaskList, 100).forEach(list -> {
      if (list != null && list.isEmpty() == false) {
        sqlDataDownloadTaskDao.createTasks(list);
      }
    });
    CollectionUtil.sizeBy(updateTaskList, 100).forEach(list -> {
      if (list != null && list.isEmpty() == false) {
        sqlDataDownloadTaskDao.updateTasks(list);
      }
    });
    CollectionUtil.sizeBy(csvFileList, 100).forEach(list -> {
      if (list != null && list.isEmpty() == false) {
        sqlDataDownloadTaskDao.createCsvFiles(list);
      }
    });
    CollectionUtil.sizeBy(posTaskList, 100).forEach(list -> {
      if (list != null && list.isEmpty() == false) {
        sqlDataDownloadTaskDao.createPosTasks(list);
      }
    });

    sqlDataDownloadTaskDao.cleanValid(posNoList);

    if (notifyPosNow) {
      // 发送通知给pos
      notifyTask2Pos(tenant, posList);
    }
  }

  private void notifyTask2Pos(String tenant, List<Pos> posList) {
    if (CollectionUtils.isEmpty(posList)) {
      return;
    }
    FmsConfig config = baasConfigClient.getConfig(tenant, FmsConfig.class);
    FmsMsg fmsMsg = new FmsMsg();
    fmsMsg.setTemplateId(config.getSqlDataDownloadTaskPushTemplateId());
    List<String> targets = posList.stream().map(e -> "pos-" + e.getId()).collect(Collectors.toList());
    fmsMsg.setTarget(targets);
    Map<String, String> userIdent = new HashMap<>();
    for (String target : targets) {
      userIdent.put(target, target);
    }
    fmsMsg.userIdents(FmsMsg.Sender.webSocketSender, userIdent);
    fmsMsg.getTemplateParams().put("title", "系统下发了新的促销规则更新任务");
    fmsMsg.getTemplateParams().put("content", "请及时更新处理");
    fmsMgr.sendAndSave(tenant, FmsMsg.Topic.SOP_PMS_SYNC, fmsMsg);
  }

  @Override
  public void fetchDownloadState(String tenant, List<Pos> list) {
    if (list == null || list.isEmpty())
      return;

    Map<String, Pos> posMap = list.stream().collect(Collectors.toMap(Pos::getId, O -> O));
    List<SqlDataDownloadTask> currentTasks = sqlDataDownloadTaskDao.queryTasks(tenant,
        new ArrayList<>(posMap.keySet()));
    currentTasks.forEach(task -> {
      PromDataDownloadTask target = new PromDataDownloadTask();
      BeanUtils.copyProperties(task, target);
      posMap.get(task.getPosNo()).setPromDataDownloadTask(target);
    });
  }
}
