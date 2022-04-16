package com.lzh.servicerent.mapper;

import com.lzh.servicerent.entity.RentCondition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzh
 * @since 2022-03-31
 */
@Mapper
public interface RentConditionMapper extends BaseMapper<RentCondition> {

    /**
     * 根据类型查询要求的类型
     * @param s
     * @return
     */
    List<RentCondition> listBytype(String s);
}
