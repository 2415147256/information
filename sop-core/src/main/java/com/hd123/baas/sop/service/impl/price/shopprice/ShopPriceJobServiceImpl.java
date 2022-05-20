package com.hd123.baas.sop.service.impl.price.shopprice;

import com.hd123.baas.sop.annotation.NoTx;
import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.price.shopprice.ShopPriceJobService;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJob;
import com.hd123.baas.sop.service.api.price.shopprice.bean.ShopPriceJobState;
import com.hd123.baas.sop.service.dao.price.shopprice.ShopPriceJobDaoBof;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author zhengzewang on 2020/11/23.
 *
 */
@Service
@Slf4j
public class ShopPriceJobServiceImpl implements ShopPriceJobService {

  @Autowired
  private ShopPriceJobDaoBof dao;


  @Override
  public void saveNew(String tenant, ShopPriceJob priceJob, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    priceJob.setState(ShopPriceJobState.CONFIRMED);
    dao.insert(tenant, priceJob, operateInfo);
  }

  @Override
  public void batchSaveNew(String tenant, Collection<ShopPriceJob> priceJobs, OperateInfo operateInfo)
      throws BaasException {
    Assert.hasText(tenant, "tenant");
    priceJobs.stream().forEach(p -> p.setState(ShopPriceJobState.CONFIRMED));
    dao.batchInsert(tenant, priceJobs, operateInfo);
  }

  @Override
  @Tx
  public void finish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(uuid, "uuid");
    ShopPriceJob priceJob = dao.get(tenant, uuid);
    if (priceJob == null) {
      throw new BaasException("任务不存在");
    }
    dao.changeState(tenant, uuid, ShopPriceJobState.FINISHED, "", operateInfo);
  }

  @Override
  public ShopPriceJob getByShopAndTask(String tenant, String shop, String taskId) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(shop, "shop");
    Assert.hasText(taskId, "taskId");
    return dao.getByShopAndTask(tenant, shop, taskId);
  }

  @Override
  public long count(String tenant, String taskId, ShopPriceJobState state) {
    Assert.hasText(tenant, "tenant");
    Assert.hasText(taskId, "taskId");

    QueryDefinition qd = new QueryDefinition();
    qd.addByField(ShopPriceJob.Queries.TASK_ID, Cop.EQUALS, taskId);
    if (state != null) {
      qd.addByField(ShopPriceJob.Queries.STATE, Cop.EQUALS, state.name());
    }
    return dao.queryCount(tenant, qd);
  }

  @Override
  public QueryResult<ShopPriceJob> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(qd, "自定义查询条件");
    return dao.query(tenant, qd);
  }

  @Override
  @NoTx
  public void logError(String tenant, String uuid, String title, Exception ex, OperateInfo operateInfo)
      throws BaasException {
    Assert.notNull(tenant, "租户id");
    Assert.notNull(uuid, "uuid");

    title = title + "，trace_id=" + MDC.get("trace_id");
    if (ex != null) {
      title = title + "，异常信息：" + ex.getMessage();
    }
    dao.updateErrMsg(tenant, uuid, title, operateInfo);
  }
}
