package com.hd123.baas.sop.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果
 * @author yu lilin
 * @since 1.0
 */
@Setter
@Getter
public class ImportResult<T> {

    /**
     * 成功导入的列表信息
     */
    private List<T> successList = new ArrayList<T>();
    /**
     * 失败导入的列表信息
     */
    private List<T> failList = new ArrayList<T>();
    /**
     * 导入结果文件下载链接
     */
    private String backUrl;
    /**
     * 导入成功的个数
     */
    private int successCount;
    /**
     * 导入失败的个数
     */
    private int failCount;
    /**
     * 导入忽略的个数
     */
    private int ignoreCount;
}
