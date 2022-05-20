package com.hd123.baas.sop.utils;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 
 项目名：	com.hd123.dpos.utils.pageiterator
 文件名：	BatchIterator.java
 模块说明：	
 修改历史：
 2019年07月04日 - wangdanhua - 创建。
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.hd123.rumba.commons.lang.Assert;

/**
 * @author wangdanhua
 **/
public class BatchIterator<T> implements PageIterator<T> {
  private int pageSize = 1000;

  private List<T> entities;

  public BatchIterator(List<T> entities) {
    Assert.notNull(entities);

    this.entities = entities;
  }

  @Override
  public void iteratorPage(PageHandler<T> handler) throws Exception {
    if (CollectionUtils.isEmpty(entities)) {
      return;
    }
    for (int i = 0; i < getPageCount(); i++) {
      handler.processPage(i, getPage(i));
    }
  }

  /**
   * @param page
   *          页号，从0开始计数，若超过最大页号，返回空集合
   */
  public List<T> getPage(int page) {
    Assert.greaterOrEquals(page, 0);

    if (CollectionUtils.isEmpty(entities)) {
      return Collections.emptyList();
    }
    int pageCount = getPageCount();
    if (page >= pageCount) {
      return Collections.emptyList();
    }

    int size = entities.size();

    int begin = page * pageSize;
    int end = (page + 1) * pageSize;
    if (end > size) {
      end = size;
    }

    return new ArrayList<>(entities.subList(begin, end));
  }

  @Override
  public void setPageSize(int pageSize) {
    Assert.greater(pageSize, 0);
    this.pageSize = pageSize;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  public int getPageCount() {
    int size = entities.size();
    int page = size / pageSize;
    if (size % pageSize != 0) {
      page += 1;
    }
    return page;
  }

  public static void main(String[] args) throws Exception {
    BatchIterator<Integer> iterator = new BatchIterator<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13));
    iterator.setPageSize(10);
    iterator.iteratorPage((page, records) -> {
      // System.out.println(page);
      System.out.println(records);
    });

  }
}
