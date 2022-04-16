package com.lzh.serviceshop.user;


import com.lzh.servicebase.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Component
@FeignClient(name = "user",fallback =UcenterClientImpl.class )
public interface UcenterClient {
    //根据用户id获取用户信息

    /**
     * as
     * @param id
     * @return
     */
    @GetMapping("/serviceUser/tb-user/getUserInfoById/{id}")
    public User getUserInfo(@PathVariable("id") String id);


}
