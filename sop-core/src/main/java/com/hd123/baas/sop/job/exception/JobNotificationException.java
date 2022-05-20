/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobNotificationException.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.exception;

import java.text.MessageFormat;

/**
 * 意味着通知作业结果过程中发生异常。
 * 
 * @author huzexiong
 * @since 1.0
 *
 */
public class JobNotificationException extends Exception {

  private static final long serialVersionUID = 7467640232417523369L;

  public JobNotificationException() {
    // Do Nothing
  }

  public JobNotificationException(String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments));
  }

  public JobNotificationException(Throwable t) {
    super(t);
  }

  public JobNotificationException(Throwable t, String pattern, Object... arguments) {
    super(MessageFormat.format(pattern, arguments), t);
  }

}
