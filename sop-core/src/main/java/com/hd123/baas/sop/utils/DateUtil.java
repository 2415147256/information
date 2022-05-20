package com.hd123.baas.sop.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

  public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
  public static final SimpleDateFormat SDF = new SimpleDateFormat(PATTERN);

  public static String toDateStr(Date date) {
    if (date == null) {
      return null;
    }
    return DateFormatUtils.format(date, PATTERN);
  }

  /**
   * @param field
   *     : one of followings: Calendar.YEAR, MONTH, DATE, HOUR, MINUTE, SECOND, WEEK_OF_YEAR
   */
  public static Date truncate(Date date, int field) {
    if (date == null)
      return null;
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    switch (field) {
      case Calendar.YEAR:
        c.clear(Calendar.MONTH);
      case Calendar.MONTH:
        c.set(Calendar.DAY_OF_MONTH, 1);
      case Calendar.DATE:
        c.set(Calendar.HOUR_OF_DAY, 0);
      case Calendar.HOUR:
        c.clear(Calendar.MINUTE);
      case Calendar.MINUTE:
        c.clear(Calendar.SECOND);
      case Calendar.SECOND:
        c.clear(Calendar.MILLISECOND);
        break;
      case Calendar.WEEK_OF_MONTH:
      case Calendar.WEEK_OF_YEAR:
        c.clear(Calendar.MILLISECOND);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MINUTE);
        c.set(Calendar.HOUR_OF_DAY, 0);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        c.add(Calendar.DATE, -(dayOfWeek - 1));
        break;
      default:
        throw new IllegalArgumentException("无法识别字段" + field);
    }
    return c.getTime();
  }

  /**
   * 取得日期的时间部分。
   */
  public static Date getTimePart(Date date) {
    Calendar c1 = Calendar.getInstance();
    c1.setTime(date);
    c1.clear(Calendar.YEAR);
    c1.clear(Calendar.MONTH);
    c1.set(Calendar.DAY_OF_MONTH, 1);
    return c1.getTime();
  }

  /**
   * 将指定日期部分与时间部分相加。
   *
   * @param date
   *     指定日期部分。
   * @param time
   *     指定时间部分。
   */
  public static Date addDateAndTime(Date date, Date time) {
    return new Date(date.getTime() + time.getTime() + Calendar.getInstance().get(Calendar.ZONE_OFFSET));
  }

  /**
   * @param field
   *     : Calendar.FIELD
   */
  public static Date add(Date date, int field, int amount) {
    if (date == null)
      return null;
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(field, amount);
    return c.getTime();
  }

  /**
   * 取出日期的一部分。
   *
   * @param field
   *     Calendar.FIELD
   */
  public static int get(Date date, int field) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(field);
  }

  public static int offset = Calendar.getInstance().getTimeZone().getOffset(new Date().getTime());

  public static Date sql2util(ResultSet rs, String field) throws SQLException {
    return new Date(rs.getDate(field).getTime() + rs.getTime(field).getTime() + offset);
  }

  /**
   * 获取指定时间所在周的开始时间。即：周一的00:00:00.000
   */
  public static Date getCurrentWeekStartTime(Date date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int week = calendar.get(Calendar.DAY_OF_WEEK);
    int difference = 0;
    if (week == Calendar.SUNDAY) {
      difference = -6;
    } else if (week == Calendar.MONDAY) {
      return date;
    } else if (week == Calendar.TUESDAY) {
      difference = -1;
    } else if (week == Calendar.WEDNESDAY) {
      difference = -2;
    } else if (week == Calendar.THURSDAY) {
      difference = -3;
    } else if (week == Calendar.FRIDAY) {
      difference = -4;
    } else if (week == Calendar.SATURDAY) {
      difference = -5;
    }
    calendar.add(Calendar.DAY_OF_MONTH, difference);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTime();
  }

  /**
   * 获取指定时间所在周的结束时间。即：周日的23:59:59.999
   */
  public static Date getCurrentWeekEndTime(Date date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int week = calendar.get(Calendar.DAY_OF_WEEK);
    int difference = 0;
    if (week == Calendar.SUNDAY) {
      return date;
    } else if (week == Calendar.MONDAY) {
      difference = 6;
    } else if (week == Calendar.TUESDAY) {
      difference = 5;
    } else if (week == Calendar.WEDNESDAY) {
      difference = 4;
    } else if (week == Calendar.THURSDAY) {
      difference = 3;
    } else if (week == Calendar.FRIDAY) {
      difference = 2;
    } else if (week == Calendar.SATURDAY) {
      difference = 1;
    }
    calendar.add(Calendar.DAY_OF_MONTH, difference);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    return calendar.getTime();
  }

  public static boolean isBirthday(Date birthDate) {
    if (birthDate == null) {
      return false;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date now = new Date();
    String birthDay = sdf.format(birthDate);
    String nowDay = sdf.format(now);
    return birthDay.equals(nowDay);
  }

  public static Date prase(String dataString) throws ParseException {
    if (dataString == null) {
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.parse(dataString);
  }

  public static Date toDate(String o) {
    try {
      return StringUtils.isBlank(o) ? null
          : org.apache.commons.lang3.time.DateUtils.parseDate(o.replaceAll("Z$", "+0000"), new String[] {
          "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss",
          "yyyy-MM-dd" });
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private static final long SECOND = 1000;
  private static final long MINUTE = 60 * SECOND;
  private static final long HOUR = 60 * MINUTE;
}
