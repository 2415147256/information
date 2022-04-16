package com.lzh.serviceoss.controller;

import com.lzh.commonutils.T;
import com.lzh.serviceoss.service.OssService;
import com.lzh.serviceoss.service.impl.OssServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

/**
 * @author 卢正豪
 * @version 1.0
 */
@RestController
@RequestMapping("/service-oss/fileOss")
@CrossOrigin

public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping() // 上传头像的方法
    public T sentOssFile(MultipartFile file) throws FileNotFoundException {

        ossService = new OssServiceImpl();
        String url = ossService.uploadFileAvatar(file);
        return T.ok().data("url", url);

    }
}
