package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lins
 */
@Getter
@Setter
public class RsInvAdjustReq {

    private String appId;

    private String wrhCode;

    private String wrhId;

    List<RsShopSkuInv> lines;
}
