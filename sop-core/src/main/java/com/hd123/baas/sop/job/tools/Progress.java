/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	Progress.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.io.Serializable;
import java.text.MessageFormat;

import com.hd123.rumba.commons.i18n.DefaultStringValue;
import com.hd123.rumba.commons.i18n.Resources;
import com.hd123.rumba.commons.json.JsonObject;
import com.hd123.rumba.commons.json.JsonSyntaxException;

/**
 * 进度信息对象。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class Progress implements Serializable {

  private static final long serialVersionUID = 2607221666380008878L;

  private static final int DEFAULT_MAXIMUM = 100;
  private static final float DEFAULT_POSITION = 0f;

  private static final String ATTR_MAXIMUM = "maximum";
  private static final String ATTR_POSITION = "position";
  private static final String ATTR_SUBPROGRESS = "subprogress";

  /**
   * 将来自{@link #toString()}返回的字符串，反序列化为对象。
   * 
   * @param jsonStr
   *          传入null将导致返回null。
   */
  public static Progress valueOf(String jsonStr) {
    if (jsonStr == null) {
      return null;
    }
    JsonObject json = new JsonObject(jsonStr);
    return fromJson(json);
  }

  static Progress fromJson(JsonObject json) throws JsonSyntaxException {
    if (json == null) {
      return null;
    }
    Progress progress = new Progress();
    if (json.has(ATTR_MAXIMUM)) {
      int maximum = json.getInt(ATTR_MAXIMUM);
      progress.setMaximum(maximum);
    }
    if (json.has(ATTR_POSITION)) {
      float position = Double.valueOf(json.getDouble(ATTR_POSITION)).floatValue();
      progress.setPosition(position);
    }
    if (json.has(ATTR_SUBPROGRESS)) {
      progress.setSubprogress(fromJson(json.getJsonObject(ATTR_SUBPROGRESS)));
    }
    return progress;
  }

  private final int minimum = 0;
  private int maximum = DEFAULT_MAXIMUM;
  private float position = DEFAULT_POSITION;
  private Progress subprogress;

  /**
   * 取得最小值，永远返回0。
   */
  public int getMinimum() {
    return minimum;
  }

  /**
   * 取得最大值。
   */
  public int getMaximum() {
    return maximum;
  }

  /**
   * 设置最大值，要求必须大于0。
   * 
   * @param maximum
   * @throws IllegalArgumentException
   *           当参数maximum取值小于或等于0时抛出。
   */
  public void setMaximum(int maximum) throws IllegalArgumentException {
    if (maximum <= 0f) {
      throw new IllegalArgumentException(
          MessageFormat.format(R.R.maximumMustGreaterThanZero(), String.valueOf(maximum)));
    }
    this.maximum = maximum;
  }

  /**
   * 取得当前位置。
   */
  public float getPosition() {
    return position;
  }

  /**
   * 设置当前位置。
   * 
   * @param position
   *          传入小于0的值等价于0；传入大于{@link #getMaximum()}的值等价于{@link #getMaximum()}
   *          的返回值。
   */
  public void setPosition(float position) {
    this.position = position;
    if (this.position < 0f) {
      this.position = 0f;
    } else if (this.position > maximum) {
      this.position = maximum;
    }
  }

  /**
   * 取得百分比形式的当前位置，取值范围0-100之间。
   */
  public float getPercent() {
    return position * 100 / maximum;
  }

  /**
   * 取得子进度，null表示没有子进度。
   */
  public Progress getSubprogress() {
    return subprogress;
  }

  /**
   * 设置子进度，null表示没有子进度。
   * 
   * @param subprogress
   */
  public void setSubprogress(Progress subprogress) {
    this.subprogress = subprogress;
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.put(ATTR_MAXIMUM, Integer.valueOf(maximum));
    json.put(ATTR_POSITION, Float.valueOf(position));

    if (subprogress != null) {
      json.put(ATTR_SUBPROGRESS, subprogress.toJson());
    }
    return json;
  }

  @Override
  public String toString() {
    return toJson().toString("  ");
  }

  public static interface R {
    public static final R R = Resources.create(R.class);

    @DefaultStringValue("属性maximum取值（{0}）必须大于0。")
    String maximumMustGreaterThanZero();
  }
}
