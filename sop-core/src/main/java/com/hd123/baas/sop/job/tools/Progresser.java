/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	Progresser.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.text.MessageFormat;
import java.util.Map;

import com.hd123.baas.sop.job.mapper.ProgressMapper;
import com.hd123.rumba.commons.i18n.DefaultStringValue;
import com.hd123.rumba.commons.i18n.Resources;
import com.hd123.rumba.commons.lang.Assert;

/**
 * 用于作业处理中，告知外部作业执行进度的工具类。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class Progresser {

  /** 默认最小值。 */
  public static final int DEFAULT_MIN = 0;
  /** 默认最大值。 */
  public static final int DEFAULT_MAX = 100;

  private static final int DEFAULT_MAX_SIZE_OF_MESSAGES = 100;
  private static final int MIN_OF_MAX_SIZE_OF_MESSAGES = 1;

  /**
   * @param dataMap
   *          禁止传入null。
   * @throws IllegalArgumentException
   */
  public Progresser(Map<String, Object> dataMap) throws IllegalArgumentException {
    Assert.assertArgumentNotNull(dataMap, "dataMap");
    this.dataMap = dataMap;
    impl = new Impl(null, 0f, new JobMessages());
    impl.updateDataMap();
  }

  private Map<String, Object> dataMap;
  private ProgressMapper progressMapper = new ProgressMapper();
  private Impl impl;
  private JobMessagesMapper messagesMapper = new JobMessagesMapper();
  private int maxSizeOfMessages = DEFAULT_MAX_SIZE_OF_MESSAGES;

  /**
   * 返回当前栈顶进度的最小值。
   */
  public int getMin() {
    return impl.getMin();
  }

  /**
   * 设置当前栈顶进度的最小值。<br>
   * 调用此方法将导致当前位置为初始位置，即等于最小值。
   * 
   * @param min
   * @throws IllegalArgumentException
   *           当指定的最小值大于当前最大值时抛出。
   */
  public void setMin(int min) throws IllegalArgumentException {
    impl.setMin(min);
  }

  /**
   * 返回当前栈顶进度的最大值。
   */
  public int getMax() {
    return impl.getMax();
  }

  /**
   * 设置当前栈顶进度的最大值。<br>
   * 调用此方法将导致当前位置为初始位置，即等于最小值。
   * 
   * @param max
   * @throws IllegalArgumentException
   *           当指定的最大值小于当前最小值时抛出。
   */
  public void setMax(int max) throws IllegalArgumentException {
    impl.setMax(max);
  }

  /**
   * 将当前栈顶进度重置为起始状态，即当前位置等于最小值。调用此方法不会改变当前最大值与最小值。
   */
  public void reset() {
    impl.reset();
  }

  /**
   * 将当前栈顶进度重置为起始状态，即当前位置等于最小值；并且指定最大值；同时设置最小值为0。
   * 
   * @param max
   *          最大值，必须是大于等于0的值。
   * @throws IllegalArgumentException
   *           当参数max取值小于0时抛出。
   */
  public void reset(int max) throws IllegalArgumentException {
    impl.reset(DEFAULT_MIN, max);
  }

  /**
   * 将当前栈顶重置为起始状态，即当前位置等于最小值；并且指定最大值与最小值。
   * 
   * @param min
   *          最小值。
   * @param max
   *          最大值。
   * @throws IllegalArgumentException
   *           当参数max小于参数min时抛出。
   */
  public void reset(int min, int max) throws IllegalArgumentException {
    impl.reset(min, max);
  }

  /**
   * 返回当前位置。
   */
  public float getPosition() {
    return impl.getPosition();
  }

  /**
   * 指定当前位置。
   * 
   * @param position
   *          当前位置。当传入小于当前最小值时，等价于传入最小值；当传入大于当前最大值时，等价于传入最大值。
   * @return 返回操作结束后的当前位置取值。
   */
  public float setPosition(float position) {
    impl.setPosition(position);
    return impl.getPosition();
  }

  /**
   * 返回百分比形式的当前位置，取值范围0-100。
   */
  public float getPercent() {
    return impl.getPercent();
  }

  /**
   * 以步长为1，使得当前栈顶进度的当前位置前进。
   * 
   * @return 返回操作结束后的当前位置
   */
  public float stepBy() {
    return stepBy(1f);
  }

  /**
   * 当前栈顶进度的当前位置前进指定步长。
   * 
   * @param step
   *          步长。
   * @return 返回操作结束后的当前位置。
   */
  public float stepBy(int step) {
    return stepBy((float) step);
  }

  /**
   * 当前栈顶进度的当前位置前进指定步长。
   * 
   * @param step
   *          步长。
   * @return 返回操作结束后的当前位置。
   */
  public float stepBy(float step) {
    float position = impl.getPosition();
    position += step;
    impl.setPosition(position);
    return impl.getPosition();
  }

  /**
   * 将当前栈顶进度的当前位置直接设置为结束状态。
   */
  public void complete() {
    impl.setPosition(impl.getMax());
  }

  /**
   * 返回当前栈顶进度表示当前正在做的事情的消息文本。
   */
  public String getMessage() {
    return impl.getMessage();
  }

  /**
   * 在当前栈顶进度中，添加表示当前正在做的事情的消息文本。
   * 
   * @param message
   */
  public void addMessage(String message) {
    impl.addMessage(message);
  }

  /**
   * 在进度栈中执行“压栈”，即创建一个新的相对于当前进度的子进度。
   * 
   * @param ratio
   *          新的子进度全部完成后相当于当前进度的步长。
   * @throws IllegalArgumentException
   *           当参数ratio小于0时抛出；<br>
   *           当参数ratio小于剩余步长（即当前进度的最大值与当前位置的差）时抛出。
   */
  public void push(float ratio) throws IllegalArgumentException {
    JobMessages messages = impl.messages.addMessages();
    impl = new Impl(impl, ratio, messages);
  }

  /**
   * 在进度栈中执行“出栈”。若当前已经位于进度栈的栈底时，操作将被忽略。
   */
  public void pop() {
    if (impl.getUpper() != null) {
      impl = impl.getUpper();
      impl.removeLower();
    }
  }

  /**
   * 返回当前进度是否已经是进度栈的栈底。也就是说如果返回true，意味着当前进度即为总进度。
   */
  public boolean isStackButtom() {
    return impl.getUpper() == null;
  }

  /**
   * 返回消息池最大尺寸。
   */
  public int getMaxSizeOfMessages() {
    return maxSizeOfMessages;
  }

  /**
   * @param maxSizeOfMessages
   *          传入所有小于1的值，等价于1。
   */
  public void setMaxSizeOfMessages(int maxSizeOfMessages) {
    if (maxSizeOfMessages < MIN_OF_MAX_SIZE_OF_MESSAGES) {
      this.maxSizeOfMessages = MIN_OF_MAX_SIZE_OF_MESSAGES;
    } else {
      this.maxSizeOfMessages = maxSizeOfMessages;
    }
  }

  private class Impl {

    private Impl upper = null;
    /** 最小值对应上级进度的位置。 */
    private float upperMinPosition = DEFAULT_MIN;
    /** 最大值对应上级进度的位置。 */
    private float upperMaxPosition = DEFAULT_MAX;
    private Impl lower = null;
    private int min = DEFAULT_MIN;
    private int max = DEFAULT_MAX;
    private float position = DEFAULT_MIN;
    private JobMessages messages;

    public Impl(Impl upper, float ratio, JobMessages messages) throws IllegalArgumentException {
      Assert.assertArgumentNotNull(messages, "message");
      this.messages = messages;

      this.upper = upper;
      if (upper == null) {
        return;
      }
      this.upper.lower = this;

      if (ratio < 0) {
        throw new IllegalArgumentException(MessageFormat.format(R.R.argumentMustGreaterThan(), "ratio", ratio, "0"));
      }
      upperMinPosition = upper.position;
      upperMaxPosition = upper.position + ratio;
      if (upperMaxPosition > upper.max) {
        throw new IllegalArgumentException(MessageFormat.format(R.R.ratioTooGreat(), upper.position, ratio, upper.max));
      }

      getTop().updateDataMap();
    }

    public Impl getUpper() {
      return upper;
    }

    /** 返回总进度。 */
    public Impl getTop() {
      if (upper == null) {
        return this;
      } else {
        return upper.getTop();
      }
    }

    public void removeLower() {
      if (lower != null) {
        lower.upper = null;
        lower = null;
        getTop().updateDataMap();
      }
    }

    public int getMin() {
      return min;
    }

    public void setMin(int min) throws IllegalArgumentException {
      if (min > max) {
        throw new IllegalArgumentException(MessageFormat.format(R.R.minMustLesserThanOrEqualsMax(), min, max));
      }
      this.min = min;
      setPosition(min);
    }

    public int getMax() {
      return max;
    }

    public void setMax(int max) throws IllegalArgumentException {
      if (min > max) {
        throw new IllegalArgumentException(MessageFormat.format(R.R.minMustLesserThanOrEqualsMax(), min, max));
      }
      this.max = max;
      setPosition(min);
    }

    public void reset() {
      setPosition(min);
    }

    public void reset(int min, int max) throws IllegalArgumentException {
      if (min > max) {
        throw new IllegalArgumentException(MessageFormat.format(R.R.minMustLesserThanOrEqualsMax(), min, max));
      }
      this.min = min;
      this.max = max;
      setPosition(min);
    }

    public float getPosition() {
      return position;
    }

    public void setPosition(float position) {
      if (position < min) {
        this.position = min;
      } else if (position > max) {
        this.position = max;
      } else {
        this.position = position;
      }

      if (upper != null) {
        // 计算上级进度的position
        float upperPosition = upper.position;
        if (this.position == max) {
          upperPosition = upperMaxPosition;
        } else if (this.position == min) {
          upperPosition = upperMinPosition;
        } else {
          float upperOffset = (upperMaxPosition - upperMinPosition) * (this.position - min) / (max - min);
          upperPosition = upperMinPosition + upperOffset;
        }
        upper.setPosition(upperPosition);

      } else {
        // 更新数据映射表。
        getTop().updateDataMap();
      }
    }

    public float getPercent() {
      return (position - min) * 100f / (max - min);
    }

    public String getMessage() {
      return messages.getLastMessageText();
    }

    public void addMessage(String message) {
      messages.addMessage(message);

      getTop().updateDataMap();
    }

    /** 更新作业数据映射表。 */
    public void updateDataMap() {
      Progress progress = toProgress();
      progressMapper.writeTo(progress, dataMap);

      messagesMapper.writeTo(messages, dataMap);

    }

    private Progress toProgress() {
      Progress progress = new Progress();
      progress.setMaximum(max - min);
      progress.setPosition(position - min);

      if (lower != null) {
        progress.setSubprogress(lower.toProgress());
      }
      return progress;
    }

  }

  public static interface R {
    public static final R R = Resources.create(R.class);

    @DefaultStringValue("参数{0}({1})取值必须大于{2}。")
    String argumentMustGreaterThan();

    @DefaultStringValue("从当前位置({0})计算子进度步长({1})超过父进度最大值({2})。")
    String ratioTooGreat();

    @DefaultStringValue("最小值({0})必须小于等于最大值({1})。")
    String minMustLesserThanOrEqualsMax();
  }

}
