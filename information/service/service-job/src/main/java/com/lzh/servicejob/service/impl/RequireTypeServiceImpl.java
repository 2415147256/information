package com.lzh.servicejob.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.servicejob.entity.JobRequireType;
import com.lzh.servicejob.entity.vo.OneSubjectVo;
import com.lzh.servicejob.entity.vo.TwoSubjectVo;
import com.lzh.servicejob.mapper.RequireTypeMapper;
import com.lzh.servicejob.service.RequireTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-20
 */
@Service
public class RequireTypeServiceImpl extends ServiceImpl<RequireTypeMapper, JobRequireType> implements RequireTypeService {

    @Resource
    private RequireTypeMapper requireTypeMapper;


    @Override
    public List<OneSubjectVo> searchSubject() {

        LambdaQueryWrapper<JobRequireType> requireTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        requireTypeLambdaQueryWrapper.eq(JobRequireType::getParentId,"0");

        List<JobRequireType> jobRequireTypes = requireTypeMapper.selectList(requireTypeLambdaQueryWrapper);
        List<OneSubjectVo> oneSubjectVos = new ArrayList<>();

        for(JobRequireType jobRequireType : jobRequireTypes){

            OneSubjectVo oneSubjectVo = new OneSubjectVo();

            BeanUtils.copyProperties(jobRequireType, oneSubjectVo);
            String id = jobRequireType.getId();
            List<JobRequireType> twoSubject = requireTypeMapper.selectByParentId(id);
            ArrayList<TwoSubjectVo> twoSubjectVos = new ArrayList<>();
            for(JobRequireType jobRequireType1 : twoSubject){
                TwoSubjectVo twoSubjectVo = new TwoSubjectVo();
                BeanUtils.copyProperties(jobRequireType1, twoSubjectVo);
                twoSubjectVos.add(twoSubjectVo);
            }
            oneSubjectVo.setChildren(twoSubjectVos);
            oneSubjectVos.add(oneSubjectVo);
        }

        return oneSubjectVos;
    }
}
