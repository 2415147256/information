package com.lzh.serviceorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.serviceorder.entity.ShopOrderMx;
import com.lzh.serviceorder.service.ShopOrderMxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
@RequestMapping("/service-order/shop-order-mx")
@CrossOrigin
public class ShopOrderMxController {

    @Autowired(required = true)
    private ShopOrderMxService shopOrderMxService;

    /**
     * 创建订单信息
     * @param shopOrderMx
     * @return
     */
    @PostMapping("addOrderMxInfo")
    public T addOrderMxInfo(@RequestBody ShopOrderMx shopOrderMx){

        shopOrderMx.setIsPay(0);
        String shopPrice = shopOrderMx.getShopPrice();
        String shopNum = shopOrderMx.getShopNum();
        if(shopNum.equals("") || StringUtils.isEmpty(shopNum)){
            return T.ok().message("请输入想要购买的数量");
        }
        BigDecimal bigDecimal = new BigDecimal(Integer.valueOf(shopPrice) * Integer.valueOf(shopNum));
        shopOrderMx.setShopAllPrice(bigDecimal);
        shopOrderMxService.save(shopOrderMx);
        String id = shopOrderMx.getId();
        return T.ok().data("shopMxId" , id);
    }

    /**
     * 根据id 获取订单的信息
     * @param mxId
     * @return
     */
    @GetMapping("getShopMxInfoById/{mxId}")
    public T  getOrderMxInfoById(@PathVariable String mxId){
        ShopOrderMx shopOrderMx = shopOrderMxService.getById(mxId);
        return T.ok().data("shopOrderMx",shopOrderMx);
    }

    /**
     * 根据用户的id获取订单的信息
     * @param userId
     * @return
     */
    @GetMapping("getShopInfo/{userId}")
    public T getShopInfoByUserId(@PathVariable String userId){
        LambdaQueryWrapper<ShopOrderMx> shopOrderMxLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopOrderMxLambdaQueryWrapper.eq(ShopOrderMx::getShopUserId , userId);
        List<ShopOrderMx> list = shopOrderMxService.list(shopOrderMxLambdaQueryWrapper);
        return T.ok().data("shopOrders" , list);
    }

    /**
     * 根据id删除 订单表的信息
     * @param id
     * @return
     */
    @GetMapping("deleteShopInfo/{id}")
    public T  deleteShopInfoById(@PathVariable String id){
        shopOrderMxService.removeById(id);
        return T.ok();
    }
}

