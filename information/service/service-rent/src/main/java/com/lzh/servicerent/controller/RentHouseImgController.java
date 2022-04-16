package com.lzh.servicerent.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.servicerent.entity.RentHouseImg;
import com.lzh.servicerent.service.RentHouseImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-04-04
 */
@RestController
@RequestMapping("/service-rent/rent-house-img")
@CrossOrigin
public class RentHouseImgController {

    @Autowired
    private RentHouseImgService rentHouseImgService;

    // 根据id获取出租房子的照片信息
    @GetMapping("getRentImage/{houseId}")
    public T getRentHouseImgs(@PathVariable String houseId){
        LambdaQueryWrapper<RentHouseImg> rentHouseImgLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rentHouseImgLambdaQueryWrapper.eq(RentHouseImg::getHouseId , houseId);
        List<RentHouseImg> list = rentHouseImgService.list(rentHouseImgLambdaQueryWrapper);
        return T.ok().data("list" , list);
    }

    /**
     * 查询前台页面
     * @return
     */
    @GetMapping("getFrontImag")
    public T getFrontImag(){
        List<RentHouseImg> list = rentHouseImgService.list(null);
        ArrayList<RentHouseImg> rentHouseImgs = new ArrayList<>();
        for(RentHouseImg rentHouseImg : list){
            if(StringUtils.isEmpty(rentHouseImg.getHouseId())){
               rentHouseImgs.add(rentHouseImg);
            }
        }
        return T.ok().data("imags" , rentHouseImgs);
    }

}

