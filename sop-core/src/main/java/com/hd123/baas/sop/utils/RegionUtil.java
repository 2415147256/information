/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2021，所有权利保留。
 * <p>
 * 项目名： sop-parent
 * 文件名： RegionUtil.java
 * 模块说明：
 * 修改历史：
 * 2021年05月26日 - XLT - 创建。
 */
package
  com.hd123.baas.sop.utils;

public class RegionUtil {

  // 获取两个火星坐标之间的距离
  public static double calcDistance(double lng1, double lat1, double lng2, double lat2) {
    double EARTH_RADIUS = 6378.137;
    double radLat1 = rad(lat1);
    double radLat2 = rad(lat2);
    double a = radLat1 - radLat2;
    double b = rad(lng1) - rad(lng2);
    double l = Math.pow(Math.sin(a / 2), 2)
      + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2);
    double s = 2 * Math.atan2(Math.sqrt(l), Math.sqrt(1 - l));
    s = s * EARTH_RADIUS;
    s = Math.round(s * 1000);
    return s;
  }

  // 将角度转化为长度
  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }
}