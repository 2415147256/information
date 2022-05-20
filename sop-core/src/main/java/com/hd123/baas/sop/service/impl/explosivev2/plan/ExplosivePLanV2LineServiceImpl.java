package com.hd123.baas.sop.service.impl.explosivev2.plan;

import com.hd123.baas.sop.service.api.explosivev2.plan.ExplosivePlanV2LineService;
import com.hd123.baas.sop.service.dao.explosivev2.plan.ExplosivePlanV2LineDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shenmin
 */
@Service
@Slf4j
public class ExplosivePLanV2LineServiceImpl implements ExplosivePlanV2LineService {
  @Autowired
  private ExplosivePlanV2LineDao explosivePlanV2LineDao;
}
