package com.lzh.servicerent.service.impl;

import com.lzh.servicerent.entity.RentCondition;
import com.lzh.servicerent.mapper.RentConditionMapper;
import com.lzh.servicerent.service.RentConditionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-31
 */
@Service
public class RentConditionServiceImpl extends ServiceImpl<RentConditionMapper, RentCondition> implements RentConditionService {

    @Resource
    public RentConditionMapper rentConditionMapper;

    @Override
    public List<RentCondition> selectByType(String s) {
        List<RentCondition> list = rentConditionMapper.listBytype(s);
        return list;
    }
}
