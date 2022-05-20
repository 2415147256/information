package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

/**
 * 描述 文本/多行文本控件
 **/
@Data
public class TextFormControl implements ApplyFormControl {

    private String text;

    @Override
    public String formControlName() {
        return "Text";
    }
}
