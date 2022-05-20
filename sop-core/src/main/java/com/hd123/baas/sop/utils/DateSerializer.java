/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	swallows-web
 * 文件名：	DateSerializer.java
 * 模块说明：	
 * 修改历史：
 * 2018年5月8日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author huangjunxian
 *
 */
public class DateSerializer extends com.fasterxml.jackson.databind.ser.std.DateSerializer {

  public DateSerializer() {
    super(false, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
  }

  public DateSerializer(Boolean useTimestamp,DateFormat customFormat) {
    super(useTimestamp, customFormat);
  }

}
