package com.hd123.baas.sop.utils;

import com.qianfan123.baas.common.BaasException;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zhengzewang on 2020/11/24.
 */
public class ZipUtils {

  // zip后缀
  public static final String ZIP_SUFFIX = ".zip";

  public static void zip(File sourceFile, File target) throws BaasException {
    zip(sourceFile, target, false);
  }

  public static void zip(File sourceFile, File target, boolean containParent) throws BaasException {
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target))) {
      compress(out, sourceFile,
          containParent ? sourceFile.getName() : (sourceFile.isDirectory() ? null : sourceFile.getName()));
    } catch (Exception e) {
      throw new BaasException(e);
    }
  }

  private static void compress(ZipOutputStream out, File sourceFile, String base) throws Exception {
    if (sourceFile.isDirectory()) {
      File[] flist = sourceFile.listFiles();
      if (flist.length == 0) {
        out.putNextEntry(new ZipEntry(StringUtils.isBlank(base) ? "" : (base + "/")));
      } else {
        for (File file : flist) {
          if (file.getName().endsWith(ZIP_SUFFIX)) {
            continue;
          }
          compress(out, file, (StringUtils.isBlank(base) ? "" : (base + "/")) + file.getName());
        }
      }
    } else {
      try (FileInputStream fos = new FileInputStream(sourceFile); BufferedInputStream bis = new BufferedInputStream(fos)//
      ) {
        out.putNextEntry(new ZipEntry(base));
        int tag;
        while ((tag = bis.read()) != -1) {
          out.write(tag);
        }
      }
    }
  }

}
