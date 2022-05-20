package com.hd123.baas.sop.evcall.exector.skutag;

import com.hd123.baas.sop.common.DefaultOrgIdConvert;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.skutag.Tag;
import com.hd123.baas.sop.service.api.skutag.TagService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.skutag.GdTag;
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
public class TagSaveEvCallExecutor extends AbstractEvCallExecutor<TagSaveMsg> {

  public static final String TAG_SAVE_EXECUTOR_ID = TagSaveEvCallExecutor.class.getSimpleName();

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private TagService tagService;

  @Override
  protected void doExecute(TagSaveMsg message, EvCallExecutionContext context) throws Exception {
    try {
      String tenant = message.getTenant();
      Integer uuid = message.getUuid();
      Tag tag = tagService.get(tenant, uuid);
      if (tag == null) {
        throw new BaasException("标签不存在");
      }
      GdTag gdTag = new GdTag();
      gdTag.setOrgGid(Integer.parseInt(DefaultOrgIdConvert.toH6DefOrgId(tag.getOrgId())));
      gdTag.setTagId(String.valueOf(uuid));
      gdTag.setTagName(tag.getName());
      RsH6SOPClient rsH6SOPClient = feignClientMgr.getClient(message.getTenant(), null, RsH6SOPClient.class);
      BaasResponse<Void> response = rsH6SOPClient.tagSave(tenant, gdTag);
      if (!response.isSuccess()) {
        throw new BaasException("调用H6保存标签接口失败:{0}", response.getMsg());
      }
    } catch (Exception e) {
      log.error("TagSaveEvCallExecutor错误", e);
      throw e;
    }
  }

  @Override
  protected TagSaveMsg decodeMessage(String msg) throws BaasException {
    log.info("TagSaveMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, TagSaveMsg.class);
  }

}
