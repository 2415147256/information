package com.lzh.servicejob.controller;


import com.lzh.commonutils.T;
import com.lzh.servicejob.entity.vo.OneSubjectVo;
import com.lzh.servicejob.service.RequireTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author lzh
 * @since 2022-03-20
 */
@RestController
@RequestMapping("/service-job/require-type")
@CrossOrigin
public class RequireTypeController {

    @Autowired
    private RequireTypeService requireTypeService;

    @GetMapping("/getAllOneSubject")
    public T getOneSubject(){
         List<OneSubjectVo> oneList= requireTypeService.searchSubject();
        return T.ok().data("oneList",oneList);
    }
}

