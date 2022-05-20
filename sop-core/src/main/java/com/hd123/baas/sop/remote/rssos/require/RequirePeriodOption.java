package com.hd123.baas.sop.remote.rssos.require;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(description = "要货期间选项配置")
@Getter
@Setter
public class RequirePeriodOption {
    private String startTime;
    private String endTime;
    private Integer offsetDays;
    private List<String> excludeStores;
}
