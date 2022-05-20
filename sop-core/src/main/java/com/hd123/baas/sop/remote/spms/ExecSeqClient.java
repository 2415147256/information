package com.hd123.baas.sop.remote.spms;

import com.hd123.h5.pom.execseq.ExecSeqGroup;
import com.hd123.h5.pom.execseq.ExecSeqGroupType;
import com.hd123.spms.commons.bean.ListWrapper;
import com.hd123.spms.commons.bean.Response;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by fangzhipeng on 2017/4/6.
 */
@FeignClient(name = "spms-sync-server", configuration = SpmsFeignConfig.class)
public interface ExecSeqClient {

  @ApiOperation(value = "开始全量同步指定租户促销数据", notes = "启动后，会清空该租户对应来源的全部促销单，以待同步；全量更新期间，同步不会调用促销引擎缓存更新")
  @RequestMapping(value = "/{tenant_id}/promotionexecseqservice/allGroups", method = RequestMethod.GET)
  ListWrapper<ExecSeqGroup> getAllGroups(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @ApiParam("数据来源") @RequestParam("type") ExecSeqGroupType type);

  @ApiOperation(value = "指定租户促销数据全量同步结束", notes = "并发送促销引擎环境全量更新")
  @RequestMapping(value = "/{tenant_id}/promotionexecseqservice/saveGroups", method = RequestMethod.POST)
  Response saveGroups(
          @ApiParam("租户标识") @PathVariable("tenant_id") String tenantId,
          @ApiParam("数据来源") @RequestParam("type") ExecSeqGroupType type,
          @RequestBody ListWrapper<ExecSeqGroup> groups);

}
