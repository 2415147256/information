package com.hd123.baas.sop.service.dao.voice;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author W.J.H.7
 */
public class PVoiceLine extends PEntity {

  public static final String TABLE_NAME = "voice_line";
  public static final String TABLE_ALIAS = "_voice_line";

  public static final String TENANT = "tenant";
  public static final String OWNER = "owner";
  public static final String SHOP_ID = "shop_id";
  public static final String SHOP_CODE = "shop_code";
  public static final String SHOP_NAME = "shop_name";
  public static final String CALLEE = "callee";
  public static final String TEMPLATE_PARAS = "template_paras";
}