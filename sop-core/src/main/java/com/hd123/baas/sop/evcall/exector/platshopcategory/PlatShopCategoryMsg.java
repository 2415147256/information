/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-parent
 * 文件名：	ShopSkuMsg.java
 * 模块说明：
 * 修改历史：
 * 2021/1/13 - lzy - 创建。
 */
package com.hd123.baas.sop.evcall.exector.platshopcategory;

import com.hd123.baas.sop.evcall.AbstractTenantEvCallMessage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author lzy
 */
@Getter
@Setter
public class PlatShopCategoryMsg extends AbstractTenantEvCallMessage {

  private String taskId;
  private String operator;
  private String orgType;
  private String orgId;

}
