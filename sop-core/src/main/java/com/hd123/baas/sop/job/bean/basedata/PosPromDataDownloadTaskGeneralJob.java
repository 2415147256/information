package com.hd123.baas.sop.job.bean.basedata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.config.api.entity.ConfigItem;

import lombok.extern.slf4j.Slf4j;

/**
 * pos资料下发
 *
 * @author cRazy
 */
@Slf4j
@DisallowConcurrentExecution
public class PosPromDataDownloadTaskGeneralJob implements Job {

  @Autowired
  private BaasConfigClient client;
  @Value("${baas-config.currentAppId:${spring.application.name}}")
  private String currentAppId;

  @Autowired
  private EvCallEventPublisher publisher;

  @Override
  @Tx
  public void execute(JobExecutionContext context) {
    log.info("pos促销数据下载job");
    List<ConfigItem> items;
    try {
      items = getItems();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    for (ConfigItem item : items) {
      PosPromDataDownloadTaskMsg msg = new PosPromDataDownloadTaskMsg();
      msg.setTenant(item.getTenant());
      msg.setOrgId(item.getSpec());
      msg.setTraceId(MDC.get("trace_id"));
      publisher.publishForNormal(PosPromDataDownloadTaskEvCallExecutor.POS_PROM_DATA_DOWNLOAD_TASK, msg);
    }
  }

  /**
   * 获取所有组织配置
   *
   * @return 租户列表
   */
  private List<ConfigItem> getItems() throws Exception {
    QueryDefinition qd = new QueryDefinition();
    /**
     * {@link com.hd123.baas.sop.config.H6TaskConfig}
     */
    qd.addByField(ConfigItem.Queries.KEY, Cop.EQUALS, "pos.taskGeneration");
    qd.addByField(ConfigItem.Queries.APP_ID, Cop.EQUALS, currentAppId);
    qd.addByField(ConfigItem.Queries.VALUE, Cop.EQUALS, "true");
    QueryResult<ConfigItem> result = client.query(qd);
    if (CollectionUtils.isEmpty(result.getRecords())) {
      return new ArrayList<>();
    }
    return result.getRecords();
  }
}
