package com.hd123.baas.sop.service.api.price.gradeadjustment;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

import java.util.Collection;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/12.
 */
public interface PriceGradeAdjustmentService {

  /**
   * 创建一个门店价格级调整单
   *
   * @param tenant 租户
   * @return 调整单
   */
  PriceGradeAdjustment create(String tenant, String orgId, OperateInfo operateInfo);

  /**
   * 添加行
   * <p>
   * 仅草稿和待审核状态的单据可添加
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param skuGroup    分类id
   * @param skuPosition 定位id
   * @param operateInfo 操作人信息
   */
  void addLine(String tenant, String uuid, String skuGroup, String skuPosition, String priceGrade,
               OperateInfo operateInfo) throws BaasException;

  /**
   * @param tenant      租户
   * @param uuid        单据Id
   * @param lines       行列表
   * @param operateInfo 操作人信息
   * @throws BaasException
   */
  void batchAddLine(String tenant, String uuid, List<PriceGradeAdjustmentLine> lines, OperateInfo operateInfo) throws BaasException;

  /**
   * 添加行。有则更新，无责插入。不校验对应分组、定位、价格级是否存在
   * <p>
   * 仅草稿和待审核状态的单据可添加
   *
   * @param tenant      租户
   * @param line        待报错的信息
   * @param operateInfo 操作人信息
   */
  void saveLine(String tenant, String uuid, PriceGradeAdjustmentLine line, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 批量删除行信息
   * <p>
   * 仅草稿和待审核状态的单据可删除
   *
   * @param tenant  租户
   * @param uuid    单据id
   * @param lineIds 行id集合
   */
  void deleteLines(String tenant, String uuid, Collection<String> lineIds, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 设置价格级
   * <p>
   * 仅草稿和待审核状态的单据可设置
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param lineIds     行id集合
   * @param priceGrade  价格级
   * @param operateInfo 操作人信息
   */
  void editLineGrade(String tenant, String uuid, Collection<String> lineIds, String priceGrade, OperateInfo operateInfo)
      throws BaasException;

  /**
   * 保存修改。若状态为草稿，则变更为待审核
   *
   * @param tenant      租户
   * @param adjustment  价格调整单
   * @param operateInfo 操作人信息
   */
  void saveAndSubmit(String tenant, PriceGradeAdjustment adjustment, OperateInfo operateInfo) throws BaasException;

  /**
   * 审核
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param operateInfo 操作人信息
   */
  void audit(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 作废。待审核和已审核状态均可作废
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param reason
   * @param operateInfo
   */
  void cancel(String tenant, String uuid, String reason, OperateInfo operateInfo) throws BaasException;

  /**
   * 发布
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param operateInfo 操作人信息
   */
  void publish(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 过期
   *
   * @param tenant      租户
   * @param uuid        单据id
   * @param operateInfo 操作人信息
   */
  void expire(String tenant, String uuid, OperateInfo operateInfo) throws BaasException;

  /**
   * 查询详情
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @return 门店价格级调整单
   */
  PriceGradeAdjustment get(String tenant, String uuid, String... fetchParts);

  /**
   * 分页查询
   *
   * @param tenant 租户
   * @param qd     查询条件
   * @return 查询结果
   */
  QueryResult<PriceGradeAdjustment> query(String tenant, QueryDefinition qd, String... fetchParts);

  /**
   * 单据行分页查询
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @param qd     查询条件
   * @return 查询结果
   */
  QueryResult<PriceGradeAdjustmentLine> queryLine(String tenant, String uuid, QueryDefinition qd);

  /**
   * 关联门店
   *
   * @param tenant  租户
   * @param uuid    单据id
   * @param shopIds 门店id集合
   * @throws BaasException 业务异常
   */
  void relateShops(String tenant, String uuid, List<String> shopIds) throws BaasException;

  /**
   * 查询关联门店
   *
   * @param tenant 租户
   * @param uuid   单据id
   * @return 关联门店列表
   */
  List<PriceGradeAdjustmentShop> listShops(String tenant, String uuid);

}
