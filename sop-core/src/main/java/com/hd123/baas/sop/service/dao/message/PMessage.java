package com.hd123.baas.sop.service.dao.message;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author zhengzewang on 2020/11/17.
 */
public class PMessage extends PStandardEntity {

  public static final String TABLE_NAME = "message";
  public static final String TABLE_ALIAS = "_message";

  public static final String TENANT = "tenant";
  public static final String ORG_ID = "org_id";
  public static final String APP_ID = "app_id";
  public static final String SHOP = "shop";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";

  public static final String TYPE = "type";
  public static final String ACTION = "action";
  public static final String ACTION_INFO = "action_info";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String READ = "`read`";
  public static final String SOURCE = "source";
  public static final String SEQ = "seq";
  public static final String SEND_POS = "send_pos";
  public static final String TAG = "tag";

  public static final String READ_APP_ID = "read_app_id";
  public static final String READ_INFO_TIME = "read_time";
  public static final String READ_INFO_OPERATOR_NAMESPACE = "reader_ns";
  public static final String READ_INFO_OPERATOR_ID = "reader_id";
  public static final String READ_INFO_OPERATOR_FULL_NAME = "reader_name";
  
  public static final String USER_ID = "user_id";

  public static String[] allColumns() {
    return toColumnArray(PStandardEntity.allColumns(), TENANT, SHOP, SHOP_CODE, SHOP_NAME, TYPE, ACTION, ACTION_INFO,
        TITLE, CONTENT, READ, SOURCE, READ_APP_ID, SEQ, SEND_POS, TAG, READ_INFO_TIME, READ_INFO_OPERATOR_NAMESPACE,
        READ_INFO_OPERATOR_ID, READ_INFO_OPERATOR_FULL_NAME,USER_ID, APP_ID);
  }

}
