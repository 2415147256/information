package com.hd123.baas.sop.remote.workwx.apply.formcontrol;


import com.hd123.baas.sop.remote.workwx.apply.TemplateText;
import lombok.Data;

import java.util.List;

@Data
public class SelectorOptionData {
    private String key;
    private List<TemplateText> value;

}
