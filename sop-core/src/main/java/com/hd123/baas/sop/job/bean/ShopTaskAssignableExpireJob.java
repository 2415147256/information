package com.hd123.baas.sop.job.bean;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import com.hd123.baas.sop.service.dao.task.ShopTaskTransferDaoBof;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.ShopTaskConfig;
import com.hd123.baas.sop.service.api.task.ShopTaskService;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * 执行普通任务过期
 * 
 * @Author guyahui
 * @Since
 */
@Slf4j
@Component
public class ShopTaskAssignableExpireJob implements Job {

  @Value("${sop-service.appId}")
  private String appId;

  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private ShopTaskService shopTaskService;
  @Autowired
  private BaasConfigClient configClient;
  @Autowired
  private ShopTaskTransferDaoBof shopTaskTransferDao;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("执行普通任务过期job");
    Set<String> tenants;
    try {
      tenants = getTenants();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (String tenant : tenants) {
      expireShopTask(tenant);
    }
  }

  public void expireShopTask(String tenant) {
    shopTaskService.expireShopTask(tenant);
    shopTaskTransferDao.autoCancelByExpireShopTask(tenant);
  }

  /**
   * 获取所有租户
   *
   * @return 租户列表
   */
  public Set<String> getTenants() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, ShopTaskConfig.SHOP_TASK_ENABLED);
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, appId);
    QueryResult<ConfigItem> result = configClient.query(qd);
    if (result != null) {
      return result.getRecords().stream().map(i -> i.getTenant()).collect(Collectors.toSet());
    }
    return null;
  }

  protected OperateInfo getSysOperateInfo() {
    OperateInfo operateInfo = new OperateInfo();
    Operator operator = new Operator();
    operateInfo.setTime(new Date());
    operator.setId("system");
    operator.setFullName("系统用户");
    operator.setNamespace("system");
    operateInfo.setOperator(operator);
    return operateInfo;
  }
}
