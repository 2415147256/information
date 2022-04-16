package com.lzh.serviceuser.service.impl;

import com.lzh.serviceuser.entity.TbUser;
import com.lzh.serviceuser.mapper.TbUserMapper;
import com.lzh.serviceuser.service.TbUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户 服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-04-11
 */
@Service
public class TbUserServiceImpl extends ServiceImpl<TbUserMapper, TbUser> implements TbUserService {

}
