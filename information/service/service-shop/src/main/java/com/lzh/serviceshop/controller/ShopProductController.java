package com.lzh.serviceshop.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.commonutils.T;
import com.lzh.servicebase.entity.User;
import com.lzh.serviceshop.entity.ShopMx;
import com.lzh.serviceshop.entity.ShopProduct;
import com.lzh.serviceshop.entity.ShopStore;
import com.lzh.serviceshop.entity.vo.ShopConditionVo;
import com.lzh.serviceshop.service.ShopMxService;
import com.lzh.serviceshop.service.ShopProductService;
import com.lzh.serviceshop.service.ShopStoreService;
import com.lzh.serviceshop.user.UcenterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/service-shop/shop-product")
@CrossOrigin
public class ShopProductController {

    @Autowired
    private ShopProductService shopProductService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private ShopMxService shopMxService;

    @Autowired
    private UcenterClient ucenterClient;


    @PostMapping("condition/{page}/{limit}")
    public T getConditionShop(@PathVariable long page , @PathVariable long limit, @RequestBody(required = false)  ShopConditionVo shopConditionVo){

        Page<ShopProduct> shopProductPage = new Page<>(page,limit);
        LambdaQueryWrapper<ShopProduct> shopProductLambdaQueryWrapper = new LambdaQueryWrapper<>();

        String productType = shopConditionVo.getProductType();
        String productTwotype = shopConditionVo.getProductTwotype();
        Integer productNum = shopConditionVo.getProductNum();
        Double productPrice = shopConditionVo.getProductPrice();

        if(!StringUtils.isEmpty(productTwotype)){
            shopProductLambdaQueryWrapper.eq(ShopProduct::getProductTwotype, productTwotype);
        }
        if(!StringUtils.isEmpty(productType)){
            shopProductLambdaQueryWrapper.eq(ShopProduct::getProductType, productType);
        }
        if(!StringUtils.isEmpty(productNum)){
            shopProductLambdaQueryWrapper.orderByDesc(ShopProduct::getProductNum);
        }
        if(!StringUtils.isEmpty(productPrice)){
            shopProductLambdaQueryWrapper.orderByDesc(ShopProduct::getProductPrice);
        }

        shopProductService.page(shopProductPage , shopProductLambdaQueryWrapper);
        long total = shopProductPage.getTotal();
        List<ShopProduct> records = shopProductPage.getRecords();
        return T.ok().data("total" , total).data("records" , records);
    }

    /**
     * 根据id获取商品的信息
     * @param shopId
     * @return
     */
    @GetMapping("getShopById/{shopId}")
    public T getShopInfoById(@PathVariable String shopId){

        ShopProduct shopInfo = shopProductService.getById(shopId);
        String storeId = shopInfo.getStoreId();
        // 获取商店的信息
        LambdaQueryWrapper<ShopStore> shopStoreLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shopStoreLambdaQueryWrapper.eq(ShopStore::getStoreId , storeId);
        ShopStore shopStore = shopStoreService.getOne(shopStoreLambdaQueryWrapper);
        // 获取商品明细的信息
        ShopMx shopMx = shopMxService.getById(shopId);
        String productUserid = shopInfo.getProductUserid();
        User userInfo = ucenterClient.getUserInfo(productUserid);
        return T.ok().data("shopInfo",shopInfo).data("shopStore" , shopStore).data("shopMx",shopMx).data("userInfo" , userInfo);

    }

    /**
     * 查询前四条热门的内容
     * @return
     */
    @GetMapping("getHotShopInfo")
    public T getHotShopInfo(){
        LambdaQueryWrapper<ShopProduct> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.orderByDesc(ShopProduct::getId);
        LambdaQueryWrapper.last("limit  4");
        List<ShopProduct> shopProducts= shopProductService.list(LambdaQueryWrapper);
        return T.ok().data("shopProducts" , shopProducts);
    }

}

