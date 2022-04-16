package com.lzh.servicejob.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzh.commonutils.T;
import com.lzh.servicejob.entity.Comment;
import com.lzh.servicejob.entity.vo.CommentVo;
import com.lzh.servicejob.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-29
 */
@RestController
@RequestMapping("/service-job/comment")
@CrossOrigin

public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 增加评论
     * @param commentVo
     * @return
     */
    @PostMapping("addComment")
    public T saveComment(@RequestBody CommentVo commentVo){
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentVo , comment);
        commentService.save(comment);
        return T.ok();
    }



    @GetMapping("selectAllInfo/{typeId}")
    public T selectAllInfo(@PathVariable String typeId){
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getTypeId,typeId);
        //commentLambdaQueryWrapper.eq(Comment::getParentId,"0");

        List<Comment> list = commentService.list(commentLambdaQueryWrapper);
        List<CommentVo> commentVos = new ArrayList<>();
        for(Comment Message : list){
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(Message , commentVo);
            commentVos.add(commentVo);
        }
        for (CommentVo message : commentVos) {
            String parentId = message.getParentId();
            // 判断当前的留言是否有父级，如果有，则返回父级留言的信息
            // 原理：遍历所有留言数据，如果id跟当前留言信息的parentId相等，则将其设置为父级评论信息，也就是Message::setParentMessage
            commentVos.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(message::setParentComment);
        }
        return T.ok().data("list" , commentVos);
    }

    @PostMapping("deleteComment/{commentId}")
    public T deleteCommentInfo(@PathVariable String commentId){
        boolean b = commentService.removeById(commentId);
        if(b){
            return T.ok();
        }else {
            return T.error().message("删除出错");
        }
    }
}

