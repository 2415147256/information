package com.hd123.baas.sop.service.dao.announcement;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/20.
 */
public class PAnnouncement extends PStandardEntity {

  public static final String TABLE_NAME = "announcement";
  public static final String TABLE_ALIAS = "_announcement";

  public static final String TENANT = "tenant";
  public static final String ALL_SHOPS = "all_shops";
  public static final String SEND_POS = "send_pos";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String IMAGE = "image";
  public static final String URL = "url";
  public static final String STATE = "state";
  public static final String PROGRESS = "progress";
  public static final String ORG_ID = "org_id";
  public static final String TARGET_TYPE = "target_type";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, ALL_SHOPS, SEND_POS, TITLE, CONTENT, IMAGE, URL, STATE,
        PROGRESS, ORG_ID, TARGET_TYPE);
  }

}
