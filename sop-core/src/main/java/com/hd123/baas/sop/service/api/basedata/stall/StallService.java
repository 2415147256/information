/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	sop-commons
 * 文件名：	StallService.java
  * 模块说明：	
 * 修改历史：

 * 2021年1月4日 - lsz - 创建。
 */
package com.hd123.baas.sop.service.api.basedata.stall;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

/**
 * 
 * @author lsz
 */
public interface StallService {

  QueryResult<Stall> query(String tenant, StallFilter filter) throws BaasException;

  BaasResponse<Void> create(String tenant,String orgType, String orgId,  StallCreation creation, String operator) throws BaasException;

  BaasResponse<Void> modify(String tenant, String orgType, String orgId, String id, StallModification modification, String operator) throws BaasException;
  
  BaasResponse<Void> bindPosToStall(String tenant,String orgType, String orgId, PosToStallBind posToStallBind) throws BaasException;

  BaasResponse<Void> enable(String tenant,String orgType, String orgId,  String id, String operator) throws BaasException;

  BaasResponse<Void> disable(String tenant, String orgType, String orgId, String id, String operator) throws BaasException;

}
