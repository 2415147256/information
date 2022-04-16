package com.lzh.servicejob.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author 卢正豪
 * @version 1.0
 * 公司规模等
 */
@Data
public class OneSubjectVo {

    private String id;
    private String parentId;
    private String embellish;

    private List<TwoSubjectVo> children;
}
