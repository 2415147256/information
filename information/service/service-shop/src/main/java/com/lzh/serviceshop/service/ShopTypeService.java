package com.lzh.serviceshop.service;

import com.lzh.serviceshop.entity.ShopType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzh
 * @since 2022-04-05
 */
public interface ShopTypeService extends IService<ShopType> {

    /**
     * 获取所有的一级分类
     * @return
     */
    List<ShopType> getAllOneSubject();

    /**
     * 根据一级id 获取二级分类
     * @param shopId
     * @return
     */
    List<ShopType> getTwoSubject(String shopId);
}
