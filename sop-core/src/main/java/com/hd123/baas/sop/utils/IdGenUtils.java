package com.hd123.baas.sop.utils;

import com.hd123.rumba.commons.biz.entity.IidWorker;

import java.util.Random;
import java.util.UUID;

/**
 * @author W.J.H.7
 * @since 1.0.0
 */
public class IdGenUtils {

  static int DATA_CENTER_ID = 0;
  static int MACHINE_ID = new Random().nextInt(31);
  static IidWorker iidWorker = new IidWorker(DATA_CENTER_ID, MACHINE_ID);

  /**
   * 返回原生的UUID
   */
  public static String buildRdUuid() {
    return UUID.randomUUID().toString();
  }

  public static long buildIid() {
    return iidWorker.next();
  }

  public static String buildIidAsString() {
    return String.valueOf(buildIid());
  }

  public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {
      System.out.println(buildIid());
    }
  }
}
