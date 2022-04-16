package com.lzh.servicejob.service.impl;

import com.lzh.servicejob.entity.JobCompany;
import com.lzh.servicejob.mapper.CompanyMapper;
import com.lzh.servicejob.service.CompanyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-27
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, JobCompany> implements CompanyService {

}
