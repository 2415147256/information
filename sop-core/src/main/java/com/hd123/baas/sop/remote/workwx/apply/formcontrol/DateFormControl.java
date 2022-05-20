package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DateFormControl implements ApplyFormControl {

    private DateFormControlData date;

    @Override
    public String formControlName() {
        return "Date";
    }
}
