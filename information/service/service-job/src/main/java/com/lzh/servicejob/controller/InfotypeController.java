package com.lzh.servicejob.controller;
import com.lzh.commonutils.T;
import com.lzh.servicejob.entity.JobInfotype;
import com.lzh.servicejob.service.InfotypeService;
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
 * @since 2022-03-19
 */
@RestController
@RequestMapping("/service-job/infoType")
@CrossOrigin

public class InfotypeController {

    @Autowired
    private InfotypeService infotypeService;

    /**
     * 获取所有的职位分类信息
     * @return
     */
    @GetMapping("getAllTypeInfo")
    public T  getAllType(){
        List<JobInfotype> list = infotypeService.list(null);
        return T.ok().data("list",list);
    }


}

