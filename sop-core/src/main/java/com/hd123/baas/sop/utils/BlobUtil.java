package com.hd123.baas.sop.utils;

import java.io.InputStream;
import java.sql.Blob;

/**
 * @author zhengzewang on 2020/11/18.
 */
public class BlobUtil {

  public static String decode(Blob blob) {
    if (blob != null) {
      try {
        InputStream is = blob.getBinaryStream();
        byte[] b = new byte[is.available()];
        is.read(b, 0, b.length);
        return new String(b, "UTF-8");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

}
