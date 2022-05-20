package com.hd123.baas.sop.service.dao.taskgroup;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author guyahui
 * @date 2021/4/29 20:12
 */
public class PTaskTemplate extends PStandardEntity {

  private static final long serialVersionUID = 4744661080373945712L;

  public static final String TABLE_NAME = "task_template";
  public static final String TABLE_ALIAS = "_task_template";

  public static final String TENANT = "tenant";
  // 巡检主题ID
  public static final String OWNER = "owner";
  // 类
  public static final String TEMPLATE_CLASS = "template_class";
  // 名称
  public static final String NAME = "name";
  // 模版分值
  public static final String SCORE = "score";
  // 内容
  public static final String CONTENT = "content";
  // 业务ID
  public static final String FLOW_NO = "flow_no";
  //备注
  public static final String NOTE = "note";
  // 需要文字反馈
  public static final String WORD_NEEDED = "word_needed";
  // 需要图片反馈
  public static final String IMAGE_NEEDED = "image_needed";
  // 需要视频反馈
  public static final String VIDEO_NEEDED = "video_needed";
  // 排序字段
  public static final String SEQ = "seq";

}
