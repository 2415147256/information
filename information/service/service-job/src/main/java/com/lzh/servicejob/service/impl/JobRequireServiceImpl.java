package com.lzh.servicejob.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzh.servicejob.entity.JobRequire;
import com.lzh.servicejob.mapper.JobRequireMapper;
import com.lzh.servicejob.mapper.RequireTypeMapper;
import com.lzh.servicejob.service.JobRequireService;
import org.springframework.stereotype.Service;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Service
public class JobRequireServiceImpl extends ServiceImpl<JobRequireMapper, JobRequire> implements JobRequireService {
}
