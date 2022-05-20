package com.hd123.baas.sop.remote.rsmas.cat;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @Author maodapeng
 * @Since
 */
public class MasPageResponse<T> {
  private int echoCode;
  private String echoMessage;
  public boolean success;
  private int page;
  private int pageSize;
  private int pageCount;
  private int total;
  private T data;

  public MasPageResponse() {
  }

  public boolean isSuccess() {
    return this.echoCode == 0;
  }

  public static MasPageResponse fail(String echoMessage) {
    MasPageResponse r = new MasPageResponse();
    r.setSuccess(false);
    r.setEchoCode(500);
    r.setEchoMessage(echoMessage);
    return r;
  }

  public static MasPageResponse fail(int echoCode, String echoMessage) {
    if (echoCode == 0) {
      throw new IllegalArgumentException("echoCode不能为0");
    } else {
      MasPageResponse r = new MasPageResponse();
      r.setSuccess(false);
      r.setEchoCode(echoCode);
      r.setEchoMessage(echoMessage);
      return r;
    }
  }

  public MasPageResponse<T> ok(int page, int pageSize, int pageCount, int total, T data) {
    MasPageResponse<T> r = new MasPageResponse();
    r.setPage(page);
    r.setPageSize(pageSize);
    r.setPageCount(pageCount);
    r.setTotal(total);
    r.setData(data);
    return r;
  }

  @XmlTransient
  public void inject(int page, int pageSize, int total) {
    this.setPage(page);
    this.setPageSize(pageSize);
    this.setTotal(total);
    int pageCount = pageSize == 0 ? 0 : total / pageSize;
    if (pageCount * pageSize < total) {
      ++pageCount;
    }

    this.setPageCount(pageCount);
  }

  public int getEchoCode() {
    return this.echoCode;
  }

  public String getEchoMessage() {
    return this.echoMessage;
  }

  public int getPage() {
    return this.page;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public int getPageCount() {
    return this.pageCount;
  }

  public int getTotal() {
    return this.total;
  }

  public T getData() {
    return this.data;
  }

  public void setEchoCode(int echoCode) {
    this.echoCode = echoCode;
  }

  public void setEchoMessage(String echoMessage) {
    this.echoMessage = echoMessage;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void setData(T data) {
    this.data = data;
  }
}
