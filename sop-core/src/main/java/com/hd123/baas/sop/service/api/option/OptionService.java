package com.hd123.baas.sop.service.api.option;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;

import java.util.List;

public interface OptionService {

  /**
   * 批量保存
   *
   * @param tenant
   * @param options
   * @param operateInfo
   * @throws Exception
   */
  void batchSaveNew(String tenant, List<Option> options, OperateInfo operateInfo) throws Exception;

  /**
   * 批量编辑
   *
   * @param tenant
   * @param options
   * @param operateInfo
   * @throws Exception
   */
  void batchSaveModify(String tenant, List<Option> options, OperateInfo operateInfo) throws Exception;


  /**
   * 根据 tenant+type+key 判断 有则修改 吴泽新增
   *
   * @param tenant
   * @param options
   * @param operateInfo
   */
  void save(String tenant, List<Option> options, OperateInfo operateInfo);
  /**
   * 根据租户+类型+key值获取数据
   *
   * @param tenant
   *         租户
   * @param type
   *        类型
   * @param keys
   *        集合
   * @return
   */
  List<Option> list(String tenant, OptionType type, List<String> keys);

  /**
   * 查询
   *
   * @param tenant
   *        租户
   * @param qd
   *        查询条件
   * @return
   */
  QueryResult<Option> query(String tenant, QueryDefinition qd);

}
