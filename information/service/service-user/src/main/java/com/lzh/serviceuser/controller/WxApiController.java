package com.lzh.serviceuser.controller;


import com.google.gson.Gson;
import com.lzh.commonutils.JwtUtils;
import com.lzh.servicebase.exception.GuLiException;
import com.lzh.serviceuser.entity.User;
import com.lzh.serviceuser.service.UserService;
import com.lzh.serviceuser.utils.ConstantWxUtils;
import com.lzh.serviceuser.utils.HttpClientUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.HashMap;

@Controller
@RequestMapping("/api/ucenter/wx")
@CrossOrigin

public class WxApiController {

    @Resource
    private UserService userService;

    //2.获取扫描人信息，添加数据
    @GetMapping("/callback")
    public String callback(String code,String state){
        try {
            //1.先获取到code值,临时票据
            //2.拿着code请求微信的固定地址，得到两个值 openid 和 access_token
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";
            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code
            );
            //请求这个拼接好的地址，得到返回俩个值    openid 和 access_token
            //使用httpclient发送请求，得到返回结果
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            //从accessTokenInfo中取出俩个值    openid 和 access_token
            //把accessTokenInfo字符串转换map集合，根据map里面key获取对应值
            Gson gson = new Gson();
            HashMap map = new HashMap();
            HashMap hashMap = gson.fromJson(accessTokenInfo, HashMap.class);
            String openid = (String) hashMap.get("openid");
            String access_token = (String) hashMap.get("access_token");

            //把扫码人信息添加到数据库中
            //判断数据表里面是否存在相同微信信息
            User user = userService.getOpenIdMember(openid);
            if (user == null){
                //3.拿着access_token和openid，在去请求微信提供的固定地址,获取扫描人信息
                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(
                        baseUserInfoUrl,
                        access_token,
                        openid
                );
                //发送请求
                String userInfo = HttpClientUtils.get(userInfoUrl);
                //获取返回userInfo字符串扫描人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                //昵称
                String nickname = (String) userInfoMap.get("nickname");
                //头像
                String headimgurl = (String) userInfoMap.get("headimgurl");
                user = new User();
                user.setOpenid(openid);
                user.setUsername(nickname);
                user.setAvatar(headimgurl);
                user.setIsDelete(0);
                userService.save(user);
            }

            //使用jwt根据member对象生成token字符串
            String token = JwtUtils.getJwtToken(user.getId(), user.getName());

            //最后：返回首页面，通路径传递token字符串
            return "redirect:http://localhost:3000?token="+token;

        } catch (Exception e){
            e.printStackTrace();
            throw new GuLiException(20001,"登录失败");
        }
    }


    //1.生成微信扫描二维码
    @GetMapping("/login")
    public String getWxCode(){
        //请求微信地址
        //微信平台开发授权baseUrl，%s相当于？代表占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //对redirect_url进行URLEncoder编码
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL;

        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }

        //设置%s里面的值
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirectUrl,
                "atguigu"
        );
        return "redirect:"+url;
    }

}
