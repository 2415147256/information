package com.hd123.baas.sop.service.impl.taskgroup;

import com.hd123.baas.sop.service.api.taskgroup.TaskGroup;
import com.hd123.baas.sop.service.api.taskgroup.TaskGroupData;
import com.hd123.rumba.commons.util.converter.ConversionException;
import com.hd123.rumba.commons.util.converter.Converter;

/**
 * @author guyahui
 * @date 2021/4/29 14:37
 */
public class TaskGroupToTaskGroupData implements Converter<TaskGroup, TaskGroupData> {

  private static TaskGroupToTaskGroupData instance = new TaskGroupToTaskGroupData();

  public static TaskGroupToTaskGroupData getInstance() {
    return instance;
  }

  @Override
  public TaskGroupData convert(TaskGroup taskGroup) throws ConversionException {
    if (taskGroup == null) {
      return null;
    }

    TaskGroupData taskGroupData = new TaskGroupData();
    taskGroupData.setTenant(taskGroup.getTenant());
    taskGroupData.setName(taskGroup.getName());
    taskGroupData.setCreated(taskGroup.getCreateInfo().getTime());
    taskGroupData.setState(taskGroup.getState());
    taskGroupData.setType(taskGroup.getType().name());
    taskGroupData.setUuid(taskGroup.getUuid());
    taskGroupData.setCode(taskGroup.getCode());
    taskGroupData.setCreateInfo(taskGroup.getCreateInfo());
    taskGroupData.setLastModifyInfo(taskGroup.getLastModifyInfo());
    return taskGroupData;
  }
}
