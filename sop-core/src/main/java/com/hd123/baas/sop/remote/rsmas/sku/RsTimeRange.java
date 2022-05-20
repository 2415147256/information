/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2009，所有权利保留。
 * <p>
 * 项目名：	e21-h5-core
 * 文件名：	DateRange.java
 * 模块说明：
 * 修改历史：
 * Mar 28, 2009 - lxm - 创建。
 */
package com.hd123.baas.sop.remote.rsmas.sku;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 提供时间区间数据以及相关运算，其中描述了从开始到结束的闭-开区间。
 * <p>
 *
 * 如果指定的结束时间早于开始时间，将会自动将结束时间与开始时间取值交换。
 *
 * @author lxm
 *
 */
public class RsTimeRange implements Serializable, Comparable {

  private static final long serialVersionUID = 300100L;

  public static final Date EARLIEST_DATE = new GregorianCalendar(1800, 0, 1, 0, 0, 0).getTime();
  public static final Date LATEST_DATE = new GregorianCalendar(2100, 0, 1, 0, 0, 0).getTime();

  /**
   * 创建对象实例。
   */
  public RsTimeRange() {
    super();
    start = LATEST_DATE;
    finish = EARLIEST_DATE;
  }

  /**
   * 创建对象实例。
   *
   * @param start
   *          指定开始日期。
   * @param finish
   *          指定结束日期。
   */
  public RsTimeRange(Date start, Date finish) {
    super();
    setStart(start);
    setFinish(finish);
  }

  private Date start;
  private Date finish;

  /**
   * 取得开始时间。对于空区间，将始终返回MAX_DATE。
   */
  public Date getStart() {
    return start;
  }

  /**
   * 设置开始时间。
   *
   * @param start
   *          指定开始时间，传入null，表示不限制，等价于MIN_DATE。
   */
  public void setStart(Date start) {
    checkToExchange();
    if (start == null)
      this.start = EARLIEST_DATE;
    else
      this.start = new Date(start.getTime());
    checkToExchange();
  }

  /**
   * 取得结束时间。对于空区间，将始终返回MIN_DATE。
   */
  public Date getFinish() {
    return finish;
  }

  /**
   * 设置结束时间。
   *
   * @param finish
   *          指定结束时间，传入null，表示不限制，等价于MAX_DATE。
   */
  public void setFinish(Date finish) {
    checkToExchange();
    if (finish == null)
      this.finish = LATEST_DATE;
    else
      this.finish = new Date(finish.getTime());
    checkToExchange();
  }

  private void checkToExchange() {
    if (start == null)
      return;
    if (finish == null)
      return;
    if (start.compareTo(finish) <= 0)
      return;
    Date d = start;
    start = finish;
    finish = d;
  }


  public RsTimeRange clone() {
    return new RsTimeRange(start, finish);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((finish == null) ? 0 : finish.hashCode());
    result = prime * result + ((start == null) ? 0 : start.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final RsTimeRange other = (RsTimeRange) obj;
    if (finish == null) {
      if (other.finish != null)
        return false;
    } else if (!finish.equals(other.finish))
      return false;
    if (start == null) {
      if (other.start != null)
        return false;
    } else if (!start.equals(other.start))
      return false;
    return true;
  }

  public int compareTo(Object o) {
    if (o == null)
      return 1;
    RsTimeRange another = (RsTimeRange) o;
    int result = start.compareTo(another.getStart());
    if (result != 0)
      return result;
    result = finish.compareTo(another.getFinish());
    return result;
  }

  /**
   * 判断指定时间是否位于当前时间区间内。
   *
   * @param time
   *          指定的时间，允许null，将导致始终返回false。
   */
  public boolean in(Date time) {
    if (time == null)
      return false;
    if (start.compareTo(time) >= 0)
      return false;
    if (finish.compareTo(time) < 0)
      return false;
    return true;
  }

  /**
   * 取得两个时间区间的交集。
   *
   * @param another
   *          指定时间区间。如果传入null，将导致返回空区间。
   * @return 如果没有交集，则返回空区间。
   */
  public RsTimeRange multiply(RsTimeRange another) {
    if (another == null)
      return new RsTimeRange();

    if (isOverlap(another) == false)
      return new RsTimeRange();

    RsTimeRange result = new RsTimeRange();

    Date newStart = start;
    if (newStart.compareTo(another.getStart()) < 0)
      newStart = another.getStart();
    result.setStart(newStart);

    Date newFinish = finish;
    if (newFinish.compareTo(another.getFinish()) > 0)
      newFinish = another.getFinish();
    result.setFinish(newFinish);

    return result;
  }

  /**
   * 取得属于当前区间，且不属于指定区间的部分。
   *
   * @param another
   *          指定时间区间。允许传入null，等价于空区间。
   * @return 返回结果最多可能出现两个区间，这时数组长度最多为2，并且两者更早的时间区间放在前面。也可能返回一个空区间，这时数组长度为1，
   *         且其中唯一的时间区间为空区间。
   */
  public RsTimeRange[] subtract(RsTimeRange another) {
    if (another == null)
      return new RsTimeRange[]{
              this};

    if (isOverlap(another) == false)
      return new RsTimeRange[]{
              this};

    int cpStart = another.getStart().compareTo(start);
    int cpFinish = another.getFinish().compareTo(finish);

    if (cpStart <= 0) {
      if (cpFinish >= 0)
        return new RsTimeRange[]{
                new RsTimeRange()};

      else {
        Date newStart = another.getFinish();
        return new RsTimeRange[]{
                new RsTimeRange(newStart, finish)};
      }

    } else { // cpStart > 0
      if (cpFinish >= 0) {
        Date newFinish = another.getStart();
        return new RsTimeRange[]{
                new RsTimeRange(start, newFinish)};

      } else {
        Date newFinish = another.getStart();
        Date newStart = another.getFinish();
        return new RsTimeRange[]{
                new RsTimeRange(start, newFinish), new RsTimeRange(newStart, finish)};
      }
    }
  }

  /**
   * 区间相加运算。如果有可能将返回一个合并后的时间区间，否则将返回两个区间对象。
   *
   * @param another
   *          指定时间区间。允许传入null，等价于空区间。
   */
  public RsTimeRange[] union(RsTimeRange another) {
    if (another == null)
      return new RsTimeRange[]{
              this};


    if (isOverlap(another) == false) {
      RsTimeRange[] result = new RsTimeRange[2];
      if (start.compareTo(another.getStart()) < 0) {
        result[0] = this;
        result[1] = another;
      } else {
        result[0] = another;
        result[1] = this;
      }
      return result;
    }

    Date newStart = start;
    if (another.getStart().compareTo(newStart) < 0)
      newStart = another.getStart();
    Date newFinish = finish;
    if (another.getFinish().compareTo(newFinish) > 0)
      newFinish = another.getFinish();
    return new RsTimeRange[]{
            new RsTimeRange(newStart, newFinish)};
  }

  /**
   * 判断是否存在重叠部分。
   *
   * @param another
   *          指定时间区间。允许传入null，等价于空区间。
   */
  public boolean isOverlap(RsTimeRange another) {
    if (another == null)
      return false;

    if (another.getFinish().compareTo(start) <= 0)
      return false;
    if (another.getStart().compareTo(finish) >= 0)
      return false;
    return true;
  }

  /**
   * 判断是否相邻。
   *
   * @param another
   *          指定时间区间。允许传入null，等价于空区间。
   */
  public boolean beside(RsTimeRange another) {
    if (another == null)
      return false;

    if (start.equals(another.getFinish()))
      return true;
    if (finish.equals(another.getStart()))
      return true;
    return false;
  }

}
