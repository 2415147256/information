package com.lzh.servicejob.mapper;

import com.lzh.servicejob.entity.JobRequireType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzh
 * @since 2022-03-20
 */
@Mapper
public interface RequireTypeMapper extends BaseMapper<JobRequireType> {

    /**
     * 找出所有的一级分类和二级分类
     * @param id
     * @return
     */
    List<JobRequireType> selectByParentId(String id);
}
