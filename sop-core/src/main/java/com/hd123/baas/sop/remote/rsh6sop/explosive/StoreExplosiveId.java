/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： PExplosive.java
 * 模块说明：
 * 修改历史：
 * 2021年01月13日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.explosive;

/**
 * @author huangjunxian
 * @since 1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreExplosiveId implements Serializable {
  private String code;
}
