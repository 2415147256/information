package com.hd123.baas.sop.service.api.voice;

import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.qianfan123.baas.common.BaasException;

/**
 * @author W.J.H.7
 * @since 1.0.0
 */
public interface VoiceService {

  /**
   * 根据uuid获取语音对象，以及语音行
   * @param tenant 租户
   * @param uuid 主键
   * @param fetchParts 分片参数
   * @return
   */
  Voice get(String tenant, String uuid, String ... fetchParts);

  /**
   * 发起呼叫语音-异步
   * 
   * @param tenant
   *          租户
   * @param item
   *          语音对象
   * @param operateInfo
   *          操作对象
   * @return 业务uuid
   */
  String call(String tenant, Voice item, OperateInfo operateInfo) throws BaasException;


  /**
   * 自定义查询
   * 
   * @param tenant
   *          租户
   * @param qd
   *          查询对象
   * @return 返回一组语音对象
   */
  QueryResult<Voice> query(String tenant, QueryDefinition qd);

}
