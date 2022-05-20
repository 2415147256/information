/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * <p>
 * 项目名：	sop-service 文件名：	OssSignature.java 模块说明： 修改历史： 2019/4/30 - cRazy - 创建。
 */
package com.hd123.baas.sop.configuration.oss;

import java.util.Map;

/**
 * @author cRazy
 */
public interface OssSignature {
  Map<String, Object> signature(String path) throws Exception;

}
