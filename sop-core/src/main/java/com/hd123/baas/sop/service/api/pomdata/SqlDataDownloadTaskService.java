package com.hd123.baas.sop.service.api.pomdata;

import com.hd123.baas.sop.service.api.basedata.pos.Pos;

import java.util.Date;
import java.util.List;

public interface SqlDataDownloadTaskService {

  /**
   * 生成pos下发任务
   */
  void generalTasks(String tenant, String sourceId, Date createTime, List<Pos> posList, int state, boolean notifyPosNow) throws Exception;

  /**
   * 加载数据下发状态
   */
  void fetchDownloadState(String tenant, List<Pos> posList);

}
