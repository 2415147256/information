package com.hd123.baas.sop.service.impl.offset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hd123.baas.sop.service.api.offset.Offset;
import com.hd123.baas.sop.service.api.offset.OffsetService;
import com.hd123.baas.sop.service.api.offset.OffsetType;
import com.hd123.baas.sop.service.dao.offset.OffsetDaoBof;

/**
 * @author zhengzewang on 2020/11/17.
 */
@Service
public class OffsetServiceImpl implements OffsetService {

  @Autowired
  private OffsetDaoBof offsetDao;

  @Override
  public Offset get(String tenant, OffsetType type, String spec) {
    return offsetDao.get(tenant, type, spec);
  }

  @Override
  public Offset getWithLock(String tenant, OffsetType type, String spec) {
    return offsetDao.getWithLock(tenant, type, spec);
  }

  @Override
  public Offset get(String tenant, OffsetType type) {
    return this.get(tenant, type, Offset.DEF);
  }

  @Override
  public Offset getWithLock(String tenant, OffsetType type) {
    return this.getWithLock(tenant, type, Offset.DEF);
  }

  @Override
  public void save(String tenant, OffsetType type, String spec, Long seq) {
    Offset offset = new Offset();
    offset.setTenant(tenant);
    offset.setType(type);
    offset.setSpec(spec);
    offset.setSeq(seq);
    if (this.get(tenant, type, spec) == null) {
      offsetDao.insert(tenant, offset);
    } else {
      offsetDao.update(tenant, offset);
    }
  }

  @Override
  public void save(String tenant, OffsetType type, Long seq) {
    this.save(tenant, type, Offset.DEF, seq);
  }

}
