/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： sop
 * 文件名： FilterParams.java
 * 模块说明：
 * 修改历史：
 * 2020年11月19日 - zhuangwenting - 创建。
 */
package com.hd123.baas.sop.service.api;

import com.qianfan123.baas.common.http.FilterParam;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhuangwenting
 * @since 1.0
 */
@Data
public class FilterParams {
  private List<FilterParam> filters = new ArrayList();
}
