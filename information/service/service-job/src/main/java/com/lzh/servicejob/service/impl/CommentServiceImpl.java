package com.lzh.servicejob.service.impl;

import com.lzh.servicejob.entity.Comment;
import com.lzh.servicejob.mapper.CommentMapper;
import com.lzh.servicejob.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzh
 * @since 2022-03-29
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
