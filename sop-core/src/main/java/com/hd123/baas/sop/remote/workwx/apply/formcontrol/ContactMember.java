package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 描述
 **/
@Data
public class ContactMember {

    private String name;

    @JsonProperty("userid")
    private String userId;

}
