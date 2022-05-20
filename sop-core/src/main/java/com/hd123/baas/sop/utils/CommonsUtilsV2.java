package com.hd123.baas.sop.utils;

import lombok.extern.slf4j.Slf4j;


/**
 * @author W.J.H.7
 * @date 2022-03-11
 */
@Slf4j
public class CommonsUtilsV2 {

  public static void outTs(String title, Long sts) {
    outTs("-", title, sts, System.currentTimeMillis());
  }

  public static void outTs(String tranId, String title, Long sts) {
    outTs(tranId, title, sts, System.currentTimeMillis());
  }

  public static void outTs(String tranId, String title, Long sts, Long ets) {
    log.info(">>>><<<< " + "[" + tranId + "]" + title + "，执行耗时={}s", (ets - sts) / 1000.0);
  }
}
