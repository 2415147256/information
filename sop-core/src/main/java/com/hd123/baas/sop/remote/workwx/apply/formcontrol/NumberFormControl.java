package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

@Data
public class NumberFormControl implements ApplyFormControl {

    @JsonProperty("new_number")
    private String number;

    @Override
    public String formControlName() {
        return "Number";
    }
}
