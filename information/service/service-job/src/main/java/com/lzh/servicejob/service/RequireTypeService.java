package com.lzh.servicejob.service;

import com.lzh.servicejob.entity.JobRequireType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.servicejob.entity.vo.OneSubjectVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzh
 * @since 2022-03-20
 */
public interface RequireTypeService extends IService<JobRequireType> {

    /**
     * 获取一级分类和其子类
     * @return
     */
    List<OneSubjectVo> searchSubject();
}
