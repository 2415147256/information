/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2015，所有权利保留。
 * 
 * 项目名：	mas-commons-biz
 * 文件名：	RsMasPageResponse.java
  * 模块说明：	
 * 修改历史：

 * 2019年8月20日 - lsz - 创建。
 */
package com.hd123.baas.sop.remote.rsmas;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author lsz
 */
@Getter
@Setter
public class RsMasPageResponse<T> {
  /**
   * 结果码，默认为0表示成功
   */
  private int echoCode;
  /**
   * 结果信息
   */
  private String echoMessage;
  /**
   * 是否成功
   */
  public boolean success;
  /**
   * 页号
   */
  private int page;
  /**
   * 记录数
   */
  private int pageSize;
  /**
   * 当前页
   */
  private int pageCount;
  /**
   * 总数
   */
  private int total;
  /**
   * 响应数据
   */
  private T data;

  public boolean isSuccess() {
    return echoCode == 0;
  }

  /**
   * 返回失败响应对象，echoCode设置为500
   * 
   */
  public static RsMasPageResponse fail(String echoMessage) {
    RsMasPageResponse r = new RsMasPageResponse();
    r.setSuccess(false);
    r.setEchoCode(500);
    r.setEchoMessage(echoMessage);
    return r;
  }

  public static RsMasPageResponse fail(int echoCode, String echoMessage) {
    if (echoCode == 0) {
      throw new IllegalArgumentException("echoCode不能为0");
    }
    RsMasPageResponse r = new RsMasPageResponse();
    r.setSuccess(false);
    r.setEchoCode(echoCode);
    r.setEchoMessage(echoMessage);
    return r;
  }

  public RsMasPageResponse<T> ok(int page, int pageSize, int pageCount, int total, T data) {
    RsMasPageResponse<T> r = new RsMasPageResponse<T>();
    r.setPage(page);
    r.setPageSize(pageSize);
    r.setPageCount(pageCount);
    r.setTotal(total);
    r.setData(data);
    return r;
  }

  @XmlTransient
  public void inject(int page, int pageSize, int total) {
    setPage(page);
    setPageSize(pageSize);
    setTotal(total);
    int pageCount = (pageSize == 0 ? 0 : (total / pageSize));
    if (pageCount * pageSize < total) {
      pageCount++;
    }
    setPageCount(pageCount);
  }
}
