package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FileData {

    @JsonProperty("file_id")
    private String fileId;
}
