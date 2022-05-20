package com.hd123.baas.sop.service.dao.menu;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

/**
 * @author W.J.H.7
 * @since 2022-01-23
 */
public class PMenu extends PStandardEntity {

  public static final String TABLE_NAME = "menu";
  public static final String TABLE_ALIAS = "_menu";

  public static final String TENANT = "tenant";
  public static final String CODE = "code";
  public static final String PATH = "path";
  public static final String UPPER_CODE = "upper_code";
  public static final String TITLE = "title";
  public static final String TYPE = "type";
  public static final String ICON = "icon";
  public static final String PARAMETERS = "parameters";
  public static final String SEQUENCE = "sequence";
}