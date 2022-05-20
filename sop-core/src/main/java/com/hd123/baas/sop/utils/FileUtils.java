package com.hd123.baas.sop.utils;

import java.io.File;

/**
 * @author zhengzewang on 2020/11/24.
 */
public class FileUtils {

  public static boolean deleteFile(File file) {
    if (!file.exists()) {
      return false;
    }
    if (file.isDirectory()) {
      File[] children = file.listFiles();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteFile(children[i]);
        if (!success) {
          return false;
        }
      }
    }
    // 目录此时为空，可以删除
    return file.delete();
  }

}
