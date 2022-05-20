package com.hd123.baas.sop.service.api.feedback;

/**
 * 质量反馈状态
 * @author yu lilin
 * @since 1.0
 */
public enum FeedbackState {
    /**
     * 未提交
     */
    initial,
    /**
     * 已提交/未处理
     */
    submitted,
    /**
     * 已确认
     */
    checked,
    /**
     * 已处理
     */
    audited,
    /**
     * 已生成费用
     */
    feeGenerated
}
