package com.hd123.baas.sop.remote.workwx.apply.formcontrol;

import lombok.Data;

import java.util.List;

/**
 * 描述
 **/
@Data
public class SelectorData {

    private String type;

    private List<SelectorOptionData> options;
}
