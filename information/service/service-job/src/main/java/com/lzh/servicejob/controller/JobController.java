package com.lzh.servicejob.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.commonutils.T;
import com.lzh.servicejob.entity.JobCompany;
import com.lzh.servicejob.entity.Job;
import com.lzh.servicejob.entity.JobTechnology;
import com.lzh.servicejob.entity.vo.JobFrontVo;
import com.lzh.servicejob.entity.vo.JobVo;
import com.lzh.servicejob.service.CompanyService;
import com.lzh.servicejob.service.JobService;
import com.lzh.servicejob.service.JobTechnologyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-23
 */
@RestController
@RequestMapping("/service-job/job")
@CrossOrigin

public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private JobTechnologyService jobTechnologyService;

    @PostMapping("getJobPageInfo/{page}/{limit}")
    public T getJobInfoCondition(@PathVariable long limit, @PathVariable long page, @RequestBody(required = false) JobVo jobVo) {

        ArrayList<String> jobTypes = new ArrayList<>();
        Page<Job> jobPage = new Page<>(page, limit);
        String education = jobVo.getEducation();
        String peopleNum = jobVo.getPeopleNum();
        String jobType = jobVo.getJobType();
        String expersion = jobVo.getExpersion();
        String pay = jobVo.getPay();
        String sgin = jobVo.getSgin();

        if (jobType != null) {
            if ("全部".equals(jobType)) {
                jobType = null;
            }
        }
        if (jobType != null) {
            if (jobType.contains("/")) {
                String[] split = jobType.split("/");
                for (String str : split) {
                    jobTypes.add(str);
                }
            }
        }

        LambdaQueryWrapper<Job> jobLambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (!StringUtils.isEmpty(sgin)) {
            jobLambdaQueryWrapper.eq(Job::getSign, sgin);
        }
        if (!StringUtils.isEmpty(education)) {
            jobLambdaQueryWrapper.eq(Job::getEducation, education);
        }
        if (!StringUtils.isEmpty(jobType) && jobTypes.size() == 0) {
            jobLambdaQueryWrapper.eq(Job::getJobType, jobType);
        }
        if (jobTypes.size() > 0) {
            for (String str : jobTypes) {
                jobLambdaQueryWrapper.eq(Job::getJobType, str);
            }
        }
        if (!StringUtils.isEmpty(expersion)) {
            jobLambdaQueryWrapper.eq(Job::getExpersion, expersion);
        }
        if (!StringUtils.isEmpty(pay)) {
            jobLambdaQueryWrapper.eq(Job::getPay, pay);
        }
        jobService.page(jobPage, jobLambdaQueryWrapper);
        long total = jobPage.getTotal();
        List<Job> records = jobPage.getRecords();
        List<JobFrontVo> jobFrontVos = new ArrayList<>();
        for (Job jobs : records) {
            JobFrontVo jobFrontVo = new JobFrontVo();
            BeanUtils.copyProperties(jobs, jobFrontVo);
            String companyID = jobs.getCompanyID();
            // 获取发布工作的公司
            JobCompany jobCompany = companyService.getById(companyID);
            if (!StringUtils.isEmpty(peopleNum)) {
                if (jobCompany.getPeopleNum().contains(peopleNum)) {
                    BeanUtils.copyProperties(jobCompany, jobFrontVo);
                }
            } else {
                BeanUtils.copyProperties(jobCompany, jobFrontVo);
            }
            String jobId = jobFrontVo.getJobId();
            LambdaQueryWrapper<JobTechnology> jobTechnologyLambdaQueryWrapper = new LambdaQueryWrapper<>();
            jobTechnologyLambdaQueryWrapper.eq(JobTechnology::getJobId, jobId);
            List<JobTechnology> list = jobTechnologyService.list(jobTechnologyLambdaQueryWrapper);
            jobFrontVo.setJobTechnologies(list);
            jobFrontVos.add(jobFrontVo);
        }
        return T.ok().data("total", total).data("records", jobFrontVos);
    }


    /**
     * 根据id获取job的详情信息  评论表
     *
     * @return
     */
    @GetMapping("getJobDetailedInfo/{jobId}")
    public T getJobDetailedInfo(@PathVariable String jobId) {
        Job job = jobService.getById(jobId);
        JobFrontVo jobFrontVo = new JobFrontVo();
        if(job != null){
            String companyID = job.getCompanyID();
            JobCompany jobCompany = companyService.getById(companyID);
            BeanUtils.copyProperties(job, jobFrontVo);
            BeanUtils.copyProperties(jobCompany, jobFrontVo);
            String companyProfile = jobFrontVo.getCorporateWelfare();
            String[] corporateWelfare = companyProfile.split(",");
            return T.ok().data("jobFrontVo", jobFrontVo).data("corporateWelfare",corporateWelfare);
        }else {
            return T.error().message("没有投递记录");
        }

    }
}

