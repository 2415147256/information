package com.hd123.baas.sop.service.impl.approval;

import com.hd123.baas.config.core.BaasConfigClient;
import com.hd123.baas.sop.config.WorkWxConfig;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.workwx.WorkWxClient;
import com.hd123.baas.sop.remote.workwx.apply.ApplyInfo;
import com.hd123.baas.sop.remote.workwx.apply.SpRecord;
import com.hd123.baas.sop.remote.workwx.apply.SpRecordDetail;
import com.hd123.baas.sop.remote.workwx.request.ApprovalReq;
import com.hd123.baas.sop.remote.workwx.request.WorkWxApprovalApply;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyDetailResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkApplyResponse;
import com.hd123.baas.sop.remote.workwx.response.WorkWxUserResponse;
import com.hd123.baas.sop.service.api.approval.ApprovalService;
import com.hd123.baas.sop.service.api.approval.ApprovalState;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.qianfan123.baas.common.BaasException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author W.J.H.7
 */
@Slf4j
public abstract class AApprovalByWorkWxService<S> implements ApprovalService<S, WorkWxApproval> {

  @Autowired
  private FeignClientMgr feignClientMgr;
  @Autowired
  private BaasConfigClient configClient;

  /**
   * 源对象构建提交审批申请的对象
   *
   * @param source
   *     源对象
   * @return 审批申请的对象
   */
  protected abstract WorkWxApprovalApply build(S source) throws Exception;

  @Override
  public String submit(String tenant, S source) throws Exception {
    WorkWxApprovalApply applyReq = build(source);
    if (applyReq == null) {
      throw new BaasException("无法构造企业微信审批数据");
    }
    WorkWxClient client = feignClientMgr.getClient(tenant, null, WorkWxClient.class);
    WorkApplyResponse applyRep = client.apply(tenant,  applyReq);
    if (!applyRep.success()) {
      throw new BaasException("提交企业微信审批错误{}", applyRep.getErrMsg());
    }
    return applyRep.getSpNo();
  }

  @Override
  public WorkWxApproval getByNo(String tenant, String spNo) throws Exception {
    Assert.hasText(tenant);
    Assert.hasText(spNo);

    WorkWxClient client = feignClientMgr.getClient(tenant, null, WorkWxClient.class);
    WorkApplyDetailResponse approvalDetailRsp = client.getApprovalDetail(tenant, new ApprovalReq(spNo));

    if (!approvalDetailRsp.success()) {
      throw new BaasException("获取审批详情错误{}", approvalDetailRsp.getErrMsg());
    }

    if (approvalDetailRsp.getInfo() == null) {
      return null;
    }

    Map<String, WorkWxUserResponse> userResponseMap = fetchUserInfo(tenant, approvalDetailRsp.getInfo().getSpRecords(), client);


    return covertTo(approvalDetailRsp.getInfo(), userResponseMap);
  }

  private Map<String, WorkWxUserResponse> fetchUserInfo(String tenant, List<SpRecord> spRecords, WorkWxClient client) {
    Map<String, WorkWxUserResponse> map = new HashMap<>();
    if (CollectionUtils.isEmpty(spRecords)) {
      return map;

    }
    for (SpRecord spRecord : spRecords) {
      for (SpRecordDetail detail : spRecord.getDetails()) {
        String userId = detail.getApprover().getUserId();
        if (!StringUtil.isNullOrBlank(userId)) {
          WorkWxUserResponse workWxUserResponse = client.getUser(tenant, userId);
          if (workWxUserResponse.success()) {
            map.put(workWxUserResponse.getUserId(), workWxUserResponse);
          }
        }
      }

    }
    return map;
  }

  @Override
  public boolean checkToken(String tenant, String token) {
    Assert.notNull(tenant, "租户ID");
    Assert.notNull(token, "token");
    WorkWxConfig config = configClient.getConfig(tenant, WorkWxConfig.class);
    if (config != null) {
      return token.equalsIgnoreCase(config.getApprovalToken());
    }
    return false;
  }

  private WorkWxApproval covertTo(ApplyInfo info, Map<String, WorkWxUserResponse> userMap) {
    WorkWxApproval workWxApproval = new WorkWxApproval();
    workWxApproval.setSpNo(info.getSpNo());
    workWxApproval.setState(getState(info.getSpStatus()));
    List<WorkWxApprovalRecord> records = new ArrayList<>();
    for (SpRecord spRecord : info.getSpRecords()) {
      WorkWxApprovalRecord record = new WorkWxApprovalRecord();
      record.setStatus(spRecord.getSpStatus());
      record.setApproverattr(spRecord.getApproverAttr());
      List<RecordDetail> details = new ArrayList<>();
      for (SpRecordDetail detail : spRecord.getDetails()) {
        RecordDetail recordDetail = new RecordDetail();
        RecordDetail.RecordDetailApprover approver = new RecordDetail.RecordDetailApprover();
        String userId = detail.getApprover().getUserId();
        approver.setUserId(userId);
        if (userMap.containsKey(userId)) {
          approver.setUserName(userMap.get(userId).getName());
          approver.setUserMobil(userMap.get(userId).getMobile());
        }

        recordDetail.setApprover(approver);
        recordDetail.setSpeech(detail.getSpeech());
        recordDetail.setStatus(detail.getSpStatus());
        recordDetail.setTime(detail.getSpTime());
        recordDetail.setMediaIds(detail.getMediaIds());
        details.add(recordDetail);

      }
      record.setDetails(details);
      records.add(record);
    }
    workWxApproval.setRecords(records);
    return workWxApproval;
  }

  private ApprovalState getState(int spStatus) {
    if (spStatus == 1) {
      return ApprovalState.PROCESSING;
    } else if (spStatus == 2) {
      return ApprovalState.APPROVED;
    } else if (spStatus == 3) {
      return ApprovalState.REFUSED;
    }
    return ApprovalState.PROCESSING;
  }
}
