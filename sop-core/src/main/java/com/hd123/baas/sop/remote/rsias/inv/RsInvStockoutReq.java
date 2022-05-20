package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lins
 */
@Getter
@Setter
public class RsInvStockoutReq {

    private String skuId;

    private String skuCode;

    private String wrhCode;

    private String wrhId;
}
