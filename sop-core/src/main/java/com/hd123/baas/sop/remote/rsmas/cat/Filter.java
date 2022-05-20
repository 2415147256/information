package com.hd123.baas.sop.remote.rsmas.cat;

import java.io.Serializable;
import java.util.List;

import com.hd123.baas.sop.service.api.basedata.Sort;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author maodapeng
 * @Since
 */
public abstract class Filter implements Serializable {
  private static final long serialVersionUID = 4278270637116397962L;
  @ApiModelProperty("页号")
  private int page;
  @ApiModelProperty("页记录数")
  private int pageSize;
  @ApiModelProperty("排序字段集合")
  private List<Sort> sorts;
  @ApiModelProperty("级联查询信息")
  private String fetchParts;

  public Filter() {
  }

  public int getPage() {
    return this.page;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public List<Sort> getSorts() {
    return this.sorts;
  }

  public String getFetchParts() {
    return this.fetchParts;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public void setSorts(List<Sort> sorts) {
    this.sorts = sorts;
  }

  public void setFetchParts(String fetchParts) {
    this.fetchParts = fetchParts;
  }
}