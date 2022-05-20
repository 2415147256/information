package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class DateFormControlData {

    private String type;

    @JsonProperty("s_timestamp")
    private String timestamp;
}
