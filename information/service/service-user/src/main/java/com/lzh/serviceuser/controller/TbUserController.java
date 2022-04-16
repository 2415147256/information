package com.lzh.serviceuser.controller;


import com.lzh.serviceuser.entity.TbUser;
import com.lzh.serviceuser.entity.User;
import com.lzh.serviceuser.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户 前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-04-11
 */
@RestController
@RequestMapping("/serviceUser/tb-user")
@CrossOrigin

public class TbUserController {

    @Autowired
    private TbUserService tbUserService;


    @GetMapping("getUserInfoById/{id}")
    public TbUser getUserInfoById(@PathVariable String id){
        TbUser byId = tbUserService.getById(id);
        return byId;
    }

}

