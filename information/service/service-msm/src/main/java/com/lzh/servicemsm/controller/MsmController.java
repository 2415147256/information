package com.lzh.servicemsm.controller;


import com.lzh.commonutils.T;
import com.lzh.servicemsm.service.MsmService;
import com.lzh.servicemsm.utils.RandomUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 */
@RestController
@RequestMapping("/service-msm/msm")
@CrossOrigin

public class MsmController {
    @Resource
    private MsmService msmService;
    @Resource
    private RedisTemplate<String,String> redisTemplate;

    //发送短信的方法
    @GetMapping("send/{phone}")
    public T send(@PathVariable("phone") String phone) throws IOException {
        //1.先从redis中取验证码，如果获取到直接返回
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)){
            return T.ok();
        }

        //2.如果获取不到，则进行阿里云发送
        //生成随机值，传递给阿里云进行发送
        code = RandomUtil.getFourBitRandom();
        Map<String,Object> param = new HashMap<>();
        param.put("code",code);
        //调用service发送短信的方法
        boolean isSend = msmService.send(param,phone);
        if (isSend){
            //发送成功，将验证码保存到redis中,同时设置有效时间
            redisTemplate.opsForValue().set(phone,code,10, TimeUnit.MINUTES);
            return T.ok();
        } else {
            return T.error().message("短信发送失败");
        }
    }
}
