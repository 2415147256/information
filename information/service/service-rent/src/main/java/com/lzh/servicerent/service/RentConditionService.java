package com.lzh.servicerent.service;

import com.lzh.servicerent.entity.RentCondition;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzh
 * @since 2022-03-31
 */
public interface RentConditionService extends IService<RentCondition> {


    /**
     * 查询
     * @param s
     * @return
     */
    List<RentCondition> selectByType(String s);
}
