package com.lzh.servicejob.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.servicejob.entity.Job;
import com.lzh.servicejob.entity.JobDeliveryRecord;
import com.lzh.servicejob.entity.vo.JobDeliveryRecordVo;
import com.lzh.servicejob.service.JobDeliveryRecordService;
import com.lzh.servicejob.service.JobService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-04-06
 */
@RestController
@RequestMapping("/service-job/job-delivery-record")
@CrossOrigin
public class JobDeliveryRecordController {

    @Autowired
    private JobDeliveryRecordService jobDeliveryRecordService;

    @Autowired
    private JobService jobService;


    /**
     * 增加投递记录
     * @param jobDeliveryRecord
     * @return
     */
    @PostMapping("/addJobDeliveryRecord")
    public T addJobDeliveryRecord(@RequestBody JobDeliveryRecord jobDeliveryRecord) {
        String userId = jobDeliveryRecord.getUserId();
        String jobId = jobDeliveryRecord.getJobId();
        LambdaQueryWrapper<JobDeliveryRecord> jobDeliveryRecordVoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isEmpty(jobId)){
            jobDeliveryRecordVoLambdaQueryWrapper.eq(JobDeliveryRecord::getJobId , jobDeliveryRecord);
        }

        if(StringUtils.isEmpty(userId)){
            jobDeliveryRecordVoLambdaQueryWrapper.eq(JobDeliveryRecord::getUserId , userId);
        }
        JobDeliveryRecord one = jobDeliveryRecordService.getOne(jobDeliveryRecordVoLambdaQueryWrapper);
        if(one != null){
            return T.ok().message("已经投递");
        }
        jobDeliveryRecordService.save(jobDeliveryRecord);
        return T.ok();
    }


    /**
     * 删除投递信息
     * @param
     * @return
     */
    @PostMapping("/deleteJobDeliveryRecord")
    public T deleteJobDeliveryRecord(@RequestBody JobDeliveryRecordVo jobDeliveryRecord) {

        String jobId = jobDeliveryRecord.getJobId();
        String userId = jobDeliveryRecord.getUserId();

        LambdaQueryWrapper<JobDeliveryRecord> jobDeliveryRecordVoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isEmpty(jobId)){
            jobDeliveryRecordVoLambdaQueryWrapper.eq(JobDeliveryRecord::getJobId , jobDeliveryRecord);
        }

        if(StringUtils.isEmpty(userId)){
            jobDeliveryRecordVoLambdaQueryWrapper.eq(JobDeliveryRecord::getUserId , userId);
        }
        jobDeliveryRecordService.remove(jobDeliveryRecordVoLambdaQueryWrapper);
        return T.ok();
    }

    /**
     * 根据用户的id 获取用户投递记录和工作的信息
     * @param userId
     * @return
     */
    @GetMapping("getJobDeliveryRecord/{userId}")
    public T getJobDeliveryRecord(@PathVariable String userId){

        LambdaQueryWrapper<JobDeliveryRecord> jobDeliveryRecordVoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        jobDeliveryRecordVoLambdaQueryWrapper.eq(JobDeliveryRecord::getUserId , userId);
        List<JobDeliveryRecord> jobDeliveryRecords = jobDeliveryRecordService.list(jobDeliveryRecordVoLambdaQueryWrapper);
        ArrayList<JobDeliveryRecordVo> jobDeliveryRecordVos = new ArrayList<>();

        for(JobDeliveryRecord jobDeliveryRecord : jobDeliveryRecords){
            String jobId = jobDeliveryRecord.getJobId();
            Job jobInfo = jobService.getById(jobId);
            JobDeliveryRecordVo jobDeliveryRecordVo = new JobDeliveryRecordVo();
            BeanUtils.copyProperties(jobInfo , jobDeliveryRecordVo);
            jobDeliveryRecordVo.setCreateTime(jobDeliveryRecord.getCreateTime());
            jobDeliveryRecordVo.setUserId(jobDeliveryRecord.getUserId());
            jobDeliveryRecordVos.add(jobDeliveryRecordVo);

        }
        return T.ok().data("jobDeliveryRecords" , jobDeliveryRecordVos);
    }
}

