package com.lzh.servicerent.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.servicerent.entity.RentRequire;
import com.lzh.servicerent.service.RentRequireService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-04-04
 */
@RestController
@RequestMapping("/service-rent/rent-require")
@CrossOrigin
public class RentRequireController {

    @Autowired
    private RentRequireService rentRequireService;


    /**
     * 根据id获取房东要求的基本信息
     * @param houseId
     * @return
     */
    @GetMapping("getRentRequireInfo/{houseId}")
    public T getRentRequireByHouseId(@PathVariable String houseId){

        RentRequire rentRequire = rentRequireService.getById(houseId);
        LambdaQueryWrapper<RentRequire> rentRequireLambdaQueryWrapper = new LambdaQueryWrapper<>();
        rentRequireLambdaQueryWrapper.eq(RentRequire::getHouseId , houseId);
        RentRequire one = rentRequireService.getOne(rentRequireLambdaQueryWrapper);

        return T.ok().data("rentRequire" , one);
    }

}

