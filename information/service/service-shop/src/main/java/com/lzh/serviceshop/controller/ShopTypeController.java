package com.lzh.serviceshop.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.serviceshop.entity.ShopType;
import com.lzh.serviceshop.entity.vo.ShopTwoTypeVo;
import com.lzh.serviceshop.entity.vo.ShopTypeVo;
import com.lzh.serviceshop.service.ShopTypeService;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-04-05
 */
@RestController
@RequestMapping("/service-shop/shop-type")
@CrossOrigin
public class ShopTypeController {

    @Autowired
    private ShopTypeService shopTypeService;

    @GetMapping("getAllShopType")
    public T getAllType(){

        LambdaQueryWrapper<ShopType> shopTypeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopTypeLambdaQueryWrapper.eq(ShopType::getParentId , "0");
        List<ShopType> oneList = shopTypeService.list(shopTypeLambdaQueryWrapper);
        ArrayList<ShopTypeVo> shopTypeVos = new ArrayList<>();
        for(ShopType shop : oneList){
            ShopTypeVo shopTypeVo = new ShopTypeVo();
            BeanUtils.copyProperties(shop , shopTypeVo);
            String shopId = shopTypeVo.getId();
            shopTypeLambdaQueryWrapper.eq(ShopType::getParentId, shopId);
            List<ShopType> shopTypes = shopTypeService.list(shopTypeLambdaQueryWrapper);
            List<ShopTwoTypeVo> shopTwoTypeVos = new ArrayList<>();
            BeanUtils.copyProperties(shopTypes , shopTwoTypeVos);
            shopTwoTypeVos.addAll(shopTwoTypeVos);
            shopTypeVo.setChildren(shopTwoTypeVos);
            shopTypeVos.add(shopTypeVo);
        }
        return T.ok().data("shopTypeVos" ,shopTypeVos );
    }

}

