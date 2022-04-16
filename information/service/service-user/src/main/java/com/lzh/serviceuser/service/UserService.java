package com.lzh.serviceuser.service;

import com.lzh.serviceuser.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lzh.serviceuser.entity.frontVo.RegisterVo;
import com.lzh.serviceuser.entity.vo.LoginVo;
import com.lzh.serviceuser.entity.vo.UserVo;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lzh
 * @since 2022-03-09
 */
@Service
public interface UserService extends IService<User> {

    /**
     * 根据id获取用户数据
     * @param userId
     * @return
     */
    User getUser(String userId);

    /**
     * 删除用户的信息  修改其状态
     * @param userId
     * @return
     */
    int deleteUser(String userId);

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    User login(LoginVo loginVo);

    /**
     * 保存用户信息
     * @param user
     */
    String saveToken(User user);

    /**
     * 获取redis中的token值
     * @return
     */
    String getToken();

    /**
     * 根据token 获取用户信息
     * @param token
     * @return
     */
    User getUserInfoByToken(String token);

    /**
     * 根据id 获取用户信息
     * @param openid
     * @return
     */
    User getOpenIdMember(String openid);

    /**
     *  前台用户注册
     * @param registerVo
     */
    void register(RegisterVo registerVo);
}
