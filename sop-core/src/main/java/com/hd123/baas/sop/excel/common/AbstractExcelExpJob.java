package com.hd123.baas.sop.excel.common;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hd123.baas.sop.job.AbstractJob;
import com.hd123.baas.sop.job.BJobProgress;
import com.hd123.baas.sop.job.tools.JobContext;
import com.hd123.baas.sop.job.tools.Progresser;
import com.hd123.rumba.oss.api.Bucket;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author zhengzewang on 2020/11/27.
 */
public abstract class AbstractExcelExpJob<T> extends AbstractJob {
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private static final String XLS = ".xls", XLSX = ".xlsx";


  @Autowired(required = false)
  private Bucket bucket;

  /**
   * 开始导入前的操作，如果报错，将会阻断下面的流程。一般用于导入前的前置检查
   *
   * @param tenant
   *     租户
   */
  protected void beforeAll(String tenant) throws Exception {

  }

  /**
   * 导出后的操作，如果报错，将会阻断下面的流程。一般用于导出后的资源释放
   *
   * @param tenant
   *     租户
   */
  protected void afterAll(String tenant) throws Exception {

  }

  protected abstract void handle(String tenant) throws Exception;

  protected abstract String sheetName();

  @Override
  protected final void doExecute() throws Exception {
    Progresser progresser = JobContext.getProgresser();
    try {
      updateProgress(0, "开始导出文件...");
      String tenant = getContextStringValue(TENANT);
      this.beforeAll(tenant);

      initWriter(tenant);

      updateProgress(30, "导出文件工作准备完毕...");
      // 带条件？？？
      handle(tenant);
      File file = ExcelExpContext.finish();
      updateProgress(80, "导出文件写入已完成...");
      String ossUrl = null;
      if (file != null) {
        String ossPath = "sop/tempOf7Day/imp_feedback/" + file.getName();
        try {
          bucket.put(ossPath, new FileInputStream(file));
          ossUrl = bucket.getUrl(ossPath, Bucket.CONTENT_TYPE_OF_WILDCARD);
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
      file.delete();
      progresser.stepBy(100);
      if (ossUrl != null) {
        // 反馈信息
        JobContext.getMergedJobDataMap().put(BJobProgress.JOB_RESULT, ossUrl);
      }
    } finally {
      ExcelExpContext.free();
    }
  }

  /**
   * 默认根据类来定义表头
   */
  protected void initWriter(String tenant) throws IOException {
    ExcelExpContext.init(getTempXlsFile(tenant), getCls(), sheetName());
  }

  private Class<T> getCls() {
    Type type = this.getClass().getGenericSuperclass();
    ParameterizedType parameterizedType = (ParameterizedType) type;
    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
  }

  public File getTempXlsFile(String tenant) throws IOException {
    String defaultBaseDir = System.getProperty("java.io.tmpdir");
    String fileName = getFileName() == null ? FORMAT.format(new Date()) + new Random().nextInt(100000) : getFileName();
    File file = new File(defaultBaseDir, tenant + File.separator + "sop" + File.separator + "temp" + File.separator
        + "export" + File.separator + fileName + XLSX);
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
    return file;
  }

  protected final void export(String tenant, List<T> ts) {
    // 为空时写入空值即可，保留表头
    if (CollectionUtils.isEmpty(ts)) {
      ts = new ArrayList<>();
    }
    ExcelWriter writer = ExcelExpContext.getWriter();
    WriteSheet sheet = ExcelExpContext.getSheet();
    writer.write(ts, sheet);
  }

  @Setter
  @Getter
  public class Context {
    Map<String, String> headFieldMap = new HashMap<>();
    List<List<String>> head = new ArrayList<>();
  }
}
