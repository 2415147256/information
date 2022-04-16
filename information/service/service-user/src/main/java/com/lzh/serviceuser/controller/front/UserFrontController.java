package com.lzh.serviceuser.controller.front;

import com.lzh.commonutils.T;
import com.lzh.serviceuser.entity.frontVo.RegisterVo;
import com.lzh.serviceuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.spi.RegisterableService;

/**
 * @author 卢正豪
 * @version 1.0
 */
@RestController
@RequestMapping("serviceUser/front")
@CrossOrigin
public class UserFrontController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public T register(@RequestBody RegisterVo registerVo){

        userService.register(registerVo);
        return T.ok();
    }
}
