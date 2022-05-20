package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

/**
 * 描述 单选/多选控件
 **/
@Data
public class SelectorFormControl implements ApplyFormControl {

    private SelectorData selector;

    @Override
    public String formControlName() {
        return "Selector";
    }
}
