package com.lzh.servicerent.user;


import com.atguigu.commonutils.vo.UcenterMemberOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author 卢正豪
 * @version 1.0
 */
@Component
@FeignClient(name = "service-ucenter",fallback = com.atguigu.serviceEdu.client.ucenter.UcenterClientImpl.class)
public interface UcenterClient {
    //根据用户id获取用户信息
    @GetMapping("/serviceUcneter/ucenter-member/getInfoUc/{id}")
    public UcenterMemberOrder getUcenterPay(@PathVariable("memberId") String memberId);
}
