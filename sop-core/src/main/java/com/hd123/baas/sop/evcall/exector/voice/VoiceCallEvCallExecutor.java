package com.hd123.baas.sop.evcall.exector.voice;

import com.hd123.baas.sop.service.api.voice.Voice;
import com.hd123.baas.sop.service.api.voice.VoiceLine;
import com.hd123.baas.sop.service.api.voice.VoiceService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.remote.fms.FmsV2Client;
import com.hd123.baas.sop.remote.fms.bean.Ext;
import com.hd123.baas.sop.remote.fms.bean.VoiceMsgPushReq;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author W.J.H.7
 */
@Slf4j
@Component
public class VoiceCallEvCallExecutor extends AbstractEvCallExecutor<VoiceCallMsg> {

  public static final String VOICE_CALL_MSG_EXECUTOR_ID = VoiceCallEvCallExecutor.class.getSimpleName();

  @Autowired
  private VoiceService voiceService;
  @Autowired
  private FmsV2Client fmsV2Client;

  @Override
  protected void doExecute(VoiceCallMsg msg, EvCallExecutionContext context) throws Exception {
    String tenant = msg.getTenant();
    String uuid = msg.getPk();
    Voice voice = voiceService.get(tenant, uuid, Voice.LINES);
    List<VoiceMsgPushReq> list = new ArrayList<>();
    for (VoiceLine line : voice.getLines()) {
      final VoiceMsgPushReq voiceMsgPushReq = new VoiceMsgPushReq();
      voiceMsgPushReq.setOutId(voice.getUuid());
      voiceMsgPushReq.setTemplateId(voice.getTemplateId());
      voiceMsgPushReq.setCallee(line.getCallee());
      voiceMsgPushReq.setTemplateParams(line.getTemplateParams());
      HashMap<String, String> ext = new HashMap<>();
      ext.put(Ext.VOICE_LINE_ID, line.getUuid());
      ext.put(Ext.SHOP, JSONUtil.safeToJson(line.getShop()));
      voiceMsgPushReq.setExt(ext);
      list.add(voiceMsgPushReq);
    }
    fmsV2Client.batchPushVoiceByTemplate(tenant, list);
  }

  @Override
  protected VoiceCallMsg decodeMessage(String msg) throws BaasException {
    log.info("收到VoiceCallMsg:{}", msg);
    return BaasJSONUtil.safeToObject(msg, VoiceCallMsg.class);
  }

}
