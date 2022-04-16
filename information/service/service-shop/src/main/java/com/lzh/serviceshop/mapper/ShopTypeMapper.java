package com.lzh.serviceshop.mapper;

import com.lzh.serviceshop.entity.ShopType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lzh
 * @since 2022-04-05
 */
@Mapper
public interface ShopTypeMapper extends BaseMapper<ShopType> {

    /**
     * 获取所有的一级分类
     * @param
     * @return
     */
    List<ShopType> getAllOneSubjet();

    /**
     * 获取所有的二级分类
     * @param shopId
     * @return
     */
    List<ShopType> getAllTwoSubject(String shopId);
}
