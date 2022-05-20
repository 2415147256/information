package com.hd123.baas.sop.jmzs.franchise.dao;

import com.hd123.baas.sop.jmzs.franchise.api.Franchise;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.rumba.commons.lang.Assert;

import java.util.HashMap;
import java.util.Map;

public class PFranchise extends PStandardEntity {

  public static final String TABLE_NAME = "franchise";
  public static final String TABLE_ALIAS = "_franchise";

  public static final String TENANT = "tenant";
  public static final String ORG_ID="orgId";
  public static final String CODE="code";
  public static final String ID="Id";
  public static final String NAME = "name";
  public static final String POSITION = "position";
  public static final String MOBILE = "mobile";
  public static final String STATUS = "status";
  public static final String DELETED = "deleted";
  public static final String CREATE_DATE = "createDate";
  public static final String CONTRACT_IMAGES = "contractImages";
  public static final String EXT = "ext";


  public static Map<String, Object> toFieldValues(Franchise entity) {
    Assert.assertArgumentNotNull(entity, "entity");
    Map<String, Object> fvm = new HashMap<String, Object>(PStandardEntity.toFieldValues(entity));
    putFieldValue(fvm, CODE, entity.getCode());
    putFieldValue(fvm, ID, entity.getId());
    putFieldValue(fvm, TENANT, entity.getTenant());
    putFieldValue(fvm, ORG_ID, entity.getOrgId());
    putFieldValue(fvm, NAME, entity.getName());
    putFieldValue(fvm, POSITION, entity.getPosition());
    putFieldValue(fvm, MOBILE, entity.getMobile());
    putFieldValue(fvm, STATUS, entity.getStatus());
    putFieldValue(fvm, CONTRACT_IMAGES, JsonUtil.objectToJson(entity.getContractImages()));
    putFieldValue(fvm, CREATE_DATE, entity.getCreateDate());
    putFieldValue(fvm, DELETED, entity.getDeleted());
    putFieldValue(fvm, EXT, entity.getExt());
    return fvm;
  }
}
