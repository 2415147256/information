package com.hd123.baas.sop.excel.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hd123.baas.sop.job.AbstractJob;
import com.hd123.baas.sop.job.BJobProgress;
import com.hd123.baas.sop.job.tools.JobContext;
import com.hd123.baas.sop.job.tools.Progresser;
import com.hd123.baas.sop.utils.JsonUtil;
import com.hd123.rumba.oss.api.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Slf4j
public abstract class AbstractExcelImpJob<T extends AbstractBean> extends AbstractJob {
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private static final String XLS = ".xls", XLSX = ".xlsx";
  private final ThreadLocal<List<T>> DATA = new ThreadLocal<>();
  // 表头数据
  public static final ThreadLocal<List<List<String>>> HEAD = new ThreadLocal<>();

  public static final String OSS_PATH = "job_oss_path";
  public static final String ORG_ID = "org_id";

  public static final String BEAN_CLASS = "bean_class";

  public static final String IS_CUSTOM_HEAD = "is_custom_head";

  public static final String USER_ID = "user_id";

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

  // 数据处理的前置操作
  protected void before(String tenant, List<T> ts) {
    // NOTHING
  }

  /**
   * 处理数据
   * <p>
   * 需按顺序返回处理成功或失败或忽略的结果。反馈信息存储在 {@link AbstractBean#setFeedbackMsg(String)} ()}
   * <p>
   * 不返回处理结果则认为是成功
   * <p>
   * 注：请勿改变list中元素的顺序和值的属性
   *
   * @param tenant
   *     租户
   * @param ts
   *     数据
   * @throws Exception
   *     如果抛出异常，则认为这一批全都失败，且异常因为为失败的反馈信息
   */
  protected abstract int[] handle(String tenant, List<T> ts) throws Exception;

  // 数据处理的后置操作
  protected void after(String tenant, List<T> ts) {
    // NOTHING
  }

  /**
   * 数据全部处理完的操作。一般可用于通知
   *
   * @param tenant
   *     租户
   */
  protected void afterAll(String tenant) {
    // NOTHING
  }

  // 处理反馈信息
  protected void handleFeedback(String tenant, List<T> ts, int[] result) {
    if (result == null || !generateFeedbackFile()) {
      return;
    }

    // 过滤
    boolean inSuccess = includeSuccessGenerateFeedbackFile();
    boolean inIgnore = includeIgnoreGenerateFeedbackFile();
    List<T> todo = new ArrayList<>();
    for (int i = 0; i < ts.size() && i < result.length; i++) {
      int rs = result[i];
      if (rs == AbstractBean.SUCCESS && !inSuccess) {
        continue;
      }
      if (rs == AbstractBean.IGNORE && !inIgnore) {
        continue;
      }
      todo.add(ts.get(i));
    }
    if (todo.isEmpty()) {
      return;
    }

    // 生成反馈文件
    ExcelWriter writer = ExcelExpContext.getWriter();
    if (writer == null) {
      File file;
      try {
        file = getTempXlsFile(tenant);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      if (CollectionUtils.isNotEmpty(HEAD.get())) {
        ExcelExpContext.initAndHead(file, HEAD.get(), "sheet1");
      } else {
        ExcelExpContext.init(file, todo.get(0).getClass(), "sheet1");
      }
      writer = ExcelExpContext.getWriter();
    }
    WriteSheet sheet = ExcelExpContext.getSheet();
    writer.write(todo, sheet);
  }

  private boolean isCustomHead() {
    String isCustomHead = this.getDataMap().getString(AbstractExcelImpJob.IS_CUSTOM_HEAD);
    return "true".equals(isCustomHead);
  }

  @Override
  protected final void doExecute() throws InterruptedException, Exception {
    Progresser progresser = JobContext.getProgresser();
    try {
      updateProgress(0, "开始导入文件...");
      String tenant = getContextStringValue(TENANT);
      this.begin(tenant);
      this.beforeAll(tenant);
      updateProgress(20, "导入文件前置工作已完成...");
      // 文件地址
      String ossPath = getContextStringValue(OSS_PATH);
      // String ossUrl = bucket.getUrl(ossPath, Bucket.CONTENT_TYPE_OF_WILDCARD);

      // 读取文件
      AnalysisEventListener<T> listener = getListener(tenant, this);
      updateProgress(30, "文件读取监听器初始化已完成...");
      EasyExcel.read(new URL(ossPath).openStream(), getCls(), listener).sheet().headRowNumber(setTableRowNumber()).doRead();
      updateProgress(70, "文件读取工作已完成...");
    } finally {
      ExcelExpContext.free();
    }
  }

  //默认表格头为一行
  protected Integer setTableRowNumber() throws IOException {
    return 1;
  }

  // 开始
  private void begin(String tenant) {
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_SUCCESS_COUNT, 0);
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_FAIL_COUNT, 0);
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_IGNORE_COUNT, 0);
  }

  private void doImport(String tenant, T t) {
    int tResult = check(t);
    if (tResult != t.SUCCESS) {
      /**
       * 如果出现错误 则会中断缓存。
       *
       * {@link batchHandleSize()} 为什么数量可能小设置值的原因
       */
      List<T> list = DATA.get();
      if (list != null) {
        if (!list.isEmpty()) {
          this.doHandle(tenant, list);
        }
        DATA.remove();
      }
      int fail = JobContext.getMergedJobDataMap().getInt(BJobProgress.JOB_FAIL_COUNT) + 1;
      JobContext.getMergedJobDataMap().put(BJobProgress.JOB_FAIL_COUNT, fail);
      this.handleFeedback(tenant, Arrays.asList(t), new int[] {
          tResult });
      return;
    }
    int batchSize = batchHandleSize();
    List<T> ts = null;
    if (batchSize > 1) {
      List<T> list = DATA.get();
      if (list == null) {
        list = new ArrayList<>();
        DATA.set(list);
      }
      list.add(t);
      if (list.size() >= batchSize) {
        ts = list;
        DATA.remove();
      }
    } else {
      ts = new ArrayList<>();
      ts.add(t);
    }
    if (ts != null) {
      this.doHandle(tenant, ts);
    }
  }

  private void doHandle(String tenant, List<T> ts) {
    this.before(tenant, ts);
    int[] result;
    try {
      result = this.handle(tenant, ts);
    } catch (Exception e) {
      log.error("导入异常日志：{}", JsonUtil.objectToJson(e));
      result = new int[ts.size()];
      for (int i = 0; i < ts.size(); i++) {
        T t = ts.get(i);
        t.setFeedbackMsg(e.getMessage());
        result[i] = T.FAIL;
      }
    }
    if (result == null) {
      result = new int[0];
    }
    int addSuccess = 0;
    int addIgnore = 0;
    int addFail = 0;
    for (int i = 0; i < ts.size() && i < result.length; i++) {
      int rs = result[i];
      if (rs == AbstractBean.FAIL) {
        addFail++;
      } else if (rs == AbstractBean.IGNORE) {
        addIgnore++;
      } else {
        addSuccess++;
      }
    }
    if (result.length < ts.size()) {
      addSuccess = addSuccess + (ts.size() - result.length);
    }
    int success = JobContext.getMergedJobDataMap().getInt(BJobProgress.JOB_SUCCESS_COUNT) + addSuccess;
    int fail = JobContext.getMergedJobDataMap().getInt(BJobProgress.JOB_FAIL_COUNT) + addFail;
    int ignore = JobContext.getMergedJobDataMap().getInt(BJobProgress.JOB_IGNORE_COUNT) + addIgnore;
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_SUCCESS_COUNT, success);
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_FAIL_COUNT, fail);
    JobContext.getMergedJobDataMap().put(BJobProgress.JOB_IGNORE_COUNT, ignore);
    this.handleFeedback(tenant, ts, result);
    //实时保存处理结果
    this.processing();
    this.after(tenant, ts);
  }

  private int check(T t) {
    Class tClass = t.getClass();
    Field[] fields = tClass.getDeclaredFields();
    int result = AbstractBean.SUCCESS;
    StringBuffer msg = new StringBuffer();
    for (Field field : fields) {
      NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
      if (notEmpty == null) {
        continue;
      }
      String column = field.getName();
      ExcelProperty property = field.getAnnotation(ExcelProperty.class);
      if (property != null && property.value().length > 0 && StringUtils.isNotBlank(property.value()[0])) {
        column = property.value()[0];
      }
      field.setAccessible(true);
      try {
        Object value = field.get(t);
        if (value == null) {
          t.setFeedbackMsg(column + "不能为空");
          result = AbstractBean.FAIL;
          msg.append(column).append(":").append("不能为空").append(";");
        }
      } catch (IllegalAccessException e) {
        msg.append(column).append(":").append(e.getMessage()).append(";");
        result = AbstractBean.FAIL;
      }
    }

    // 检查数字是否合法
    for (Field field : fields) {
      NumberFormat annotation = field.getAnnotation(NumberFormat.class);
      if (annotation == null) {
        continue;
      }

      String column = field.getName();
      ExcelProperty property = field.getAnnotation(ExcelProperty.class);
      if (property != null && property.value().length > 0 && StringUtils.isNotBlank(property.value()[0])) {
        column = property.value()[0];
      }

      field.setAccessible(true);
      try {
        Object value = field.get(t);
        if (value == null) {
          continue;
        }
        if (value instanceof String) {
          try {
            new BigDecimal(value.toString());
          } catch (NumberFormatException e) {
            msg.append(column).append(":").append("数字格式错误").append(";");
            result = AbstractBean.FAIL;
          }
        }
      } catch (IllegalAccessException e) {
        msg.append(column).append(":").append(e.getMessage()).append(";");
        result = AbstractBean.FAIL;
      }

    }

    if (msg.length() > 0) {
      t.setFeedbackMsg(msg.toString());
    }
    return result;
  }

  private void afterImportAll(String tenant) {
    List<T> list = DATA.get();
    if (list != null && list.size() > 0) {
      this.doHandle(tenant, list);
    }
    DATA.remove();
    File file = ExcelExpContext.finish();
    String ossUrl = null;
    if (file != null) {
      String ossPath = "sop/tempOf7Day/imp_feedback/" + file.getName();
      try {
        bucket.put(ossPath, new FileInputStream(file));
        ossUrl = bucket.getUrl(ossPath, Bucket.CONTENT_TYPE_OF_WILDCARD);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      file.delete();
    }
    if (ossUrl != null) {
      // 反馈信息
      JobContext.getMergedJobDataMap().put(BJobProgress.JOB_RESULT, ossUrl);
    }
    this.afterAll(tenant);
    HEAD.remove();
  }

  /**
   * 批处理数量，小于1则按1处理
   * <p>
   * 注意：实际处理批次的数量可能会小于该值
   *
   * @return 数量
   */
  protected int batchHandleSize() {
    return 1;
  }

  // 是否生成反馈文件
  protected boolean generateFeedbackFile() {
    return true;
  }

  // 反馈文件是否包含成功的数据
  protected boolean includeSuccessGenerateFeedbackFile() {
    return false;
  }

  // 反馈文件是否包含忽略的数据
  protected boolean includeIgnoreGenerateFeedbackFile() {
    return true;
  }

  protected Class<T> getCls() {
    ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
    return (Class<T>) parameterizedType.getActualTypeArguments()[0];
  }

  private AnalysisEventListener<T> getListener(String tenant, AbstractExcelImpJob job) {
    return new AnalysisEventListener<T>() {

      @SneakyThrows
      @Override
      public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (isCustomHead()) {
          String aClassName = job.getDataMap().getString(AbstractExcelImpJob.BEAN_CLASS);
          Class<?> aClass = null;
          aClass = Class.forName(aClassName);
          Field[] fields = aClass.getDeclaredFields();
          try {
            List<List<String>> head = new ArrayList<>();
            // 动态修改ExcelProperty中的value值
            for (int i = 0; i < fields.length; i++) {
              Field field = fields[i];
              field.setAccessible(true);
              ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
              if (excelProperty == null) {
                continue;
              }
              IgnoreHead ignoreHead = field.getAnnotation(IgnoreHead.class);
              if (ignoreHead != null) {
                head.add(new ArrayList<>(Arrays.asList(excelProperty.value())));
                continue;
              }
              InvocationHandler h = Proxy.getInvocationHandler(excelProperty);
              Field hField = h.getClass().getDeclaredField("memberValues");
              hField.setAccessible(true);
              Map memberValues = (Map) hField.get(h);
              String[] values = new String[0];
              if (i < headMap.size()) {
                values = new String[] { headMap.get(i) };
                ArrayList<String> list = new ArrayList<>(Arrays.asList(values));
                head.add(list);
              }
              memberValues.put("value", values);
            }
            HEAD.set(head);
          } catch (Exception e) {
            log.error("设置导入模板列名错误:{}", e.getMessage());
          }
        }

        super.invokeHeadMap(headMap, context);
      }

      @Override
      public void invoke(T data, AnalysisContext context) {
        job.doImport(tenant, data);
      }

      @Override
      public void doAfterAllAnalysed(AnalysisContext context) {
        job.afterImportAll(tenant);
      }
    };
  }

  public File getTempXlsFile(String tenant) throws IOException {
    String defaultBaseDir = System.getProperty("java.io.tmpdir");
    String failName = getFileName() == null ? "" : getFileName();
    File file = new File(defaultBaseDir, tenant + File.separator + "sop" + File.separator + "temp" + File.separator
        + "imp_feedback" + File.separator + failName + FORMAT.format(new Date()) + new Random().nextInt(100000) + XLSX);
    if (!file.exists()) {
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
    return file;
  }

}
