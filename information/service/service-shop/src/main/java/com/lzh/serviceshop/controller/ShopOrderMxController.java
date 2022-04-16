package com.lzh.serviceshop.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.serviceshop.entity.ShopOrderMx;
import com.lzh.serviceshop.mapper.ShopOrderMxMapper;
import com.lzh.serviceshop.service.ShopOrderMxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
@RequestMapping("/service-shop/shop-order-mx")
public class ShopOrderMxController {

    @Autowired
    private ShopOrderMxService shopOrderMxService;

    /**
     * 增加订单的信息
     * @param shopOrderMx
     * @return
     */
    @PostMapping("addOrderMxInfo")
    public T addOrderMxInfo(@RequestBody ShopOrderMx shopOrderMx){

        shopOrderMx.setIsPay(0);
        String shopPrice = shopOrderMx.getShopPrice();
        String shopNum = shopOrderMx.getShopNum();
        BigDecimal bigDecimal = new BigDecimal(Integer.valueOf(shopPrice) * Integer.valueOf(shopNum));
        shopOrderMx.setShopAllPrice(bigDecimal);
        shopOrderMxService.save(shopOrderMx);
        String id = shopOrderMx.getId();
        return T.ok().data("shopMxId" , id);
    }
}

