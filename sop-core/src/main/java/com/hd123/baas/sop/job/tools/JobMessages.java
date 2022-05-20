/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2016，所有权利保留。
 * 
 * 项目名：	dpos-service
 * 文件名：	JobMessages.java
 * 模块说明：	
 * 修改历史：
 * 2016年9月18日 - huzexiong - 创建。
 */
package com.hd123.baas.sop.job.tools;

import java.text.MessageFormat;
import java.util.*;

import com.hd123.rumba.commons.i18n.DefaultStringValue;
import com.hd123.rumba.commons.i18n.Resources;
import com.hd123.rumba.commons.json.JsonArray;
import com.hd123.rumba.commons.json.JsonObject;

/**
 * 作业消息集合对象。
 * 
 * @author huzexiong
 * @since 1.0
 * 
 */
public class JobMessages extends JobMessageBase {

  private static final long serialVersionUID = -944919308479363020L;

  private static final int FIRST_LINE = 0;
  private static final int DEFAULT_MAX_SIZE = 100;

  private static final String ATTR_MESSAGES = "messages";
  private static final String ATTR_MAX_SIZE = "maxSize";
  private static final String ATTR_LINE = "line";

  /**
   * 将来自{@link #toString()}返回的字符串，反序列化为对象。
   * 
   * @param jsonStr
   *          传入null将导致返回null。
   */
  public static JobMessages valueOf(String jsonStr) {
    if (jsonStr == null) {
      return null;
    }
    JsonObject json = new JsonObject(jsonStr);
    return fromJson(json);
  }

  static JobMessages fromJson(JsonObject json) {
    if (json == null) {
      return null;
    }

    JobMessages target = new JobMessages();

    if (json.has(ATTR_MAX_SIZE)) {
      target.setMaxSize(json.getInt(ATTR_MAX_SIZE));
    }

    if (json.has(ATTR_MESSAGES)) {
      JsonArray messagesJson = json.getJsonArray(ATTR_MESSAGES);
      for (int index = 0; index < messagesJson.length(); index++) {
        JsonObject messageJson = messagesJson.getJsonObject(index);
        int line = messageJson.getInt(ATTR_LINE);
        JobMessageBase message = null;
        if (messageJson.has(ATTR_MESSAGES)) {
          message = fromJson(messageJson);
        } else {
          message = JobMessage.fromJson(messageJson);
        }
        if (message != null) {
          target.messages.put(Integer.valueOf(line), message);
        }
      }
    }

    return target;
  }

  /** key为消息的行号，value为对应的消息对象。 */
  private Map<Integer, JobMessageBase> messages = new HashMap<Integer, JobMessageBase>();
  private int maxSize = DEFAULT_MAX_SIZE;
  private int nextLine = -1;

  /**
   * 添加消息。
   * 
   * @param text
   *          消息文本，传入null等价于传入空字符串。
   */
  public void addMessage(String text) {
    String ripeText = text == null ? "" : text;
    JobMessage message = new JobMessage();
    message.setText(ripeText);
    message.setUpper(this);
    messages.put(Integer.valueOf(nextLine()), message);

    shrink();
  }

  /**
   * 添加下级消息集合对象。
   * 
   * @return 返回创建的消息集合对象。
   */
  public JobMessages addMessages() {
    JobMessages sub = new JobMessages();
    sub.setUpper(this);
    messages.put(Integer.valueOf(nextLine()), sub);
    return sub;
  }

  /**
   * 返回行号列表，其中为所有包含消息对象对应的行号，并按照行号升序排序。
   */
  public List<Integer> getLines() {
    List<Integer> list = new ArrayList<Integer>();
    list.addAll(messages.keySet());
    Collections.sort(list);
    return list;
  }

  /**
   * 返回最大层级数。
   */
  public int getLevels() {
    int levels = 1;
    for (JobMessageBase message : messages.values()) {
      if (message instanceof JobMessages) {
        int subLevels = ((JobMessages) message).getLevels();
        levels = Math.max(levels, subLevels + 1);
      }
    }
    return levels;
  }

  /**
   * 取得指定行号对应的消息对象。
   * 
   * @param line
   *          行号。
   * @return 返回消息对象，若找不到将返回null。
   */
  public JobMessageBase getMessage(int line) {
    return messages.get(Integer.valueOf(line));
  }

  /**
   * 根据索引对象取得消息对象。
   * 
   * @param index
   *          索引对象，传入null将导致返回null。
   * @return 返回消息对象，返回null意味着没有找到。
   */
  public JobMessageBase getMessage(Index index) {
    if (index == null || index.isEmpty()) {
      return null;
    }
    Integer line = index.get(0);
    JobMessageBase message = messages.get(line);
    if (message == null) {
      return null;
    }
    if (index.size() == 1) {
      return message;
    }
    if (message instanceof JobMessages == false) {
      return null;
    }
    Index subIndex = index.clone();
    subIndex.remove(0);
    return ((JobMessages) message).getMessage(subIndex);
  }

  /**
   * 取得指定行号的消息文本。
   * 
   * @param line
   *          行号。
   * @return 返回消息文本。返回null意味着指定行号不存在，或者不是文本消息。
   */
  public String getMessageText(int line) {
    JobMessageBase message = getMessage(line);
    if (message != null && message instanceof JobMessage) {
      return ((JobMessage) message).getText();
    } else {
      return null;
    }
  }

  /**
   * 根据索引对象取得消息文本。
   * 
   * @param index
   *          索引对象，传入null将导致返回null。
   * @return 返回消息文本，返回null意味着没有找到，或者索引指向的不是消息文本。
   */
  public String getMessageText(Index index) {
    JobMessageBase message = getMessage(index);
    if (message != null && message instanceof JobMessage) {
      return ((JobMessage) message).getText();
    } else {
      return null;
    }
  }

  /**
   * 取得最后（最新）被加入的消息对象。通常仅当为空时返回null。
   */
  public JobMessageBase getLastMessage() {
    if (messages.isEmpty()) {
      return null;
    }
    int maxLine = FIRST_LINE;
    for (Integer line : messages.keySet()) {
      maxLine = Math.max(line.intValue(), maxLine);
    }
    return messages.get(Integer.valueOf(maxLine));
  }

  /**
   * 取得指定层级最后（最新）被加入的消息对象。
   * 
   * @param level
   *          层级，0表示当前对象的直接下级消息对象，传入负数，将导致始终返回null。
   * @return 返回作业消息对象，null意味着没有找到。
   */
  public JobMessageBase getLastMessage(int level) {
    if (level < 0) {
      return null;
    }

    JobMessageBase message = this;
    for (int lvl = 0; lvl < level; lvl++) {
      if (message instanceof JobMessages == false) {
        return null;
      }
      message = ((JobMessages) message).getLastMessage();
      if (message == null) {
        return null;
      }
    }
    return message;
  }

  /**
   * 返回最后一条消息文本。此方法仅在直接下级消息范围内搜索。返回null意味着没有找到。
   */
  public String getLastMessageText() {
    List<Integer> lines = new ArrayList<Integer>();
    lines.addAll(messages.keySet());
    Collections.sort(lines);

    for (int index = lines.size() - 1; index >= 0; index--) {
      Integer line = lines.get(index);
      JobMessageBase message = messages.get(line);
      if (message instanceof JobMessage) {
        return ((JobMessage) message).getText();
      }
    }
    return null;
  }

  /**
   * 返回指定层级最后一条消息文本。
   * 
   * @param level
   *          层级，0表示当前对象的直接下级消息对象，传入负数，将导致始终返回null。
   * @return 消息文本，返回null意味着找不到。
   */
  public String getLastMessageText(int level) {
    if (level < 0) {
      return null;
    }

    JobMessageBase message = this;
    for (int lvl = 0; lvl < level; lvl++) {
      if (message instanceof JobMessages == false) {
        return null;
      }
      message = ((JobMessages) message).getLastMessage();
      if (message == null) {
        return null;
      }
    }

    return ((JobMessages) message).getLastMessageText();
  }

  /**
   * 返回指定层级范围内的最后一条消息文本。
   * 
   * @param beginLevel
   *          起始层级索引。
   * @param endLevel
   *          终止层级索引。若小于beginLevel，则将导致返回null。
   * @return 消息文本，返回null意味着找不到。
   */
  public String getLastMessageText(int beginLevel, int endLevel) {
    if (endLevel < beginLevel || endLevel < 0) {
      return null;
    }

    int start = Math.min(endLevel, getLevels() - 1);
    for (int level = start; level >= beginLevel; level--) {
      JobMessageBase m = getLastMessage(level);
      if (m == null) {
        continue;
      }
      if (m instanceof JobMessage) {
        return ((JobMessage) m).getText();
      }
      String text = getLastMessageText(level);
      if (text != null) {
        return text;
      }
    }
    return null;
  }

  /**
   * 返回所有包含消息以及子消息的总计数。
   */
  public int size() {
    int size = 0;
    for (JobMessageBase message : messages.values()) {
      if (message instanceof JobMessage) {
        size++;
      } else if (message instanceof JobMessages) {
        size += ((JobMessages) message).size();
      } else {
        assert false;
      }
    }
    return size;
  }

  /**
   * 返回最大限制尺寸，超过此限度将自动删除老的消息。此项设置仅对顶级消息对象有效。
   */
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * 设置最大限制尺寸，超过此限度将自动删除老的消息。此项设置仅对顶级消息对象有效。
   * 
   * @param maxSize
   *          取值范围大于0。
   * @throws IllegalArgumentException
   *           当参数maxSize取值小于或等于0时抛出。
   */
  public void setMaxSize(int maxSize) throws IllegalArgumentException {
    if (maxSize <= 0) {
      throw new IllegalArgumentException(MessageFormat.format(R.R.illegalMaxSize(), maxSize));
    }
    this.maxSize = maxSize;

    if (getUpper() == null) {
      shrink();
    }
  }

  /**
   * 取得指向第一条消息的索引对象。
   * 
   * @return 返回指向消息文本的索引对象，返回null意味着不存在。
   */
  public Index firstText() {
    Index ti = new Index();
    List<Integer> lines = getLines();
    for (int index = 0; index < lines.size(); index++) {
      Integer line = lines.get(index);
      JobMessageBase message = messages.get(line);

      if (message instanceof JobMessage) {
        ti.add(line);
        return ti;
      }

      assert message instanceof JobMessages;
      Index sti = ((JobMessages) message).firstText();
      if (sti != null) {
        ti.add(line);
        ti.addAll(sti);
        return ti;
      }
    }
    return null;
  }

  /**
   * 取得指向下一条消息文本的索引对象。
   * 
   * @param base
   *          作为运算基准的索引对象。传入null意味着取得指向第一条消息文本的索引对象。
   * @return 返回指向消息文本的索引对象，返回null意味着超过了最后一条消息文本。
   */
  public Index nextText(Index base) {
    if (base == null || base.isEmpty()) {
      return firstText();
    }
    // 如果当前消息空，意味着超出最后一条。
    if (messages.isEmpty()) {
      return null;
    }

    Integer baseLine = base.get(0);
    List<Integer> lines = getLines();

    // 如果baseLine大于最大索引，意味着超出最后一条。
    Integer lastLine = lines.get(lines.size() - 1);
    if (baseLine.compareTo(lastLine) > 0) {
      return null;
    }

    // 如果还有下级索引，先递归调用下级nextIndex()方法。如果下级返回不空，意味着找到。
    if (base.size() > 1) {
      Index subBase = base.clone();
      subBase.remove(0);
      JobMessageBase message = messages.get(baseLine);
      if (message instanceof JobMessages) {
        Index subNext = ((JobMessages) message).nextText(subBase);
        if (subNext != null) {
          Index next = new Index();
          next.add(baseLine);
          next.addAll(subNext);
          return next;
        }
      }
    }

    // 搜索所有baseLine之后的行号，逐个搜索，直至找到下一个消息文本，或者遍历结束找不到返回null。
    // 注：如果baseLine不在lines中，只可能小于所有行号，因此从第一个行号开始搜索。
    int index = lines.indexOf(baseLine) + 1;
    while (index < lines.size()) {
      Integer line = lines.get(index);
      JobMessageBase message = messages.get(line);

      if (message instanceof JobMessages) {
        Index subNext = ((JobMessages) message).firstText();
        if (subNext != null) {
          Index next = new Index();
          next.add(line);
          next.addAll(subNext);
          return next;
        }

      } else if (message instanceof JobMessage) {
        Index next = new Index();
        next.add(line);
        return next;

      } else {
        assert false;
      }
      index++;
    }
    return null;
  }

  /**
   * 取得指向最后一条消息文本的索引对象。
   * 
   * @return 返回指向消息文本的索引对象，返回null意味着不存在。
   */
  public Index lastText() {
    Index ti = new Index();
    List<Integer> lines = getLines();
    for (int index = lines.size() - 1; index >= 0; index++) {
      Integer line = lines.get(index);
      JobMessageBase message = messages.get(line);

      if (message instanceof JobMessage) {
        ti.add(line);
        return ti;
      }

      assert message instanceof JobMessages;
      Index sti = ((JobMessages) message).lastText();
      if (sti != null) {
        ti.add(line);
        ti.addAll(sti);
        return ti;
      }
    }
    return null;
  }

  /**
   * 取得指向上一条消息文本的索引对象。
   * 
   * @param base
   *          作为运算基准的索引对象。传入null意味着取得指向最后一条消息文本的索引对象。
   * @return 返回指向消息文本的索引对象，返回null意味着超过了第一条消息文本。
   */
  public Index previousText(Index base) {
    if (base == null || base.isEmpty()) {
      return lastText();
    }
    // 如果当前消息空，意味着超出最后一条。
    if (messages.isEmpty()) {
      return null;
    }

    Integer baseLine = base.get(0);
    List<Integer> lines = getLines();

    // 如果baseLine小于最小索引，意味着超出第一条。
    Integer firstLine = lines.get(0);
    if (baseLine.compareTo(firstLine) < 0) {
      return null;
    }

    // 如果还有下级索引，先递归调用下级previousIndex()方法。如果下级返回不空，意味着找到。
    if (base.size() > 1) {
      Index subBase = base.clone();
      subBase.remove(0);
      JobMessageBase message = messages.get(baseLine);
      if (message instanceof JobMessages) {
        Index subNext = ((JobMessages) message).previousText(subBase);
        if (subNext != null) {
          Index next = new Index();
          next.add(baseLine);
          next.addAll(subNext);
          return next;
        }
      }
    }

    // 搜索所有baseLine之前的行号，逐个搜索，直至找到下一个消息文本，或者遍历结束找不到返回null。
    // 注：如果baseLine不在lines中，只可能大于所有行号，因此从最后一个行号开始搜索。
    int index = lines.indexOf(baseLine) - 1;
    if (lines.contains(baseLine) == false) {
      index = lines.size() - 1;
    }
    while (index >= 0) {
      Integer line = lines.get(index);
      JobMessageBase message = messages.get(line);

      if (message instanceof JobMessages) {
        Index subNext = ((JobMessages) message).lastText();
        if (subNext != null) {
          Index next = new Index();
          next.add(line);
          next.addAll(subNext);
          return next;
        }

      } else if (message instanceof JobMessage) {
        Index next = new Index();
        next.add(line);
        return next;

      } else {
        assert false;
      }
      index--;
    }
    return null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + maxSize;
    result = prime * result + ((messages == null) ? 0 : messages.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    JobMessages other = (JobMessages) obj;
    if (maxSize != other.maxSize)
      return false;
    if (messages == null) {
      if (other.messages != null)
        return false;
    } else if (!messages.equals(other.messages))
      return false;
    return true;
  }

  @Override
  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (getUpper() == null) {
      json.put(ATTR_MAX_SIZE, Integer.valueOf(maxSize));
    }

    JsonArray messagesJson = new JsonArray();
    for (Map.Entry<Integer, JobMessageBase> entry : messages.entrySet()) {
      Integer line = entry.getKey();
      JobMessageBase message = entry.getValue();
      JsonObject messageJson = message.toJson();
      messageJson.put(ATTR_LINE, line);
      messagesJson.add(messageJson);
    }
    json.put(ATTR_MESSAGES, messagesJson);

    return json;
  }

  @Override
  public String toString() {
    return toJson().toString("  ");
  }

  private int nextLine() {
    if (nextLine < 0) {
      if (messages.isEmpty()) {
        nextLine = FIRST_LINE;
      } else {
        List<Integer> lines = getLines();
        nextLine = lines.get(lines.size() - 1).intValue() + 1;
      }
    }
    int result = nextLine;
    nextLine++;
    return result;
  }

  private void shrink() {
    // 调用顶级对象的shrink()方法。
    if (getUpper() != null) {
      JobMessageBase top = getTop();
      assert top instanceof JobMessages;
      ((JobMessages) top).shrink();
      return;
    }

    int size = size();
    if (size > maxSize) {
      poll(size - maxSize);
    }
  }

  /**
   * 按顺序删除指定数量的消息。
   * 
   * @param expectCount
   *          期望删除的消息数量。
   * @return 返回剩余未能删除的消息数量。
   */
  private int poll(int expectCount) {
    List<Integer> lines = new ArrayList<Integer>();
    lines.addAll(messages.keySet());
    Collections.sort(lines);

    int count = 0;
    for (Integer line : lines) {
      JobMessageBase message = messages.get(line);
      if (message instanceof JobMessage) {
        messages.remove(line);
        count++;

      } else if (message instanceof JobMessages) {
        int remainCount = ((JobMessages) message).poll(expectCount - count);
        if (((JobMessages) message).messages.isEmpty()) {
          messages.remove(line);
        }
        count = expectCount - remainCount;

      } else {
        assert false;
      }

      if (count == expectCount) {
        return 0;
      }
    }

    return expectCount - count;
  }

  /**
   * 用于在消息集合中定位一条作业消息的索引对象。
   * 
   * @author Li Ximing
   * @since 1.0
   * 
   */
  public static class Index extends ArrayList<Integer> {

    private static final long serialVersionUID = 8330200140615927483L;

    @Override
    public Index clone() {
      Index other = new Index();
      other.addAll(this);
      return other;
    }

  }

  public static interface R {
    public static final R R = Resources.create(R.class);

    @DefaultStringValue("参数“maxSize”必须大于0。（{0}）")
    String illegalMaxSize();
  }
}
