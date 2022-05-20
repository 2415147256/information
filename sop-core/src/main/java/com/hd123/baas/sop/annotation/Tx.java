package com.hd123.baas.sop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author W.J.H.7
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Transactional(value = "sop-service.txManager", propagation = Propagation.REQUIRED, rollbackFor = Throwable.class,isolation = Isolation.READ_COMMITTED)
public @interface Tx {

}
