package com.hd123.baas.sop.evcall.exector.skutag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.skutag.TagService;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhengzewang on 2020/11/18.
 */
@Slf4j
@Component
public class TagDeleteEvCallExecutor extends AbstractEvCallExecutor<TagDeleteMsg> {

  public static final String TAG_DELETE_EXECUTOR_ID = TagDeleteEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private TagService tagService;

  @Override
  protected void doExecute(TagDeleteMsg message, EvCallExecutionContext context) throws Exception {
    try {
      String tenant = message.getTenant();
      Integer uuid = message.getUuid();
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> response = rsH6SOPClient.tagRemove(tenant, String.valueOf(uuid));
      if (!response.isSuccess()) {
        throw new BaasException("调用H6删除标签接口失败:{0}", response.getMsg());
      }
    } catch (Exception e) {
      log.error("TagDeleteEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected TagDeleteMsg decodeMessage(String msg) throws BaasException {
    log.info("TagDeleteMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, TagDeleteMsg.class);
  }

}
