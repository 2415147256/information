package com.hd123.baas.sop.utils;

import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.oss.api.Bucket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * oss工具<br/> 本系统中使用的oss相关操作都在这个类里提供。方便以后迁移oss工具。
 *
 * @author W.J.H.7
 * @since 1.0.0
 */
@Component
public class OssClient {
  @Autowired(required = false)
  private Bucket bucket;
  /** 默认目录,必须 */
  private static final String DIR_NAME = "sop";

  /**
   * 文件上传至oss
   *
   * @param fileName
   *     文件名称,可以包含路径
   * @param inputStream
   *     文件流
   * @return oss资源地址
   */
  public String uploadToOss(String fileName, InputStream inputStream) throws IOException {
    String key = bucket.put(DIR_NAME + "/" + fileName, inputStream);
    inputStream.close();
    return bucket.getUrl(key).split("\\?")[0];
  }

  /**
   * 文件上传至oss
   *
   * @param file
   *     文件
   * @return oss资源地址
   */
  public String uploadToOss(File file) throws IOException {
    return uploadToOss(file, false);
  }

  /**
   * 文件上传至oss，选择是否删除临时文件
   *
   * @param file
   *     文件
   * @param delete
   *     是否需要删除原文件
   * @return oss资源地址
   */
  public String uploadToOss(File file, boolean delete) throws IOException {
    Assert.notNull(file, "file");
    InputStream in = new FileInputStream(file);
    StringBuilder nameBuilder = new StringBuilder();
    nameBuilder.append(System.nanoTime());
    nameBuilder.append(".");
    nameBuilder.append(StringUtils.reverse(StringUtils.reverse(file.getName()).split("\\.")[0]).toLowerCase());
    String key = bucket.put(DIR_NAME + "/" + nameBuilder.toString(), in);
    in.close();
    String url = bucket.getUrl(key);
    if (delete) {
      file.delete();
    }
    return url.split("\\?")[0];
  }

  public String copyObject(String storageId, String newPath, boolean delete) {
    org.springframework.util.Assert.notNull(storageId, "参数storageId不能为空");
    org.springframework.util.Assert.notNull(newPath, "参数newPath不能为空");
    String key = bucket.copy(DIR_NAME + "/" + storageId, DIR_NAME + "/" + newPath);
    if (delete) {
      bucket.delete(DIR_NAME + "/" + storageId);
    }
    return bucket.getUrl(key).split("\\?")[0];
  }

  public String copyNoDirObject(String storageId, String newPath, boolean delete) {
    org.springframework.util.Assert.notNull(storageId, "参数storageId不能为空");
    org.springframework.util.Assert.notNull(newPath, "参数newPath不能为空");
    String key = bucket.copy(storageId, newPath);
    if (delete) {
      bucket.delete(storageId);
    }
    return bucket.getUrl(key).split("\\?")[0];
  }
}