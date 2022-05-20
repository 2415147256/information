package com.hd123.baas.sop.job.bean.basedata;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hd123.baas.sop.service.api.basedata.pos.Pos;
import com.hd123.baas.sop.service.api.basedata.pos.PosFilter;
import com.hd123.baas.sop.service.api.basedata.pos.PosService;
import com.hd123.baas.sop.evcall.AbstractEvCallExecutor;
import com.hd123.baas.sop.service.api.pomdata.SqlDataDownloadTaskService;
import com.hd123.baas.sop.utils.BaasJSONUtil;
import com.hd123.rumba.evcall.EvCallExecutionContext;
import com.hd123.rumba.evcall.EvCallManager;
import com.hd123.spms.commons.util.CollectionUtil;
import com.qianfan123.baas.common.BaasException;

import lombok.extern.slf4j.Slf4j;

/**
 * pos资料下发
 *
 * @author cRazy
 */
@Slf4j
@Component
public class PosPromDataDownloadTaskEvCallExecutor extends AbstractEvCallExecutor<PosPromDataDownloadTaskMsg> {
    public static final String POS_PROM_DATA_DOWNLOAD_TASK = PosPromDataDownloadTaskEvCallExecutor.class.getSimpleName();

    @Autowired
    private EvCallManager evCallManager;
    @Autowired
    private PosService posService;
    @Autowired
    private SqlDataDownloadTaskService sqlDataDownloadTaskService;

    @PostConstruct
    public void init() {
        evCallManager.addExecutor(this, POS_PROM_DATA_DOWNLOAD_TASK);
    }

    @Override
    protected void doExecute(PosPromDataDownloadTaskMsg message, EvCallExecutionContext context) throws Exception {
        String tenant = message.getTenant();
        String orgId = message.getOrgId();
        log.info("PosPromDataDownloadTaskGeneralJob.generalTasks:{}", tenant);
        PosFilter filter = new PosFilter();
        filter.setOrgIdEq(orgId);
        int state = 0;
        List<Pos> posList = posService.query(tenant, filter).getRecords();
        if (posList.isEmpty()) {
            return;
        }
        for (List<Pos> list : CollectionUtil.sizeBy(posList, 100)) {
            // 定时作业的下发任务，默认不主动发通知给pos，等POS开机自己轮询。
            sqlDataDownloadTaskService.generalTasks(tenant, String.valueOf(context.getRequestId()), new Date(), list, state, false);
        }
    }

    @Override
    protected PosPromDataDownloadTaskMsg decodeMessage(String msg) throws BaasException {
        log.info("收到PosPromDataDownloadTaskMsg:{}", msg);
        return BaasJSONUtil.safeToObject(msg, PosPromDataDownloadTaskMsg.class);
    }
}
