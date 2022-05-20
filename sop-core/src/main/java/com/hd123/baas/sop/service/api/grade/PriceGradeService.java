package com.hd123.baas.sop.service.api.grade;

import java.util.List;

import com.hd123.baas.sop.service.api.entity.PriceGrade;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface PriceGradeService {

  void saveNew(String tenant, PriceGrade priceGrade) throws BaasException;

  void saveModify(String tenant, PriceGrade priceGrade) throws BaasException;

  void batchSaveModify(String tenant, List<PriceGrade> priceGrades) throws BaasException;

  void batchDelete(String tenant, List<Integer> uuids);

  void batchSaveNew(String tenant, List<PriceGrade> priceGrades) throws BaasException;

  List<PriceGrade> list(String tenant,String orgId);

  List<PriceGrade> list(String tenant);

  QueryResult<PriceGrade> query(String tenant, QueryDefinition qd);

  /**
   * 设置默认价格级
   */
  void setDefault(String tenant,String orgId, Integer uuid) throws BaasException;

  /**
   * 获取默认价格级
   */
  PriceGrade getDftPriceGrade(String tenant,String orgId) throws BaasException;

  /**
   * 获取默认价格级
   */
  PriceGrade getDftPriceGrade(String tenant) throws BaasException;

  /**
   * 获取价格级
   */
  PriceGrade get(String tenant, Integer uuid) throws BaasException;
}
