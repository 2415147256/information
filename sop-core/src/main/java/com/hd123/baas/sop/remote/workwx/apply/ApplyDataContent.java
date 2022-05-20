
package com.hd123.baas.sop.remote.workwx.apply;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplyDataContent {

    // value ="控件类型"
    private String control;
    // value = "控件ID"
    private String id;
    // value = "控件值,需要根据控件类型确定参数"
    private ApplyFormControl value;

    private List<TemplateText> title;

}