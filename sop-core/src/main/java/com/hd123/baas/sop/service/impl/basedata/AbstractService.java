/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 * <p>
 * 项目名：	mas-service
 * 文件名：	AbstarctMasService.java
 * 模块说明：
 * 修改历史：
 * 2019年3月10日 - __Silent - 创建。
 */
package com.hd123.baas.sop.service.impl.basedata;

import com.hd123.baas.sop.service.api.basedata.Filter;
import com.hd123.baas.sop.service.api.basedata.Sort;
import com.hd123.rumba.commons.biz.entity.OperateContext;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.*;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author __Silent
 *
 */
public abstract class AbstractService {
 
  protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


  protected OperateContext newContext(String fullName) {
    Operator operator = new Operator();
    operator.setId(fullName);
    operator.setFullName(fullName);
    return new OperateContext(operator, new Date());
  }

  /** 分配UUID */
  protected String generateUUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }


  protected <T> T getOne(QueryResult<T> result) {
    if (!result.getRecords().isEmpty()) {
      return result.getRecords().get(0);
    }
    return null;
  }

  /** 写入新建上下文 */
  protected void onCreate(StandardEntity entity, OperateContext context) {
    entity.setVersion(0);
    entity.setCreateInfo(new OperateInfo(context.getOperator()));
    entity.setLastModifyInfo(new OperateInfo(context.getOperator()));
  }

  /** 写入修改上下文 */
  protected void onUpdate(StandardEntity entity, OperateContext context) {
    entity.setVersion(entity.getVersion() + 1);
    entity.setLastModifyInfo(new OperateInfo(context.getOperator()));
  }

  protected void buildQdOrder(Filter filter, QueryDefinition qd) {
    if (!CollectionUtils.isEmpty(filter.getSorts())) {
      for (Sort sort : filter.getSorts()) {
        qd.addOrder(sort.getSortKey(),
            sort.isDesc() ? QueryOrderDirection.desc : QueryOrderDirection.asc);
      }
    }
  }
}
