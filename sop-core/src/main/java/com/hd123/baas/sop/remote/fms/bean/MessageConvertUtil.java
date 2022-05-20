package com.hd123.baas.sop.remote.fms.bean;

import com.hd123.baas.sop.service.api.announcement.AnnouncementTargetType;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;
import com.hd123.rumba.commons.util.converter.ConverterBuilder;
import com.hd123.rumba.commons.util.converter.EnumConverters;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public interface MessageConvertUtil {
  Converter<BAppMessage, Message> APP_MESSAGE_TO_MESSAGE = ConverterBuilder
      .newBuilder(BAppMessage.class, Message.class)
      .mapByType(String.class, MessageType.class, EnumConverters.toEnum(MessageType.class))
      .mapByType(String.class, MessageAction.class, EnumConverters.toEnum(MessageAction.class))
      .map("content", "content", new Converter<Map<String, String>, Map<MessageContentKey, String>>() {
        @Override
        public Map<MessageContentKey, String> convert(Map<String, String> source) throws ConversionException {
          if (source == null) {
            return null;
          }
          HashMap<MessageContentKey, String> target = new HashMap<>();
          source.forEach((k, v) -> {
            target.put(MessageContentKey.valueOf(k), v);
          });
          return target;
        }
      })
      .build();

  Converter<Message, AppMessageSaveNewReq> MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ = new Converter<Message, AppMessageSaveNewReq>() {
    @Override
    public AppMessageSaveNewReq convert(Message message) throws ConversionException {
      AppMessageSaveNewReq target = new AppMessageSaveNewReq();
      target.setSource(message.getSource());
      target.setAppId(message.getAppId());
      target.setShop(message.getShop());
      target.setShopName(message.getShopName());
      target.setShopCode(message.getShopCode());
      target.setType(message.getType().name());
      target.setJumpType(message.getAction().name());
      target.setJumpUrl(message.getActionInfo());
      target.setTag(message.getTag());
      target.setTitle(message.getTitle());
      target.setTextContent(message.getContent().get(MessageContentKey.TEXT));
      target.setImageContent(message.getContent().get(MessageContentKey.IMAGE));
      target.setUrlContent(message.getContent().get(MessageContentKey.URL));
      target.setUserId(message.getUserId());
      target.setAppId(message.getAppId());
      target.setSendPos(message.isSendPos());
      target.setOperateInfo(message.getCreateInfo());
      target.setMediaInfo(message.getMediaInfo());
      return target;
    }
  };

  Converter<Message, AppMsgSNReq> MESSAGE_TO_APP_MESSAGE_SAVE_NEW_REQ_v2 = new Converter<Message, AppMsgSNReq>() {
    @Override
    public AppMsgSNReq convert(Message message) throws ConversionException {
      AppMsgSNReq target = new AppMsgSNReq();
      target.setType(message.getType().name());
      target.setAction(message.getAction().name());
      target.setActionInfo(message.getActionInfo());
      target.setTitle(message.getTitle());
      AppMessageContent content = new AppMessageContent();
      if (StringUtils.isNotBlank(message.getContent().get(MessageContentKey.TEXT))) {
        content.setText(message.getContent().get(MessageContentKey.TEXT));
      }
      if (StringUtils.isNotBlank(message.getContent().get(MessageContentKey.IMAGE))) {
        content.setImage(message.getContent().get(MessageContentKey.IMAGE));
      }
      if (StringUtils.isNotBlank(message.getContent().get(MessageContentKey.URL))) {
        content.setUrl(message.getContent().get(MessageContentKey.URL));
      }
      target.setContent(JsonUtil.objectToJson(content));
      target.setSeq(Math.toIntExact(message.getSeq()));
      target.setSourceId(message.getSource());
      target.setSourceAppId(message.getAppId());
      target.setTargetType(AnnouncementTargetType.FRANCHISEE.name());
      target.setTargetId(message.getShop());
      target.setTargetName(message.getShopName());
      target.setTargetCode(message.getShopCode());
      target.setOperateInfo(message.getCreateInfo());
      return target;
    }
  };
}
