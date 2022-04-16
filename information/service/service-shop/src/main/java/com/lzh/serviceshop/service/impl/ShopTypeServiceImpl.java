package com.lzh.serviceshop.service.impl;

import com.lzh.serviceshop.entity.ShopType;
import com.lzh.serviceshop.mapper.ShopTypeMapper;
import com.lzh.serviceshop.service.ShopTypeService;
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
 * @since 2022-04-05
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements ShopTypeService {

    @Resource
    private ShopTypeMapper shopTypeMapper;

    @Override
    public List<ShopType> getAllOneSubject() {
        List<ShopType> shopTypes = shopTypeMapper.getAllOneSubjet();
        return shopTypes;
    }

    @Override
    public List<ShopType> getTwoSubject(String shopId) {
        List<ShopType> twoList = shopTypeMapper.getAllTwoSubject(shopId);
        return twoList;
    }
}
