package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 描述
 **/
@Data
public class ContactDepartment {

    @JsonProperty("openapi_id")
    private Integer openApiId;

    private String name;

}
