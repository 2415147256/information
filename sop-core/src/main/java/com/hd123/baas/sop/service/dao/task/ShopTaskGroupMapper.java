package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskGroup;
import com.hd123.baas.sop.service.api.task.ShopTaskGroupState;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupType;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ShopTaskGroupMapper extends PStandardEntity.RowMapper<ShopTaskGroup> {
  @Override
  public ShopTaskGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
    ShopTaskGroup result = new ShopTaskGroup();
    super.mapFields(rs, rowNum, result);
    OperateInfo finishInfo = new OperateInfo();
    finishInfo.setTime(rs.getTimestamp(PShopTaskGroup.FINISH_INFO_TIME));
    Operator operator = new Operator();
    operator.setId(rs.getString(PShopTaskGroup.FINISH_INFO_OPERATOR_ID));
    operator.setNamespace(rs.getString(PShopTaskGroup.FINISH_INFO_OPERATOR_NAMESPACE));
    operator.setFullName(rs.getString(PShopTaskGroup.FINISH_INFO_OPERATOR_FULL_NAME));
    finishInfo.setOperator(operator);
    result.setFinishInfo(finishInfo);
    result.setOrgId(rs.getString(PShopTaskGroup.ORG_ID));
    result.setFinishAppid(rs.getString(PShopTaskGroup.FINISH_APPID));
    result.setTaskGroup(rs.getString(PShopTaskGroup.TASK_GROUP));
    result.setGroupName(rs.getString(PShopTaskGroup.GROUP_NAME));
    result.setPlanTime(rs.getTimestamp(PShopTaskGroup.PLAN_TIME));
    result.setRemindTime(rs.getTimestamp(PShopTaskGroup.REMIND_TIME));
    result.setShop(rs.getString(PShopTaskGroup.SHOP));
    result.setShopName(rs.getString(PShopTaskGroup.SHOP_NAME));
    result.setShopCode(rs.getString(PShopTaskGroup.SHOP_CODE));
    result.setState(ShopTaskGroupState.valueOf(rs.getString(PShopTaskGroup.STATE)));
    result.setType(TaskGroupType.valueOf(rs.getString(PShopTaskGroup.TYPE)));
    result.setTenant(rs.getString(PShopTaskGroup.TENANT));
    result.setEarliestFinishTime(rs.getTimestamp(PShopTaskGroup.EARLIEST_FINISH_TIME));
    return result;
  }
}
