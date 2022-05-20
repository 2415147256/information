package com.hd123.baas.sop.service.api.basedata.department;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DepartmentConfig {
    private String scope;
    private List<String> depts;
}
