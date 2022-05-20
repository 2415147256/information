package com.hd123.baas.sop.excel.common;

import java.lang.annotation.*;

/**
 * excel导入导出时忽略替换表头
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface IgnoreHead {
}
