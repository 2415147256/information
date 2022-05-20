package com.hd123.baas.sop.service.dao.voice;

import com.hd123.rumba.commons.jdbc.entity.PEntity;

/**
 * @author W.J.H.7
 */
public class PVoice extends PEntity {

  public static final String TABLE_NAME = "voice";
  public static final String TABLE_ALIAS = "_voice";

  public static final String TENANT = "tenant";
  public static final String REQUEST_ID = "request_id";
  public static final String TITLE = "title";
  public static final String TEMPLATE_ID = "template_id";
  public static final String TEMPLATE_CODE = "template_code";
  public static final String TEMPLATE_CONTENT = "template_content";
  public static final String CREATED = "created";
}