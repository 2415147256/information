package com.hd123.baas.sop.job.bean.explosivev2;

import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2;
import com.hd123.baas.sop.service.api.explosivev2.ExplosiveV2Service;
import com.hd123.rumba.commons.biz.query.Cop;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author shenmin
 */
@Component
@Slf4j
public class ExplosiveOnJob extends AbstractExplosiveJob {
  @Autowired
  private ExplosiveV2Service explosiveV2Service;

  @Override
  protected QueryResult<ExplosiveV2> query(String tenant, int page) {
    QueryDefinition qd = new QueryDefinition();
    qd.setPage(page);
    qd.setPageSize(DEFAULT_PAGE_SIZE);
    qd.addByField(ExplosiveV2.Queries.SIGN_START_DATE, Cop.BEFORE_OR_EQUAL, new Date());
    qd.addByField(ExplosiveV2.Queries.STATE, Cop.EQUALS, ExplosiveV2.State.AUDITED.name());
    return explosiveV2Service.query(tenant, qd);
  }
}
