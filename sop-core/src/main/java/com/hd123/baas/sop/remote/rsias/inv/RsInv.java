package com.hd123.baas.sop.remote.rsias.inv;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lins
 */
@Getter
@Setter
public class RsInv {

    private String appId;

    private String appName;

    private Date lastModified;

    private BigDecimal lockQty;

    private BigDecimal qty;

    private BigDecimal availableQty;

    private String skuBarcode;

    private String skuCode;

    private String skuId;

    private String skuName;

    private String wrhCode;

    private String wrhName;

    private String wrhId;
}
