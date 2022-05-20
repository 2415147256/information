package com.hd123.baas.sop.service.impl.voice;

import com.hd123.baas.sop.annotation.Tx;
import com.hd123.baas.sop.service.api.voice.Voice;
import com.hd123.baas.sop.service.api.voice.VoiceLine;
import com.hd123.baas.sop.service.api.voice.VoiceService;
import com.hd123.baas.sop.service.api.voice.VoiceTemplate;
import com.hd123.baas.sop.service.dao.voice.VoiceDaoBof;
import com.hd123.baas.sop.evcall.EvCallEventPublisher;
import com.hd123.baas.sop.evcall.exector.voice.VoiceCallEvCallExecutor;
import com.hd123.baas.sop.evcall.exector.voice.VoiceCallMsg;
import com.hd123.baas.sop.remote.fms.FmsV2Client;
import com.hd123.baas.sop.remote.fms.bean.BMsgPushTemplate;
import com.hd123.baas.sop.utils.IdGenUtils;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryDefinition;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http2.BaasResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author W.J.H.7
 */
@Slf4j
@Service
public class VoiceServiceImpl implements VoiceService {


  @Autowired
  private VoiceDaoBof voiceDao;
  @Autowired
  private EvCallEventPublisher publisher;
  @Autowired
  private FmsV2Client fmsV2Client;

  @Override
  public Voice get(String tenant, String uuid, String... fetchParts) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(uuid, "uuid");
    Voice voice = voiceDao.get(tenant, uuid);
    if (voice == null) {
      return null;
    }
    for (String fetchPart : fetchParts) {
      if (Voice.LINES.equals(fetchPart)) {
        List<VoiceLine> lines = voiceDao.getLineByOwner(tenant, uuid);
        voice.setLines(lines);
      }
    }
    return voice;
  }

  @Tx
  @Override
  public String call(String tenant, Voice voice, OperateInfo operateInfo) throws BaasException {
    Assert.notNull(tenant, "租户");
    Assert.notNull(voice, "voice");
    Assert.notNull(voice.getRequestId(), "voice.requestId");
    Assert.notNull(voice.getTemplateCode(), "voice.templateCode");

    if (CollectionUtils.isEmpty(voice.getLines())) {
      return null;
    }

    // 检查幂等
    Voice history = voiceDao.getByRequestId(tenant, voice.getRequestId());
    if (history != null) {
      return history.getUuid();
    }
    String templateCode = voice.getTemplateCode().name();
    VoiceTemplate template = getTemplate(tenant, templateCode);
    // 补全数据
    build(tenant, voice, template);
    //
    voiceDao.insert(tenant, voice);
    if (CollectionUtils.isNotEmpty(voice.getLines())) {
      voiceDao.insertLine(tenant, voice.getUuid(), voice.getLines());
    }
    publishEvCall(voice);
    return voice.getUuid();
  }

  private VoiceTemplate getTemplate(String tenant, String templateCode) throws BaasException {
    BaasResponse<BMsgPushTemplate> response = fmsV2Client.getTemplateByCode(tenant, templateCode);
    if (response.isSuccess() && response.getData() == null) {
      log.error("查询{}模板为空或异常", templateCode);
      throw new BaasException("查询{}模板为空或异常", templateCode);
    }
    return response.getData().toVoiceTemplate();
  }


  @Override
  public QueryResult<Voice> query(String tenant, QueryDefinition qd) {
    Assert.notNull(tenant, "租户");
    Assert.notNull(qd, "qd");

    return voiceDao.query(tenant, qd);
  }

  private void build(String tenant, Voice voice, VoiceTemplate template) {
    Assert.notNull(voice, "voice");

    voice.setTenant(tenant);
    if (StringUtils.isEmpty(voice.getUuid())) {
      voice.setUuid(IdGenUtils.buildIidAsString());
    }

    if (template != null) {
      voice.setTemplateId(template.getUuid());
      voice.setTemplateContent(template.getContent());
    }

    voice.setCreated(new Date());
    if (CollectionUtils.isNotEmpty(voice.getLines())) {
      int i = 0;
      for (VoiceLine line : voice.getLines()) {
        i++;
        line.setUuid(voice.getUuid() + i);
      }
    }
  }

  /**
   * 发送消息
   */
  private void publishEvCall(Voice voice) {
    VoiceCallMsg msg = new VoiceCallMsg();
    msg.setTenant(voice.getTenant());
    msg.setPk(voice.getUuid());
    log.info("准备推送消息：{}", JsonUtil.objectToJson(msg));
    publisher.publishForNormal(VoiceCallEvCallExecutor.VOICE_CALL_MSG_EXECUTOR_ID, msg);
  }
}
