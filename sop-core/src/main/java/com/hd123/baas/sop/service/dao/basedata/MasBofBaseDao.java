package com.hd123.baas.sop.service.dao.basedata;

import com.hd123.baas.sop.service.dao.BofBaseDao;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author lina
 */
@Repository
public class MasBofBaseDao extends BofBaseDao {

  /** 分配UUID */
  protected String generateUUID(){
    return UUID.randomUUID().toString().replace("-", "");
  }
}
