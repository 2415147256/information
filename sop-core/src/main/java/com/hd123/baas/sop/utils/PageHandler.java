package com.hd123.baas.sop.utils;
/*
 版权所有(C)，上海海鼎信息工程股份有限公司，2019，所有权利保留。
 
 项目名：	com.hd123.dpos.mq
 文件名：	PageHandler.java
 模块说明：	
 修改历史：
 2019年06月04日 - wangdanhua - 创建。
 */

import java.util.List;

/**
 * @author wangdanhua
 **/
public interface PageHandler<T> {

  void processPage(int page, List<T> records) throws Exception;
}
