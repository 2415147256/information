/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： SOPPromotionBillState.java
 * 模块说明：
 * 修改历史：
 * 2020年11月30日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.remote.rsh6sop.prom;

/**
 * @author huangjunxian
 * @since 1.0
 */
public enum RSPromotionBillState {
	//未审核
	initial,
	//已审核
	audited,
	//已作废
	aborted;
}
