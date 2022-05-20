package com.hd123.baas.sop.service.dao.message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hd123.baas.sop.service.api.message.Message;
import com.hd123.baas.sop.service.api.message.MessageAction;
import com.hd123.baas.sop.service.api.message.MessageContentKey;
import com.hd123.baas.sop.service.api.message.MessageType;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.baas.sop.utils.BlobUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.entity.Operator;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.qianfan123.baas.common.BaasException;

/**
 * @author zhengzewang on 2020/11/17.
 */
public class MessageMapper extends PStandardEntity.RowMapper<Message> {
  @Override
  public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
    Message entity = new Message();
    super.mapFields(rs, rowNum, entity);
    entity.setTenant(rs.getString(PMessage.TENANT));
    entity.setShop(rs.getString(PMessage.SHOP));
    entity.setShopCode(rs.getString(PMessage.SHOP_CODE));
    entity.setShopName(rs.getString(PMessage.SHOP_NAME));
    entity.setType(MessageType.valueOf(rs.getString(PMessage.TYPE)));
    entity.setAction(MessageAction.valueOf(rs.getString(PMessage.ACTION)));
    entity.setActionInfo(rs.getString(PMessage.ACTION_INFO));
    entity.setTitle(rs.getString(PMessage.TITLE));
    try {
      entity.setContent(BaasJSONUtil.safeToObject(BlobUtil.decode(rs.getBlob(PMessage.CONTENT)),
          new TypeReference<LinkedHashMap<MessageContentKey, String>>() {
          }));
      if (entity.getContent() == null) {
        entity.setContent(new LinkedHashMap<>());
      }
    } catch (BaasException e) {
      throw new SQLException(e);
    }
    entity.setRead(rs.getBoolean("read"));
    entity.setReadAppId(rs.getString(PMessage.READ_APP_ID));
    entity.setSeq(rs.getLong(PMessage.SEQ));
    entity.setSendPos(rs.getBoolean(PMessage.SEND_POS));
    entity.setTag(rs.getString(PMessage.TAG));
    entity.setSource(rs.getString(PMessage.SOURCE));

    OperateInfo readerInfo = new OperateInfo();
    readerInfo.setTime(rs.getTimestamp(PMessage.READ_INFO_TIME));
    Operator operator = new Operator();
    operator.setId(rs.getString(PMessage.READ_INFO_OPERATOR_ID));
    operator.setNamespace(rs.getString(PMessage.READ_INFO_OPERATOR_NAMESPACE));
    operator.setFullName(rs.getString(PMessage.READ_INFO_OPERATOR_FULL_NAME));
    readerInfo.setOperator(operator);
    entity.setReadInfo(readerInfo);

    entity.setUserId(rs.getString(PMessage.USER_ID));

    return entity;
  }
}
