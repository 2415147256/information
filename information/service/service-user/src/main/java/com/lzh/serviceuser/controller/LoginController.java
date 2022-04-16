package com.lzh.serviceuser.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.lzh.commonutils.T;
import org.springframework.web.bind.annotation.*;

/**
 * @author 卢正豪
 * @version 1.0
 */
@RestController
@RequestMapping("serviceUser/user")
@CrossOrigin

public class LoginController {

    @PostMapping("/login")
    public T login(){
        return T.ok().data("token" , "admin");
    }

    @GetMapping("/info")
    public T info(){
        return T.ok().data("roles","[admin]").data("name","admin").data("avatar","https://scpic.chinaz.net/files/pic/pic9/202009/apic27858.jpg");
    }



}
