package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskTransfer;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @author guyahui
 * @date 2021/5/20 14:13
 */
@SchemaMeta
@MapToEntity(ShopTaskTransfer.class)
@Getter
@Setter
public class PShopTaskTransfer extends PStandardEntity {
  @TableName
  public static final String TABLE_NAME = "shop_task_transfer";
  public static final String TABLE_ALIAS = "_shop_task_transfer";

  public static final String TENANT = "tenant";
  // 任务详情ID
  public static final String SHOP_TASK_LOG_ID = "shop_task_log_id";
  // 任务ID
  public static final String SHOP_TASK_ID = "shop_task_id";
  // 店铺ID
  public static final String SHOP = "shop";
  // 店铺代码
  public static final String SHOP_CODE = "shop_code";
  // 店铺名称
  public static final String SHOP_NAME = "shop_name";
  // 转出人ID
  public static final String TRANSFER_FROM = "transfer_from";
  // 转出人姓名
  public static final String TRANSFER_FROM_NAME = "transfer_from_name";
  // 转出时间
  public static final String TRANSFER_TIME = "transfer_time";
  // 接受人ID
  public static final String TRANSFER_TO = "transfer_to";
  // 接受人姓名
  public static final String TRANSFER_TO_NAME = "transfer_to_name";
  // 接受人岗位code
  public static final String TRANSFER_TO_POSITION_CODE = "transfer_to_position_code";
  // 接受人岗位名称
  public static final String TRANSFER_TO_POSITION_NAME = "transfer_to_position_name";
  // 接受人操作时间
  public static final String OPER_TIME = "oper_time";
  // 接受人备注
  public static final String REASON = "reason";
  // 交接状态
  public static final String STATE = "state";
  //交接类型，批量/单个
  public static final String TYPE = "type";
  // 批量交接ID
  public static final String BATCH_ID = "batch_id";
}
