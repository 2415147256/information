package com.lzh.servicejob.service.impl;

import com.lzh.servicejob.entity.Job;
import com.lzh.servicejob.mapper.JobMapper;
import com.lzh.servicejob.service.JobService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-25
 */
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {

}
