package com.hd123.baas.sop.service.api.group;

import java.util.List;

import com.hd123.baas.sop.service.api.entity.*;
import com.hd123.baas.sop.service.impl.group.PriceGradeByRangeCal;
import com.hd123.baas.sop.service.impl.group.PriceGradeBySkuCal;
import com.hd123.rumba.commons.biz.entity.UCN;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

public interface SkuGroupService {

  void saveNew(String tenant, SkuGroup skuGroup) throws BaasException;

  void saveModify(String tenant, SkuGroup skuGroup) throws BaasException;

  void batchDelete(String tenant, List<Integer> uuids) throws BaasException;

  List<SkuGroup> list(String tenant,String orgId);

  List<SkuGroup> list(String tenant);

  QueryResult<SkuGroup> query(String tenant, QueryDefinition qd);

  void assignCategories(String tenant, String orgId, Integer uuid, List<UCN> categoryIds) throws BaasException;

  void removeCategories(String tenant, Integer uuid, List<String> categoryIds) throws BaasException;

  List<PriceSkuCategory> categoryList(String tenant, String skuGroupId);

  List<PriceRange> gradeByAllRangeList(String tenant, String orgId, String skuGroupId);

  List<PriceRange> gradeByAllRangeList(String tenant, String skuGroupId);

  void saveGradeListByAllRange(String tenant, String skuGroupId, List<PriceRange> priceRanges) throws BaasException;

  void batchGradeListByAllRange(String tenant, List<SkuGroupRangeGradeConfig> configs) throws BaasException;

  List<SkuPosition> gradeByAllPositionList(String tenant, String skuGroupId);

  /**
   * 查询自定类别下所有的商品定位加价率
   * 
   * @param tenant
   * @param groupIds
   *          若为null，默认查询全部
   * @return
   */
  List<SkuGroupPositionGradeConfig> groupAllPositionList(String tenant, List<Integer> groupIds);

  /**
   * 查询自定类别下所有的价格带加价率
   * 
   * @param tenant
   * @param groupIds
   *          若为null，默认查询全部
   * @return
   */
  List<SkuGroupRangeGradeConfig> groupAllRangeList(String tenant, List<Integer> groupIds);

  void saveGradeListByAllPosition(String tenant, String skuGroupId, List<SkuPosition> skuPositions);

  void batchSaveGradeListByAllPosition(String tenant, List<SkuGroupPositionGradeConfig> configs) throws BaasException;

  /**
   * 提供根据商品id查询自定义类别的接口。两个接口，一个根据商品id查询，一个根据商品id集合查询
   */
  List<SkuGroup> listBySkuIds(String tenant, List<String> skuIds) throws BaasException;

  /**
   * 获取当前租户已保存的关系
   */
  List<SkuGroupCategoryAssoc> queryAllAssoc(String tenant,String orgId);

  SkuGroup get(String tenant, String uuid);

  /**
   * 查询自定义类别与商品类别的对应关系 包含商品类别的子类
   * 
   * @param tenant
   * @param categoryId
   * @return
   * @throws BaasException
   */
  List<SkuGroupCategory> listByCategoryId(String tenant, String categoryId) throws BaasException;

  /**
   * 查询自定义类别与商品类别的对应关系 包含商品类别的子类
   * 
   * @param tenant
   * @param groupId
   * @return
   * @throws BaasException
   */
  List<SkuGroupCategory> listByGroupId(String tenant, String groupId) throws BaasException;

  /**
   * 批量计算商品加价格加价率
   * 
   * @param tenant
   * @param gradeBySkuCals
   *          商品价格级加价率模型
   */
  void saveGradeListBySkuCal(String tenant, String orgId, List<PriceGradeBySkuCal> gradeBySkuCals) throws BaasException;

  /**
   * 批量计算价格带加价格加价率
   * 
   * @param tenant
   * @param groupId
   *          自定义类别id
   * @param rangeCals
   */
  void saveGradeListByRangeCal(String tenant,String orgId, int groupId, List<PriceGradeByRangeCal> rangeCals) throws BaasException;

  /**
   * 根据两个价格级的加价率计算其余价格级对应加价率
   * 
   * @param tenant
   *          租户id
   * @param firstPriceGrade
   *          价格级1
   * @param secondPriceGrade
   *          价格级2，其在价格级序列中所处位置必须在firstPriceGrade之后
   * @return 所有价格级
   * @throws BaasException
   */
  List<PUnv> computeGradeList(String tenant,String orgId, PUnv firstPriceGrade, PUnv secondPriceGrade) throws BaasException;

  /**
   * 根据两个价格级的加价率计算其余价格级对应加价率
   *
   * @param tenant
   *          租户id
   * @param firstPriceGrade
   *          价格级1
   * @param secondPriceGrade
   *          价格级2，其在价格级序列中所处位置必须在firstPriceGrade之后
   * @return 所有价格级
   * @throws BaasException
   */
  List<PUnv> computeGradeList(String tenant, PUnv firstPriceGrade, PUnv secondPriceGrade) throws BaasException;

  /**
   * 商品价格级配置查询
   *
   * @param tenant
   *          租户id
   * @param qd
   *          查询条件
   * @return 查询结果
   */
  QueryResult<SkuGradeConfig> querySkuGradeConfig(String tenant, QueryDefinition qd);

  /***
   * 删除商品价格级配置
   * 
   * @param tenant
   *          租户id
   * @param uuid
   *          主键
   */
  void removeSkuGradeConfig(String tenant, Integer uuid);

  /**
   * 获取商品价格级配置
   */
  SkuGradeConfig getSkuGradeBySkuId(String tenant, String orgId, String skuId);

  /**
   * 获取所有商品价格级配置
   */
  List<SkuGradeConfig> skuGradeConfigList(String tenant, String orgId);
}
