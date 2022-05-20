package com.hd123.baas.sop.service.dao.offset;

/**
 * @author zhengzewang on 2020/11/17.
 */
public class POffset {

  public static final String TABLE_NAME = "offset";
  public static final String TABLE_ALIAS = "_offset";

  public static final String TENANT = "tenant";
  public static final String TYPE = "type";
  public static final String SPEC = "spec";
  public static final String SEQ = "seq";

  public static String[] allColumns() {
    return new String[] {
        TENANT, TYPE, SPEC, SEQ };
  }

}
