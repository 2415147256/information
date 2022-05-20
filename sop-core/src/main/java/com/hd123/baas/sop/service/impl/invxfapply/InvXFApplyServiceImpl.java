/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2020，所有权利保留。
 * <p>
 * 项目名：	sos_idea
 * 文件名：	StoreInvXFReqServiceImpl.java
 * 模块说明：
 * 修改历史：
 * 2020/11/3 - Leo - 创建。
 */

package com.hd123.baas.sop.service.impl.invxfapply;

import com.hd123.baas.sop.service.api.invxfapply.InvXFApply;
import com.hd123.baas.sop.service.api.invxfapply.InvXFApplyAuditor;
import com.hd123.baas.sop.service.api.invxfapply.InvXFApplyRejection;
import com.hd123.baas.sop.service.api.invxfapply.InvXFApplyService;
import com.hd123.baas.sop.service.api.invxfapply.QuerySort;
import com.hd123.baas.sop.service.api.invxfapply.RsToInvXFApply;
import com.hd123.baas.sop.common.OrgConstants;
import com.hd123.baas.sop.configuration.FeignClientMgr;
import com.hd123.baas.sop.remote.rsh6sop.RsH6SOPClient;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApply;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyAuditor;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyQueryFilter;
import com.hd123.baas.sop.remote.rsh6sop.invxfapply.RsInvXFApplyRejection;
import com.hd123.baas.sop.utils.DateUtil;
import com.hd123.rumba.commons.biz.entity.OperateInfo;
import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.lang.Assert;
import com.hd123.rumba.commons.lang.StringUtil;
import com.hd123.rumba.commons.util.converter.ConverterUtil;
import com.qianfan123.baas.common.BaasException;
import com.qianfan123.baas.common.http.FilterParam;
import com.qianfan123.baas.common.http.QueryRequest;
import com.qianfan123.baas.common.http.SortParam;
import com.qianfan123.baas.common.http2.BaasResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Leo
 */
@Service
public class InvXFApplyServiceImpl implements InvXFApplyService {
	@Autowired
	private FeignClientMgr feignClientMgr;

	@Override
    public QueryResult<InvXFApply> query(String tenant, List<String> orgIds, QueryRequest request)
					throws ParseException, BaasException {
		Assert.hasText(tenant);
		Assert.notNull(request);

		RsInvXFApplyQueryFilter queryFilter = new RsInvXFApplyQueryFilter();
		for (FilterParam filterParam : request.getFilters()) {
			String property = filterParam.getProperty();
			Object value = filterParam.getValue();
			if (RsInvXFApplyQueryFilter.CONDITION_NUM_EQ.equals(property)) {
				queryFilter.setNumEq(StringUtil.toString(value));
			} else if (RsInvXFApplyQueryFilter.CONDITION_NUM_LIKE.equals(property)) {
				queryFilter.setNumLike(StringUtil.toString(value));
			} else if (RsInvXFApplyQueryFilter.CONDITION_NUM_STARTSWITH.equals(property)) {
				queryFilter.setNumStartsWith(StringUtil.toString(value));
			} else if (RsInvXFApplyQueryFilter.CONDITION_FILDATE_GTE.equals(property)) {
				queryFilter.setSubmitTimeGreaterOrEq(
								DateUtil.toDate(StringUtil.toString(value)));
			} else if (RsInvXFApplyQueryFilter.CONDITION_FILDATE_LTE.equals(property)) {
				queryFilter.setSubmitTimeLessOrEq(
                DateUtil.toDate(StringUtil.toString(value)));
			} else if (RsInvXFApplyQueryFilter.CONDITION_FROMSTORECODE_EQ.equals(property)) {
				queryFilter.setFromStoreCodeEq(StringUtil.toString(value));
			} else if (RsInvXFApplyQueryFilter.CONDITION_TOSTORECODE_EQ.equals(property)) {
				queryFilter.setToStoreCodeEq(StringUtil.toString(value));
			} else if (RsInvXFApplyQueryFilter.CONDITION_STAT_EQ.equals(property)) {
				queryFilter.setStatEq(StringUtil.toInteger(StringUtil.toString(value)));
			} else if (RsInvXFApplyQueryFilter.CONDITION_ORG_UUID_IN.equals(property)) {
				// 需要进行组织区分
				queryFilter.setOrgUuidIn(orgIds.stream().filter(i -> !i.equals(OrgConstants.DEFAULT_MAS_ORG_ID)).collect(Collectors.toList()));
			} else if (RsInvXFApplyQueryFilter.CONDITION_STAT_IN.equals(property)) {
				if (value instanceof List) {
					List<Integer> stats = new ArrayList<>();
					for (int i = 0; i < ((List) value).size(); i++) {
						Object item = ((List) value).get(i);
						stats.add(StringUtil.toInteger(StringUtil.toString(item)));
					}
					queryFilter.setStatIn(stats);
				}
			} else {
				throw new IllegalArgumentException(MessageFormat.format("查询接口暂时不支持查询条件{0}", property));
			}
		}

		List<QuerySort> sorts = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(request.getSorters())) {
			for (SortParam sortParam : request.getSorters()) {
				String property = sortParam.getProperty();
				String direction = sortParam.getDirection();
				sorts.add(new QuerySort().setField(property)
								.setAsc(!RsInvXFApplyQueryFilter.DESC.equalsIgnoreCase(direction)));
			}
		}

		queryFilter.setSorts(sorts);

		Integer page = new Double(
						Math.floor(new Double(request.getStart()) / new Double(request.getLimit()))).intValue();
		queryFilter.setPage(page);
		queryFilter.setPageSize(request.getLimit());

		BaasResponse<List<RsInvXFApply>> response = getClient(tenant)
						.invXFQuery(tenant, queryFilter);
		checkResponse(response);

		QueryResult<InvXFApply> result = new QueryResult<>();
		result.setRecords(ConverterUtil.convert(response.getData(), RsToInvXFApply.getInstance()));
		result.setMore(response.getMore());
		result.setRecordCount(response.getTotal());
		return result;
	}

	@Override
	public InvXFApply get(String tenant, String num) throws BaasException {
		Assert.hasText(tenant);
		Assert.hasText(num);

		BaasResponse<RsInvXFApply> response = getClient(tenant).invXFGet(tenant, num);
		checkResponse(response);
		return RsToInvXFApply.getInstance().convert(response.getData());
	}

	@Override
	public void reject(String tenant, InvXFApplyRejection rejection, OperateInfo operateInfo)
					throws BaasException {
		Assert.hasText(tenant);
		Assert.notNull(rejection);

		RsInvXFApplyRejection rsRejection = new RsInvXFApplyRejection();
		rsRejection.setNum(rejection.getNum());
		rsRejection.setRejectReason(rejection.getRejectReason());
		rsRejection.setAuditTime(operateInfo.getTime());
		rsRejection.setAuditorId(operateInfo.getOperator().getId());
		rsRejection.setAuditorName(operateInfo.getOperator().getFullName());

		BaasResponse<Void> response = getClient(tenant).invXFReject(tenant, rsRejection);
		checkResponse(response);
	}

	@Override
	public void audit(String tenant, InvXFApplyAuditor auditor, OperateInfo operateInfo)
					throws BaasException {
		Assert.hasText(tenant);
		Assert.notNull(auditor);

		RsInvXFApplyAuditor rsAuditor = new RsInvXFApplyAuditor();
		rsAuditor.setNum(auditor.getNum());
		rsAuditor.setAuditTime(operateInfo.getTime());
		rsAuditor.setAuditorId(operateInfo.getOperator().getId());
		rsAuditor.setAuditorName(operateInfo.getOperator().getFullName());

		BaasResponse<Void> response = getClient(tenant).invXFAudit(tenant, rsAuditor);
		checkResponse(response);
	}

	private RsH6SOPClient getClient(String tenant) throws BaasException {
		return feignClientMgr.getClient(tenant, null, RsH6SOPClient.class);
	}

	public void checkResponse(BaasResponse response) throws IllegalArgumentException {
		if (response.isSuccess()) {
			return;
		}
		throw new IllegalArgumentException(response.getMsg());
	}
}
