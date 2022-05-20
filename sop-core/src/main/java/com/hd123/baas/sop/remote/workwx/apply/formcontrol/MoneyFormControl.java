package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hd123.baas.sop.remote.workwx.apply.ApplyFormControl;
import lombok.Data;

@Data
public class MoneyFormControl implements ApplyFormControl {

    @JsonProperty("new_money")
    private String money;

    @Override
    public String formControlName() {
        return "Money";
    }
}
