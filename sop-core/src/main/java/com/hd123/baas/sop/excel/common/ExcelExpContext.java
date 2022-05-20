package com.hd123.baas.sop.excel.common;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

/**
 * @author zhengzewang on 2020/11/24.
 */
@Getter
@Setter
public class ExcelExpContext {

  private static final ThreadLocal<Context> CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

  /**
   * 配置可以抽一个类
   */
  public static void init(File file, Class cls, String sheetName) {
    Context context = new Context();
    ExcelWriter writer = EasyExcel.write(file, cls).build();
    WriteSheet sheet = EasyExcel.writerSheet(sheetName).build();
    context.setFile(file);
    context.setWriter(writer);
    context.setSheet(sheet);
    CONTEXT_THREAD_LOCAL.set(context);
  }

  /**
   * 配置可以抽一个类
   */
  public static void initAndHead(File file, List<List<String>> head, String sheetName) {
    Context context = new Context();
    ExcelWriter writer = EasyExcel.write(file).head(head).build();
    WriteSheet sheet = EasyExcel.writerSheet(sheetName).build();
    context.setFile(file);
    context.setWriter(writer);
    context.setSheet(sheet);
    CONTEXT_THREAD_LOCAL.set(context);
  }

  public static void init(File file, List<List<String>> head, String sheetName) {
    Context context = new Context();
    ExcelWriterBuilder excelWriterBuilder = new ExcelWriterBuilder();
    excelWriterBuilder.head(head);
    excelWriterBuilder.file(file);
    WriteSheet sheet = EasyExcel.writerSheet(sheetName).build();
    context.setFile(file);
    context.setWriter(excelWriterBuilder.build());
    context.setSheet(sheet);
    CONTEXT_THREAD_LOCAL.set(context);
  }

  public static File finish() {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return null;
    }
    if (CONTEXT_THREAD_LOCAL.get().getWriter() != null) {
      CONTEXT_THREAD_LOCAL.get().getWriter().finish();
    }
    File file = CONTEXT_THREAD_LOCAL.get().getFile();
    return file;
  }

  public static void free() {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return;
    }
    File file = CONTEXT_THREAD_LOCAL.get().getFile();
    if (file != null) {
      file.delete();
    }
    CONTEXT_THREAD_LOCAL.remove();
  }

  public static ExcelWriter getWriter() {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return null;
    }
    return CONTEXT_THREAD_LOCAL.get().getWriter();
  }

  public static WriteSheet getSheet() {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return null;
    }
    return CONTEXT_THREAD_LOCAL.get().getSheet();
  }

  public static File getFile() {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return null;
    }
    return CONTEXT_THREAD_LOCAL.get().getFile();
  }

  public static File setFile(File file) {
    if (CONTEXT_THREAD_LOCAL.get() == null) {
      return null;
    }
    CONTEXT_THREAD_LOCAL.get().setFile(file);
    return CONTEXT_THREAD_LOCAL.get().getFile();
  }

  @Getter
  @Setter
  private static class Context {
    private ExcelWriter writer;
    private WriteSheet sheet;
    private File file;
  }

}
