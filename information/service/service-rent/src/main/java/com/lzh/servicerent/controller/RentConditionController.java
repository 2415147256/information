package com.lzh.servicerent.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.servicerent.entity.RentCondition;
import com.lzh.servicerent.service.RentConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-31
 */
@RestController
@RequestMapping("/service-rent/rent-condition")
@CrossOrigin
public class RentConditionController {

    @Autowired
    private RentConditionService rentConditionService;

    @GetMapping("getAllRentInfo")
    public T rentCondition(){
        List<RentCondition> rentMoney = rentConditionService.selectByType("0");
        List<RentCondition> rentRoom = rentConditionService.selectByType("1");
        return T.ok().data("rentMoney" , rentMoney).data("rentRoom" , rentRoom);
    }
}

