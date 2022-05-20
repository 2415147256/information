/*
 * Copyright (c) 2021. Shanghai HEADING information engineering Co., Ltd. All rights reserved.
 */

package com.hd123.baas.sop.remote.rsmas2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author BinLee
 */
@Getter
@Setter
public class RsFindRequest {
    private Integer start = 0;
    private Integer limit = 500;
    @JsonInclude
    private Map<String, Object> filters = new HashMap<>();
    private List<String> fetchParts = new ArrayList<>();
    private LinkedHashMap<String, String> sorters = new LinkedHashMap<>();
}
