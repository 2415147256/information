package com.hd123.baas.sop.service.dao.task;

import com.hd123.baas.sop.service.api.task.ShopTaskLog;
import com.hd123.rumba.commons.jdbc.annotation.MapToEntity;
import com.hd123.rumba.commons.jdbc.annotation.SchemaMeta;
import com.hd123.rumba.commons.jdbc.annotation.TableName;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author maodapeng
 * @Since
 */
@Setter
@Getter
@MapToEntity(ShopTaskLog.class)
@SchemaMeta
public class PShopTaskLog extends PStandardEntity {
  @TableName
  public static final String TABLE_NAME = "shop_task_log";
  public static final String TABLE_ALIAS = "_shop_task_log";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";

  public static final String NAME = "name";

  public static final String POINT = "point";
  public static final String POINT_DESC = "point_desc";

  public static final String SCORE = "score";

  public static final String STATE = "state";

  public static final String FEEDBACK = "feedback";

  public static final String TYPE = "type";

  public static final String OPERATOR_ID = "operator_id";

  public static final String OPERATOR_NAME = "operator_name";

  public static final String FINISH_INFO_TIME = "finished";
  public static final String FINISH_INFO_OPERATOR_NAMESPACE = "finishNS";
  public static final String FINISH_INFO_OPERATOR_ID = "finishId";
  public static final String FINISH_INFO_OPERATOR_FULL_NAME = "finishName";
  public static final String FINISH_APPID = "finish_appid";
  //备注
  public static final String NOTE = "note";
  // 需要文字反馈
  public static final String WORD_NEEDED = "word_needed";
  // 需要图片反馈
  public static final String IMAGE_NEEDED = "image_needed";
  // 需要视频反馈
  public static final String VIDEO_NEEDED = "video_needed";

}
