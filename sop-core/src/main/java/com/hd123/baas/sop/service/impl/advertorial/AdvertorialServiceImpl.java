package com.hd123.baas.sop.service.impl.advertorial;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.advertorial.Advertorial;
import com.hd123.baas.sop.service.api.advertorial.AdvertorialService;
import com.hd123.baas.sop.service.dao.advertorial.AdvertorialDaoBof;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdvertorialServiceImpl implements AdvertorialService {

  @Autowired
  private AdvertorialDaoBof advertorialDao;

  @Override
  public Advertorial saveNew(String tenant, Advertorial advertorial, OperateInfo operateInfo) {
    advertorial.setTenant(tenant);
    advertorial.setUuid(IdGenUtils.buildIidAsString());
    advertorialDao.insert(advertorial, operateInfo);
    return advertorial;
  }

  @Tx
  @Override
  public Advertorial saveModify(Advertorial advertorial, OperateInfo operateInfo) throws BaasException {
    Advertorial detail = advertorialDao.get(advertorial.getTenant(), advertorial.getUuid());
    if (detail != null) {
      if (!StringUtil.isNullOrBlank(advertorial.getTitle())) {
        detail.setTitle(advertorial.getTitle());
      }
      if (!StringUtil.isNullOrBlank(advertorial.getContent())) {
        detail.setContent(advertorial.getContent());
      }
      if (!StringUtil.isNullOrBlank(advertorial.getThUri())) {
        detail.setThUri(advertorial.getThUri());
      }
      advertorialDao.update(detail, operateInfo);
    } else {
      throw new BaasException("该记录不存在无法保存");
    }
    return detail;
  }

  @Tx
  @Override
  public void remove(String tenant, String uuid) {
    Advertorial detail = advertorialDao.get(tenant, uuid);
    if (detail != null) {
      advertorialDao.delete(tenant, uuid);
    } else {
      log.warn("该记录不存在无法删除");
    }

  }

  @Override
  public QueryResult<Advertorial> query(String tenant, QueryDefinition qd) {
    return advertorialDao.query(tenant, qd);
  }

  @Override
  public Advertorial get(String tenant, String uuid) {
    return advertorialDao.get(tenant, uuid);
  }

}
