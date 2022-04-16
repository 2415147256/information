package com.lzh.serviceuser.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.JwtUtils;
import com.lzh.commonutils.MD5;
import com.lzh.servicebase.exception.GuLiException;
import com.lzh.serviceuser.entity.User;
import com.lzh.serviceuser.entity.frontVo.RegisterVo;
import com.lzh.serviceuser.entity.vo.LoginVo;
import com.lzh.serviceuser.mapper.UserMapper;
import com.lzh.serviceuser.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lzh.commonutils.JwtUtils.APP_SECRET;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public User getUser(String userId) {
        User user = userMapper.selectById(userId);
        // 1表示一级删除  0 表示刚刚注册
       return user;

    }

    @Override
    public int deleteUser(String userId) {
        User user = getUser(userId);
        if(user != null){
            user.setIsDelete(1);
            int updateById = userMapper.updateById(user);
            return updateById;
        }else{
            return 0;
        }
    }

    @Override
    public User login(LoginVo loginVo) {

        String telephone = loginVo.getTelephone();
        String password = loginVo.getPassword();
        if(StringUtils.isEmpty(telephone) || StringUtils.isEmpty(password)){
            throw new GuLiException(20001,"登录失败");
        }

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getTelephone , telephone);
        userLambdaQueryWrapper.eq(User::getPassword , MD5.encrypt(password) );
        User user = userMapper.selectOne(userLambdaQueryWrapper);
        if(StringUtils.isEmpty(user)){
            throw new GuLiException(20001,"手机号或密码错误");
        }
        return user;
    }

    @Override
    public String saveToken(User user) {
        String jwtToken =  JwtUtils.getJwtToken(user.getId() , user.getName());
        // 将登入的用户信息村存入redis中 并且设置过期时间
        redisTemplate.opsForValue().set("token", jwtToken,5, TimeUnit.MINUTES);
        return jwtToken;
    }

    @Override
    public String getToken() {

        String token = redisTemplate.opsForValue().get("token");
        return token;
    }

    @Override
    public User getUserInfoByToken(String token) {

        boolean b = JwtUtils.checkToken(token);
        String id = null;
        if(b){
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(token);
            Claims claims = claimsJws.getBody();
            id  = (String) claims.get("id");
        }
        User user = userMapper.selectById(id);

        return user;
    }

    @Override
    public User getOpenIdMember(String openid) {
        LambdaQueryWrapper<User> ucenterMemberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ucenterMemberLambdaQueryWrapper.eq(User::getOpenid , openid);
        User user = baseMapper.selectOne(ucenterMemberLambdaQueryWrapper);
        return user;
    }

    @Override
    public void register(RegisterVo registerVo) {
        String nickname = registerVo.getNickname();
        String telephone = registerVo.getTelephone();
        String code = registerVo.getCode();
        String password = registerVo.getPassword();

        if(StringUtils.isEmpty(nickname) || StringUtils.isEmpty(telephone) || StringUtils.isEmpty(code) || StringUtils.isEmpty(password)){
            throw new GuLiException(20001,"注册失败");
        }
        //判断验证码
        // 获取Redis验证码
        String redisCode = redisTemplate.opsForValue().get(telephone);

        if(redisCode == null){
            throw new GuLiException(20001,"验证码过期");
        }
        if(!code.equals(redisCode)){
            throw new GuLiException(20001,"验证码错误");
        }

        // 判读手机号是否重复
        LambdaQueryWrapper<User> registerVoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        registerVoLambdaQueryWrapper.eq(User::getTelephone, telephone);
        User member = userMapper.selectOne(registerVoLambdaQueryWrapper);
        if( !StringUtils.isEmpty(member) ){
           if(member.getIsDelete() == 0){
               throw new GuLiException(20001, "注册失败");
           }
        }

        // 数据添加
        User member1 = new User();
        member1.setTelephone(telephone);
        // 密码进行加密
        member1.setPassword(MD5.encrypt(password));
        member1.setUsername(nickname);
        member1.setIsDelete(0);
        member1.setAvatar("https://online-teach-file.oss-cn-beijing.aliyuncs.com/teacher/2019/10/30/de47ee9b-7fec-43c5-8173-13c5f7f689b2.png");
        userMapper.insert(member1);
    }
}
