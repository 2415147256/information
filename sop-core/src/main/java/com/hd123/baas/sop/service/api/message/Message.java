package com.hd123.baas.sop.service.api.message;

import com.hd123.baas.sop.remote.fms.bean.MediaInfo;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.StandardEntity;
import com.hd123.rumba.commons.biz.query.QueryEntity;
import com.hd123.rumba.commons.biz.query.QueryFactors;
import com.hd123.rumba.commons.biz.query.QueryField;
import com.hd123.rumba.commons.biz.query.QueryOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zhengzewang on 2020/11/9.
 */
@Getter
@Setter
public class Message extends StandardEntity {

  // 租户
  private String tenant;
  private String orgId;
  private String appId;
  // 门店
  private String shop;
  private String shopCode;
  private String shopName;
  // 类型
  private MessageType type = MessageType.NOTICE;
  // 点击动作。
  private MessageAction action = MessageAction.DETAIL;
  // 动作信息。如果是url则为链接。如果是page则为path，具体的值是否需要配置由调用方决定
  private String actionInfo;

  private String title;
  // 以后有富文本的需求再考虑吧
  private Map<MessageContentKey, String> content = new LinkedHashMap<>();
  // 是否已读
  private boolean read;

  // 消息来源。如果是公告，则为公号id
  private String source;

  // 阅读设备
  private String readAppId;
  // 阅读人信息
  private OperateInfo readInfo;
  // 序号
  private Long seq;
  // 是否发布到收银机
  private boolean sendPos;
  // 调用方可以自定义一些标签，用于透传给接收方
  private String tag;

  // 处理人
  private String userId;
  // 动作对像
  private MediaInfo mediaInfo;

  @QueryEntity(Message.class)
  public static class Queries extends QueryFactors.StandardEntity {

    private static final String PREFIX = Message.class.getName() + "::";

    @QueryField
    public static final String TENANT = PREFIX + "tenant";
    @QueryField
    public static final String SHOP = PREFIX + "shop";
    @QueryField
    public static final String APP_ID = PREFIX + "appId";
    @QueryField
    public static final String TYPE = PREFIX + "type";
    @QueryField
    public static final String READ = PREFIX + "read";
    @QueryField
    public static final String SEQ = PREFIX + "seq";
    @QueryField
    public static final String SEND_POS = PREFIX + "sendPos";
    @QueryField
    public static final String SOURCE = PREFIX + "source";
    @QueryField
    public static final String USER_ID = PREFIX + "userId";
    @QueryField
    public static final String ORG_ID = PREFIX + "orgId";

    @QueryOperation
    public static final String SHOP_KEYWORD = PREFIX + "shopKeyword";
    @QueryOperation
    public static final String OWNERSHIP_IN = PREFIX + "ownership in";
  }

}
