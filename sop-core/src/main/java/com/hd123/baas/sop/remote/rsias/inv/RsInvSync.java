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
public class RsInvSync {

    private String gid;
    private Date lastSynced;
    private BigDecimal qpc;
    private BigDecimal qty;
    private String skuId;
    private String skuCode;
    private String wrhCode;
    private String wrhId;

}
