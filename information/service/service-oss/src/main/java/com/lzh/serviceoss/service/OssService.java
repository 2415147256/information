package com.lzh.serviceoss.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 卢正豪
 * @version 1.0
 */
public interface OssService {

    /**
     * 采用阿里云  用于头像图片的存储
     * @param file
     * @return
     */
    String uploadFileAvatar(MultipartFile file);
}
