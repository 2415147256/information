/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名： workspace_mkh
 * 文件名： ApplicationContextUtils.java
 * 模块说明：
 * 修改历史：
 * 2020年11月27日 - huangjunxian - 创建。
 */
package com.hd123.baas.sop.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author huangjunxian
 * @since 1.0
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

	private static ApplicationContextUtils instance = null;

	public static synchronized ApplicationContextUtils getInstance() {
		if (instance == null) {
			instance = new ApplicationContextUtils();
		}
		return instance;
	}

	private ApplicationContextUtils() {

	}

	private ApplicationContext appCtx;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		getInstance().appCtx = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return getInstance().appCtx;
	}

	public static <T> T getBean(String name) {
		return (T) getInstance().appCtx.getBean(name);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return getInstance().appCtx.getBean(requiredType);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return getInstance().appCtx.getBean(name, requiredType);
	}
}