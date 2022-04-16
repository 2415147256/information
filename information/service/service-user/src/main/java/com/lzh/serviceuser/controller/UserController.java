package com.lzh.serviceuser.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzh.commonutils.JwtUtils;
import com.lzh.commonutils.MD5;
import com.lzh.commonutils.T;
import com.lzh.servicebase.exception.GuLiException;
import com.lzh.serviceuser.entity.User;
import com.lzh.serviceuser.entity.vo.LoginVo;
import com.lzh.serviceuser.entity.vo.UserVo;
import com.lzh.serviceuser.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-09
 */

@RestController
@RequestMapping("/serviceUser/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根据id获取用户数据
     * @param userId
     * @return
     */
    @GetMapping("getUserInfo/{userId}")
    public T getUserInfo(@PathVariable String userId){
        User userInfo = userService.getUser(userId);
        if(userInfo != null){
            return T.ok().data("userInfo" , userInfo);
        }else {
            return T.ok().message("没有此用户信息");
        }
    }

    /**
     * 删除用户的信息  修改用户的状态
     * @param userId
     * @return
     */
    @DeleteMapping("deleteUserInfo/{userId}")
    public T removeUserInfo(@PathVariable String userId){
        int i = userService.deleteUser(userId);
        if(i == 1){
            return T.ok();
        }else {
            return T.error().message("删除失败");
        }
    }

    /**
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("updateUserInfo")
    public T updateUserInfo( @RequestBody User user){
        boolean b = userService.updateById(user);
        if(b){
            return T.ok().message("修改用户信息成功");
        }else {
            return T.error().message("修改用户信息失败");
        }
    }

    /**
     * 增加用户信息
     * @param user
     * @return
     */
    @PutMapping("addUser")
    public T addUserInfo(@RequestBody User user){
        String password = user.getPassword();
        user.setPassword(MD5.encrypt(password));
        // 没注销状态码为 0  注销的状态码为 1
        user.setIsDelete(0);
        boolean save = userService.save(user);
        if(save){
            return T.ok().message("保存用户信息成功");
        }else {
            return T.error().message("保存用户信息失败");
        }
    }

    /**
     * 用户注册
     * @param userVo
     * @return
     */
    @PutMapping("/register")
    public T registerUser(@RequestBody UserVo userVo){
        String password = userVo.getPassword();
        User user = new User();
        BeanUtils.copyProperties(userVo , user);
        user.setPassword(MD5.encrypt(password));
        boolean save = userService.save(user);
        if(save){
            return T.ok().message("用户注册成功");
        }else {
            return T.error().message("保存用户注册失败");
        }
    }

    /**
     * 用户登录
     * @param loginVo
     * @return
     */
    @PostMapping("front/login")
    public T loginUser(@RequestBody LoginVo loginVo){
        User user = userService.login(loginVo);
        Integer isDelete = user.getIsDelete();
        if(isDelete == 1){
            throw new GuLiException(20001 , "该账号已注销");
        }
        String token = userService.saveToken(user);
        return T.ok().data("token" , token);
    }

    /**
     * 根据 token 获取用户的基本信息
     * @param request
     * @return
     */
    @GetMapping("/getUserByToken")
    public T getMembet(HttpServletRequest request){
        //  调用jwt工具类获取用户的id
        String memberID = JwtUtils.getMemberIdByJwtToken(request);
        // 查询数据库 根据id来查询用户的数据
        User user = userService.getById(memberID);
        return T.ok().data("userInfo" , user);

    }


    /**
     * 获取所有的用户信息
     * @return
     */
    @GetMapping("getAllUser")
    public T getAllUserInfo(){
        List<User> list = userService.list(null);
        return T.ok().data("items" , list);
    }

    /**
     * 用户信息分页功能
     * @param limit
     * @param page
     *
     * @return
     */
    @GetMapping("getUserInfo/{page}/{limit}")
    public T  pageUser(@PathVariable long limit, @PathVariable long page){

        Page<User> userPage = new Page<>(page, limit);
        userService.page(userPage , null);
        // 获取所有的用户信息
        List<User> records = userPage.getRecords();
        // 获取总记录数
        long total = userPage.getTotal();
        return T.ok().data("total" , total).data("row" , records);

    }

    /**
     * 实现条件分页功能
     * @param limit
     * @param page
     * @param userQuery
     * @return
     */
    @PostMapping("conditionsUser/{page}/{limit}")
    public T pageUserList(@PathVariable long page , @PathVariable long limit,  @RequestBody(required = false) UserVo userQuery){
        Page<User> userPage = new Page<>(page,limit);
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(userQuery != null){
            Date createTime = userQuery.getCreateTime();
            Date modifyTime = userQuery.getModifyTime();
            String telephone = userQuery.getTelephone();
            Integer isDelete = userQuery.getIsDelete();
            if(!StringUtils.isEmpty(createTime)){
                userLambdaQueryWrapper.ge(User::getCreateTime, createTime);
            }
            if(!StringUtils.isEmpty(modifyTime)){
                userLambdaQueryWrapper.le(User::getModifyTime, modifyTime);
            }
            if(!StringUtils.isEmpty(telephone)){
                userLambdaQueryWrapper.like(User::getTelephone, telephone);
            }
            if(!StringUtils.isEmpty(isDelete)){
                userLambdaQueryWrapper.eq(User::getIsDelete, isDelete);
            }
        }
        // 排序的方法
        userService.page(userPage , userLambdaQueryWrapper);
        long total = userPage.getTotal();
        List<User> records = userPage.getRecords();
        return T.ok().data("total" , total).data("row" , records);
    }

    /**
     * 根据token 获取用户的基本信息
     *
     * @return
     */
    @GetMapping("/CurrentlyLogged")
    public T getUserByToken(){
        String token = userService.getToken();
        User userInfoByToken = userService.getUserInfoByToken(token);
        return T.ok().data("userInfoByToken" , userInfoByToken);
    }


    @GetMapping("getUserInfoById/{id}")
    public User getUserInfoById(@PathVariable String id){
        User byId = userService.getById(id);
       return byId;
    }


}

